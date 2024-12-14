package dev.kelompokceria.smartumkm.user.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dev.kelompokceria.smartumkm.user.data.database.AppDatabase
import dev.kelompokceria.smartumkm.user.data.repository.ProductRepository
import dev.kelompokceria.smartumkm.user.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProductViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ProductRepository

    // LiveData untuk menyimpan posisi yang dipilih
    private val _selectedPositions = MutableLiveData<Set<Product>>(emptySet())
    val selectedPositions: LiveData<Set<Product>> get() = _selectedPositions

    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> get() = _products

    init {
        val productDao = AppDatabase.getInstance(application).productDao()
        repository = ProductRepository(productDao)
        refreshProducts()
        refreshDeleteProducts()
        products
        getAllProduct()
    }

    fun getAllProduct() {
        viewModelScope.launch {
            _products.postValue(repository.getAllProducts())
        }
    }

    fun getProductByCategory(category: String)  {
        viewModelScope.launch {
            _products.postValue(repository.getAllProducts().filter { it.category == category })
        }
    }

    fun refreshProducts() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Ambil produk dari API
                val apiProducts = repository.getProductsFromApi()

                // Ambil produk yang ada di Room
                val existingProducts = repository.getAllProducts()

                // Tambahkan produk baru yang tidak ada di database
                val newProducts = apiProducts.filter { apiProduct ->
                    existingProducts.none { existingProduct ->
                        existingProduct.id == apiProduct.id
                    }
                }
                // Simpan produk baru ke database
                repository.insertAll(newProducts)

                val updatedProducts = repository.getAllProducts()
                _products.postValue(updatedProducts) // Gunakan postValue untuk mengupdate LiveData dari background thread
            } catch (e: Exception) {
                Log.e("ProductViewModel", "Error refreshing products", e)
            }
        }
    }

    fun refreshDeleteProducts() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Ambil produk dari API
                val apiProducts = repository.getProductsFromApi()

                // Ambil produk yang ada di Room
                val existingProducts = repository.getAllProducts()

                // Tambahkan produk baru yang tidak ada di database
                val recentProducts = existingProducts.filter { roomProducts ->
                    apiProducts.none { apiProducts ->
                        apiProducts.id == roomProducts.id
                    }
                }
                // Simpan produk baru ke database
                repository.deleteAll(recentProducts)

                val updatedProducts = repository.getAllProducts()
                _products.postValue(updatedProducts) // Gunakan postValue untuk mengupdate LiveData dari background thread
            } catch (e: Exception) {
                Log.e("ProductViewModel", "Error refreshing products", e)
            }
        }
    }

    fun getProductsByIds(ids: List<Int>): LiveData<List<Product?>> {
        if (ids.isEmpty()) {
            return MutableLiveData(emptyList()) // Mengembalikan LiveData kosong jika ID kosong
        }
        return repository.getProductByIds(ids)
    }

//     // Tambahkan atau hapus posisi dari daftar seleksi
    fun toggleSelection(position: Product) {
        val currentSelections = _selectedPositions.value?.toMutableSet() ?: mutableSetOf()
        if (currentSelections.contains(position)) {
            currentSelections.remove(position)
        } else {
            currentSelections.add(position)
        }
        _selectedPositions.postValue(currentSelections)
    }

    // Set ulang semua seleksi
    fun clearSelections() {
        _selectedPositions.value = emptySet()
    }

}
