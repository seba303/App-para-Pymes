package com.seba303.appparapymes.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.seba303.appparapymes.databinding.ItemSaleBinding
import com.seba303.appparapymes.models.Sale
import java.text.SimpleDateFormat
import java.util.Locale

class SaleAdapter : ListAdapter<Sale, SaleAdapter.SaleViewHolder>(DiffCallback) {

    private val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    private val outputFormat = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SaleViewHolder {
        val binding = ItemSaleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SaleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SaleViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SaleViewHolder(private val binding: ItemSaleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(sale: Sale) {
            binding.tvSaleProduct.text = sale.productName
            binding.tvSaleCustomer.text = if (sale.customerName.isNotBlank()) sale.customerName else "—"
            binding.tvSaleQuantity.text = "x${sale.quantity}"
            binding.tvSaleDate.text = formatDisplayDate(sale.date)
            binding.tvSaleTotal.text = String.format("$%.2f", sale.total)
        }

        private fun formatDisplayDate(date: String): String {
            return try {
                val parsed = inputFormat.parse(date)
                if (parsed != null) outputFormat.format(parsed) else date
            } catch (e: Exception) {
                date
            }
        }
    }

    private companion object DiffCallback : DiffUtil.ItemCallback<Sale>() {
        override fun areItemsTheSame(oldItem: Sale, newItem: Sale) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Sale, newItem: Sale) = oldItem == newItem
    }
}
