package com.ram.weatherdetails.utils

/*Getting the key from c++ file   */
object Keys {
    init {
        System.loadLibrary("native-lib")
    }

    external fun apiKey(): String
}