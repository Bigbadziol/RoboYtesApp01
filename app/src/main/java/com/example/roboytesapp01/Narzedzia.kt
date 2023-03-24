package com.example.roboytesapp01

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException

class Narzedzia {
}
/**
 * Sprawdz czy podany string jest poprawnie sformatowanym obiektem Json
 */
fun czytoJson(incomingData: String?): Boolean {
    val gson = Gson()
    if (incomingData.isNullOrBlank()) return false
    return try {
        gson.fromJson(incomingData, Any::class.java)
        val jsonObjType: Any = gson.fromJson(incomingData, Any::class.java).javaClass
        if (jsonObjType == String::class.java) {
            false
        } else true
    } catch (ex: JsonSyntaxException) {
        false
    }
}