package dev.kelompokceria.smartumkm.user.data.api


import dev.kelompokceria.smartumkm.user.model.Transaction
import dev.kelompokceria.smartumkm.user.model.Product
import dev.kelompokceria.smartumkm.user.model.ProductCategory
import dev.kelompokceria.smartumkm.user.model.User

data class ProductApiResponse(
    val status: Boolean,
    val message: String,
    val data: List<Product>
)


data class ProductCategoryApiResponse(
    val status: Boolean,
    val message: String,
    val data: List<ProductCategory>
)

data class UpdateProductCategoryResponse(
    val status: Boolean,
    val message: String,
    val data: ProductCategory?
)

data class UpdateUserResponse(
    val status: Boolean,
    val message: String,
    val data: User?
)

data class LoginResponse(
     val status: Boolean,
    val message: String,
    val data: User?
)

data class TransactionApiResponse(
    val status: Boolean,
    val message: String,
    val data: List<Transaction>
)

data class UpdateTransactionResponse(
    val status: Boolean,
    val message: String,
    val data: Transaction?
)




