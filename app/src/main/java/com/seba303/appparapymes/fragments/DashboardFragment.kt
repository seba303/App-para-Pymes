package com.seba303.appparapymes.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.seba303.appparapymes.database.DatabaseHelper
import com.seba303.appparapymes.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dbHelper = DatabaseHelper(requireContext())
    }

    override fun onResume() {
        super.onResume()
        loadStats()
    }

    private fun loadStats() {
        binding.tvTotalProducts.text = dbHelper.getProductCount().toString()
        binding.tvSalesToday.text = dbHelper.getTodaySalesCount().toString()
        binding.tvRevenueToday.text = String.format("$%.2f", dbHelper.getTodayRevenue())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
