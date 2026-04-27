package com.seba303.appparapymes

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.seba303.appparapymes.databinding.ActivityMainBinding
import com.seba303.appparapymes.fragments.DashboardFragment
import com.seba303.appparapymes.fragments.ProductsFragment
import com.seba303.appparapymes.fragments.SalesFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            loadFragment(DashboardFragment(), TAG_DASHBOARD)
        }

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_dashboard -> { loadFragment(DashboardFragment(), TAG_DASHBOARD); true }
                R.id.navigation_products  -> { loadFragment(ProductsFragment(), TAG_PRODUCTS); true }
                R.id.navigation_sales     -> { loadFragment(SalesFragment(), TAG_SALES); true }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment, tag: String) {
        val existing = supportFragmentManager.findFragmentByTag(tag)
        if (existing != null && existing.isAdded) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, existing, tag)
                .commit()
        } else {
            supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, fragment, tag)
                .commit()
        }
    }

    companion object {
        private const val TAG_DASHBOARD = "dashboard"
        private const val TAG_PRODUCTS = "products"
        private const val TAG_SALES = "sales"
    }
}
