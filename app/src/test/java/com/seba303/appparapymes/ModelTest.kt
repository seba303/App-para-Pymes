package com.seba303.appparapymes

import com.seba303.appparapymes.models.Product
import com.seba303.appparapymes.models.Sale
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class ModelTest {

    @Test
    fun product_defaultIdIsZero() {
        val product = Product(name = "Producto A", price = 100.0, stock = 10)
        assertEquals(0L, product.id)
    }

    @Test
    fun product_copyUpdatesFields() {
        val product = Product(id = 1, name = "Producto A", price = 100.0, stock = 10)
        val updated = product.copy(name = "Producto B", price = 200.0)
        assertEquals("Producto B", updated.name)
        assertEquals(200.0, updated.price, 0.001)
        assertEquals(1L, updated.id)
    }

    @Test
    fun sale_totalCalculation() {
        val product = Product(id = 1, name = "Producto A", price = 50.0, stock = 20)
        val quantity = 3
        val total = quantity * product.price
        val sale = Sale(
            productId = product.id,
            productName = product.name,
            quantity = quantity,
            total = total,
            date = "2024-01-01 10:00:00"
        )
        assertEquals(150.0, sale.total, 0.001)
    }

    @Test
    fun product_equalityByValue() {
        val p1 = Product(id = 1, name = "A", price = 10.0, stock = 5)
        val p2 = Product(id = 1, name = "A", price = 10.0, stock = 5)
        val p3 = Product(id = 2, name = "A", price = 10.0, stock = 5)
        assertEquals(p1, p2)
        assertNotEquals(p1, p3)
    }
}
