package com.seba303.appparapymes.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.seba303.appparapymes.models.Product
import com.seba303.appparapymes.models.Sale
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "pymes.db"
        private const val DATABASE_VERSION = 1

        // Products table
        const val TABLE_PRODUCTS = "products"
        const val COL_PRODUCT_ID = "_id"
        const val COL_PRODUCT_NAME = "name"
        const val COL_PRODUCT_PRICE = "price"
        const val COL_PRODUCT_STOCK = "stock"

        // Sales table
        const val TABLE_SALES = "sales"
        const val COL_SALE_ID = "_id"
        const val COL_SALE_PRODUCT_ID = "product_id"
        const val COL_SALE_PRODUCT_NAME = "product_name"
        const val COL_SALE_QUANTITY = "quantity"
        const val COL_SALE_TOTAL = "total"
        const val COL_SALE_DATE = "date"
        const val COL_SALE_CUSTOMER = "customer_name"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLE_PRODUCTS (
                $COL_PRODUCT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_PRODUCT_NAME TEXT NOT NULL,
                $COL_PRODUCT_PRICE REAL NOT NULL,
                $COL_PRODUCT_STOCK INTEGER NOT NULL DEFAULT 0
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE $TABLE_SALES (
                $COL_SALE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_SALE_PRODUCT_ID INTEGER NOT NULL,
                $COL_SALE_PRODUCT_NAME TEXT NOT NULL,
                $COL_SALE_QUANTITY INTEGER NOT NULL,
                $COL_SALE_TOTAL REAL NOT NULL,
                $COL_SALE_DATE TEXT NOT NULL,
                $COL_SALE_CUSTOMER TEXT DEFAULT '',
                FOREIGN KEY ($COL_SALE_PRODUCT_ID) REFERENCES $TABLE_PRODUCTS ($COL_PRODUCT_ID)
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SALES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PRODUCTS")
        onCreate(db)
    }

    // ── Product operations ──────────────────────────────────────────────────────

    fun insertProduct(product: Product): Long {
        val values = ContentValues().apply {
            put(COL_PRODUCT_NAME, product.name)
            put(COL_PRODUCT_PRICE, product.price)
            put(COL_PRODUCT_STOCK, product.stock)
        }
        return writableDatabase.insert(TABLE_PRODUCTS, null, values)
    }

    fun getAllProducts(): List<Product> {
        val products = mutableListOf<Product>()
        val cursor = readableDatabase.query(
            TABLE_PRODUCTS, null, null, null, null, null,
            "$COL_PRODUCT_NAME ASC"
        )
        cursor.use {
            while (it.moveToNext()) {
                products.add(
                    Product(
                        id = it.getLong(it.getColumnIndexOrThrow(COL_PRODUCT_ID)),
                        name = it.getString(it.getColumnIndexOrThrow(COL_PRODUCT_NAME)),
                        price = it.getDouble(it.getColumnIndexOrThrow(COL_PRODUCT_PRICE)),
                        stock = it.getInt(it.getColumnIndexOrThrow(COL_PRODUCT_STOCK))
                    )
                )
            }
        }
        return products
    }

    fun updateProduct(product: Product): Int {
        val values = ContentValues().apply {
            put(COL_PRODUCT_NAME, product.name)
            put(COL_PRODUCT_PRICE, product.price)
            put(COL_PRODUCT_STOCK, product.stock)
        }
        return writableDatabase.update(
            TABLE_PRODUCTS, values,
            "$COL_PRODUCT_ID = ?", arrayOf(product.id.toString())
        )
    }

    fun deleteProduct(productId: Long): Int {
        return writableDatabase.delete(
            TABLE_PRODUCTS,
            "$COL_PRODUCT_ID = ?", arrayOf(productId.toString())
        )
    }

    fun decreaseStock(productId: Long, quantity: Int) {
        writableDatabase.execSQL(
            "UPDATE $TABLE_PRODUCTS SET $COL_PRODUCT_STOCK = $COL_PRODUCT_STOCK - ?" +
                " WHERE $COL_PRODUCT_ID = ? AND $COL_PRODUCT_STOCK >= ?",
            arrayOf(quantity, productId, quantity)
        )
    }

    // ── Sale operations ─────────────────────────────────────────────────────────

    fun insertSale(sale: Sale): Long {
        val values = ContentValues().apply {
            put(COL_SALE_PRODUCT_ID, sale.productId)
            put(COL_SALE_PRODUCT_NAME, sale.productName)
            put(COL_SALE_QUANTITY, sale.quantity)
            put(COL_SALE_TOTAL, sale.total)
            put(COL_SALE_DATE, sale.date)
            put(COL_SALE_CUSTOMER, sale.customerName)
        }
        return writableDatabase.insert(TABLE_SALES, null, values)
    }

    fun getAllSales(): List<Sale> {
        val sales = mutableListOf<Sale>()
        val cursor = readableDatabase.query(
            TABLE_SALES, null, null, null, null, null,
            "$COL_SALE_DATE DESC"
        )
        cursor.use {
            while (it.moveToNext()) {
                sales.add(
                    Sale(
                        id = it.getLong(it.getColumnIndexOrThrow(COL_SALE_ID)),
                        productId = it.getLong(it.getColumnIndexOrThrow(COL_SALE_PRODUCT_ID)),
                        productName = it.getString(it.getColumnIndexOrThrow(COL_SALE_PRODUCT_NAME)),
                        quantity = it.getInt(it.getColumnIndexOrThrow(COL_SALE_QUANTITY)),
                        total = it.getDouble(it.getColumnIndexOrThrow(COL_SALE_TOTAL)),
                        date = it.getString(it.getColumnIndexOrThrow(COL_SALE_DATE)),
                        customerName = it.getString(it.getColumnIndexOrThrow(COL_SALE_CUSTOMER))
                    )
                )
            }
        }
        return sales
    }

    // ── Dashboard stats ─────────────────────────────────────────────────────────

    fun getProductCount(): Int {
        val cursor = readableDatabase.rawQuery("SELECT COUNT(*) FROM $TABLE_PRODUCTS", null)
        return cursor.use { if (it.moveToFirst()) it.getInt(0) else 0 }
    }

    fun getTodaySalesCount(): Int {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val cursor = readableDatabase.rawQuery(
            "SELECT COUNT(*) FROM $TABLE_SALES WHERE $COL_SALE_DATE LIKE ?",
            arrayOf("$today%")
        )
        return cursor.use { if (it.moveToFirst()) it.getInt(0) else 0 }
    }

    fun getTodayRevenue(): Double {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val cursor = readableDatabase.rawQuery(
            "SELECT COALESCE(SUM($COL_SALE_TOTAL), 0) FROM $TABLE_SALES WHERE $COL_SALE_DATE LIKE ?",
            arrayOf("$today%")
        )
        return cursor.use { if (it.moveToFirst()) it.getDouble(0) else 0.0 }
    }
}
