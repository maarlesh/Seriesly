package com.seriesly.core.common.ui

import com.seriesly.core.common.result.AppException

data class UiError(val title: String, val message: String)

fun AppException.toUiError(): UiError = when (this) {
    is AppException.NetworkException   -> UiError("No Connection", "Check your internet and try again")
    is AppException.ApiException       -> when (code) {
        404          -> UiError("Not Found", "This title couldn't be found")
        429          -> UiError("Slow Down", "Too many requests — please wait a moment")
        in 500..599  -> UiError("Server Error", "Something went wrong on our end")
        else         -> UiError("Error", "Something went wrong (code: $code)")
    }
    is AppException.AuthException      -> UiError("Auth Error", message ?: "Please try again")
    is AppException.RateLimitException -> UiError("Slow Down", "Too many requests")
    is AppException.ValidationException -> UiError("Invalid Input", message ?: "Please check your input")
    else                               -> UiError("Unexpected Error", "Please restart the app")
}
