package com.shoshin.common


data class ApiResponse<T> internal constructor(
    val data: T? = null,
    val isSuccess: Boolean = true,
    val message: String? = null,
    val error: Throwable? = null
) {
    companion object {
        fun <T> success(data: T): ApiResponse<T> {
            return ApiResponse(data)
        }

        fun failure(error: Throwable?): ApiResponse<*> {
            return ApiResponse(
                data = null,
                isSuccess = false,
                error = error
            )
        }
    }
}