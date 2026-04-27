package com.seba303.appparapymes.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.seba303.appparapymes.R
import com.seba303.appparapymes.adapters.ProductAdapter
import com.seba303.appparapymes.database.DatabaseHelper
import com.seba303.appparapymes.databinding.DialogProductBinding
import com.seba303.appparapymes.databinding.FragmentProductsBinding
import com.seba303.appparapymes.models.Product

class ProductsFragment : Fragment() {

    private var _binding: FragmentProductsBinding? = null
    private val binding get() = _binding!!

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var adapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dbHelper = DatabaseHelper(requireContext())

        adapter = ProductAdapter(
            onEdit = { product -> showProductDialog(product) },
            onDelete = { product -> confirmDelete(product) }
        )

        binding.recyclerProducts.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerProducts.adapter = adapter

        binding.fabAddProduct.setOnClickListener { showProductDialog(null) }

        loadProducts()
    }

    override fun onResume() {
        super.onResume()
        loadProducts()
    }

    private fun loadProducts() {
        val products = dbHelper.getAllProducts()
        adapter.submitList(products)
        binding.tvEmptyProducts.visibility = if (products.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun showProductDialog(existing: Product?) {
        val dialogBinding = DialogProductBinding.inflate(layoutInflater)
        existing?.let {
            dialogBinding.etProductName.setText(it.name)
            dialogBinding.etProductPrice.setText(it.price.toString())
            dialogBinding.etProductStock.setText(it.stock.toString())
        }

        val title = if (existing == null) R.string.add_product else R.string.edit_product
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setView(dialogBinding.root)
            .setPositiveButton(R.string.save) { _, _ ->
                val name = dialogBinding.etProductName.text?.toString()?.trim().orEmpty()
                val priceStr = dialogBinding.etProductPrice.text?.toString()?.trim().orEmpty()
                val stockStr = dialogBinding.etProductStock.text?.toString()?.trim().orEmpty()

                if (name.isBlank() || priceStr.isBlank() || stockStr.isBlank()) {
                    Snackbar.make(binding.root, R.string.error_fields_required, Snackbar.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val price = priceStr.toDoubleOrNull() ?: run {
                    Snackbar.make(binding.root, R.string.error_fields_required, Snackbar.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                val stock = stockStr.toIntOrNull() ?: run {
                    Snackbar.make(binding.root, R.string.error_fields_required, Snackbar.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                if (existing == null) {
                    dbHelper.insertProduct(Product(name = name, price = price, stock = stock))
                } else {
                    dbHelper.updateProduct(existing.copy(name = name, price = price, stock = stock))
                }
                loadProducts()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun confirmDelete(product: Product) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.delete_product)
            .setMessage(getString(R.string.product_confirm_delete, product.name))
            .setPositiveButton(R.string.delete) { _, _ ->
                dbHelper.deleteProduct(product.id)
                loadProducts()
                Snackbar.make(binding.root, R.string.product_deleted, Snackbar.LENGTH_SHORT).show()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
