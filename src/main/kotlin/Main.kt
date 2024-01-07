package org.example

import java.io.File
import java.io.InputStream
import java.nio.charset.Charset
import java.time.Year

data class CarsCSV (
    val Car_Name: String,
    val year: Year,
    val Selling_Price: Double,
    val Present_Price: Double,
    val Kms_Driven: Int,
    val Fuel_Type: String,

)

fun Any.toPrettyString(className : String, indentSize: Int = 2) = " ".repeat(indentSize).let { indent ->
    toString()
        .replace(", ", ",\n$indent")
        .replace("(", "(\n$indent")
        .replace("$className(", "\n$className(")
        .dropLast(1) + "\n)"
}

fun readFileAsTextUsingInputStream(fileName: String) = File(fileName).inputStream()

operator fun <T> List<T>.component6(): T = get(5)

fun readCsv(inputStream: InputStream): List<CarsCSV> {
    val reader = inputStream.bufferedReader()
    val header = reader.readLine()
    return reader.lineSequence()
        .filter { it.isNotBlank() }
        .map {
            val (Car_Name, year, Selling_Price, Present_Price, Kms_Driven, Fuel_Type) = it.split(',', ignoreCase =
            false, limit = 0)
            CarsCSV(Car_Name.trim().removeSurrounding("\""), Year.of(year.trim().toInt()), Selling_Price.trim()
                .toDouble(), Present_Price.trim().toDouble(),
                Kms_Driven.trim().toInt(), Fuel_Type.trim().removeSurrounding("\""))
        }.toList()
}


fun main() {
    val stream = readFileAsTextUsingInputStream("src/main/kotlin/cardekho_data.csv")
    val movies = readCsv(stream)
    println(movies.take(3).toPrettyString("CarsCSV"))
    println("Hello World!")
}