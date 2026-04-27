package com.seba303.appparapymes.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.seba303.appparapymes.R
import com.seba303.appparapymes.adapters.SaleAdapter
import com.seba303.appparapymes.database.DatabaseHelper
import com.seba303.appparapymes.databinding.DialogSaleBinding
import com.seba303.appparapymes.databinding.FragmentSalesBinding
import com.seba303.appparapymes.models.Product
import com.seba303.appparapymes.models.Sale
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SalesFragment : Fragment() {

    private var _binding: FragmentSalesBinding? = null
    private val binding get() = _binding!!

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var adapter: SaleAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSalesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dbHelper = DatabaseHelper(requireContext())

        adapter = SaleAdapter()
        binding.recyclerSales.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerSales.adapter = adapter

        binding.fabAddSale.setOnClickListener { showSaleDialog() }

        loadSales()
    }

    override fun onResume() {
        super.onResume()
        loadSales()
    }

    private fun loadSales() {
        val sales = dbHelper.getAllSales()
        adapter.submitList(sales)
        binding.tvEmptySales.visibility = if (sales.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun showSaleDialog() {
        val products = dbHelper.getAllProducts()
        if (products.isEmpty()) {
            Snackbar.make(binding.root, R.string.error_no_products, Snackbar.LENGTH_LONG).show()
            return
        }

        val dialogBinding = DialogSaleBinding.inflate(layoutInflater)

        // Set up product dropdown
        val productNames = products.map { it.name }
        val dropdownAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            productNames
        )
        dialogBinding.spinnerProduct.setAdapter(dropdownAdapter)

        var selectedProduct: Product? = null

        fun updateTotal() {
            val qty = dialogBinding.etQuantity.text?.toString()?.toIntOrNull() ?: 0
            val price = selectedProduct?.price ?: 0.0
            val total = qty * price
            dialogBinding.tvSaleTotalPreview.text = String.format("Total: $%.2f", total)
        }

        dialogBinding.spinnerProduct.setOnItemClickListener { _, _, position, _ ->
            selectedProduct = products[position]
            updateTotal()
        }

        dialogBinding.etQuantity.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
            override fun afterTextChanged(s: Editable?) { updateTotal() }
        })

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.register_sale)
            .setView(dialogBinding.root)
            .setPositiveButton(R.string.save) { _, _ ->
                val product = selectedProduct
                val quantityStr = dialogBinding.etQuantity.text?.toString()?.trim().orEmpty()
                val customerName = dialogBinding.etCustomerName.text?.toString()?.trim().orEmpty()

                if (product == null || quantityStr.isBlank()) {
                    Snackbar.make(binding.root, R.string.error_fields_required, Snackbar.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val quantity = quantityStr.toIntOrNull()
                if (quantity == null || quantity <= 0) {
                    Snackbar.make(binding.root, R.string.error_invalid_quantity, Snackbar.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                if (quantity > product.stock) {
                    val msg = getString(R.string.error_stock_insufficient, product.stock)
                    Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val total = quantity * product.price
                val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

                val sale = Sale(
                    productId = product.id,
                    productName = product.name,
                    quantity = quantity,
                    total = total,
                    date = date,
                    customerName = customerName
                )
                dbHelper.insertSale(sale)
                dbHelper.decreaseStock(product.id, quantity)

                loadSales()
                Snackbar.make(binding.root, R.string.sale_registered, Snackbar.LENGTH_SHORT).show()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
