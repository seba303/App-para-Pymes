# App para Pymes

Aplicación Android para la gestión de pequeñas y medianas empresas (PyMEs).

## Funcionalidades

### 📊 Dashboard (Inicio)
- Resumen del día: total de productos, ventas del día e ingresos del día.

### 📦 Gestión de Productos
- Listar todos los productos con nombre, precio y stock.
- Agregar nuevos productos.
- Editar productos existentes.
- Eliminar productos con confirmación.

### 🛒 Gestión de Ventas
- Registrar nuevas ventas (seleccionar producto, ingresar cantidad y cliente).
- Descuento automático del stock al registrar una venta.
- Validación de stock disponible antes de registrar la venta.
- Historial de ventas ordenado por fecha.

## Tecnologías

- **Lenguaje**: Kotlin
- **Base de datos**: SQLite (SQLiteOpenHelper)
- **UI**: Material Components, ViewBinding, RecyclerView
- **Arquitectura**: Single-Activity con Fragments y Bottom Navigation

## Requisitos

- Android Studio Hedgehog o superior
- SDK mínimo: Android 7.0 (API 24)
- SDK objetivo: Android 14 (API 34)
- JDK 17

## Compilación

```bash
./gradlew assembleDebug
```

## Pruebas unitarias

```bash
./gradlew test
```

## Estructura del proyecto

```
app/
└── src/
    └── main/
        ├── java/com/seba303/appparapymes/
        │   ├── MainActivity.kt
        │   ├── adapters/
        │   │   ├── ProductAdapter.kt
        │   │   └── SaleAdapter.kt
        │   ├── database/
        │   │   └── DatabaseHelper.kt
        │   ├── fragments/
        │   │   ├── DashboardFragment.kt
        │   │   ├── ProductsFragment.kt
        │   │   └── SalesFragment.kt
        │   └── models/
        │       ├── Product.kt
        │       └── Sale.kt
        └── res/
            ├── layout/         # Layouts de actividades, fragmentos y diálogos
            ├── menu/           # Menú de navegación inferior
            └── values/         # Strings, colores y temas
```
