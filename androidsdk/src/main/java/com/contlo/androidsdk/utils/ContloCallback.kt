package com.contlo.androidsdk.utils

interface ContloCallback {
    fun onSuccess()
    fun onError(e: Exception)
}