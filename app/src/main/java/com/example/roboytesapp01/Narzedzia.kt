package com.example.roboytesapp01

import android.graphics.Color
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException

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

/**
 * Konwertuj inta do tego typu obiektu : {"R":255,"G":128,"B":64}
 */
fun intToJsonColor(value: Int): JsonObject {
    val color = JsonObject()
    color.addProperty("R", Color.red(value))
    color.addProperty("G", Color.green(value))
    color.addProperty("B", Color.blue(value))
    return color
}

/**
 * Konwertuj obiekt typu {"R":255,"G":128,"B":64} na int
 */
fun jsonColorToInt(jsonColor: JsonObject): Int {
    var r = 0
    var g = 0
    var b = 0
    if (jsonColor.has("R")) r = jsonColor.get("R").asInt
    if (jsonColor.has("G")) g = jsonColor.get("G").asInt
    if (jsonColor.has("B")) b = jsonColor.get("B").asInt
    return Color.rgb(r, g, b)
}

fun booleanToInt(b: Boolean) = if (b) 1 else 0

fun intToBoolean(int: Int) = if (int == 1) true else false
