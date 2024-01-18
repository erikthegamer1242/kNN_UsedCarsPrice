package org.example

import java.io.File
import java.io.InputStream
import java.lang.Math.multiplyFull
import java.lang.Math.pow
import java.nio.charset.Charset
import java.time.Year
import kotlin.math.abs
import kotlin.math.*
import kotlinx.coroutines.*

data class CarsCSV (
    val Car_Name: String, //useless
    val year: Year,
    val Selling_Price: Double,//return
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

fun distance(knnList: List<CarsCSV>, Car_Name: String, year: Year, Selling_Price: Double, Present_Price: Double,
             Kms_Driven: Int, Fuel_Type: String, k: Int) : MutableList<Pair<Double, CarsCSV>> {
    var distanceList = mutableListOf<Pair<Double, CarsCSV>>()
    runBlocking {
        knnList.forEach {
            launch {
                if(it.Fuel_Type==Fuel_Type){
                    distanceList.add(Pair(((it.year.toString().toDouble() - year.toString().toDouble()).pow(2.0) +
                            ((it.Present_Price - Present_Price)/1.5).pow(2.0) + ((it.Selling_Price -
                            Selling_Price)*4.5).pow
                        (2.0)
                            + ((it.Kms_Driven.toDouble() - Kms_Driven.toDouble())/15000).pow(2.0)).pow(1 / 2.0),
                        it))
                }
            }
        }
    }
    return distanceList
}

fun knn(knnList: List<CarsCSV>, Car_Name: String, year: Year, Selling_Price: Double, Present_Price: Double,
        Kms_Driven: Int, Fuel_Type: String, k: Int):Double{
//uzima zapis, mjeri sve udaljenosti i sortira, ispis prvih k sortiranih
    //izracunat udaljenost od svake tocke
    var distance:Double=0.0
    var distanceList = distance(knnList, Car_Name, year, Selling_Price, Present_Price, Kms_Driven, Fuel_Type, k)

//    for(i in 0..300){
//        if(knnList[i].Fuel_Type==Fuel_Type){
//        distanceList.add(Pair(((knnList[i].year.toString().toDouble() - year.toString().toDouble()).pow(2.0) +
//        ((knnList[i].Present_Price - Present_Price)/1.5).pow(2.0) + ((knnList[i].Selling_Price - Selling_Price)*4.5).pow
//            (2.0)
//        + ((knnList[i].Kms_Driven.toDouble() - Kms_Driven.toDouble())/15000).pow(2.0)).pow(1 / 2.0), knnList[i]))
//        }
//        continue //ako nije isti tip goriva, preskace petlju
//    }

    //sortirat
    distanceList.sortBy { it.first }

    //odredit cijenu težinski
    var finalPrice=0
    var multiply=0.0
    var sum=0.0

    for(i in 0..k){
        multiply+=distanceList[i].second.Selling_Price*distanceList[i].first
        sum+=distanceList[i].first
    }

    //fajrunt
    val x:Double=multiply/sum
    return x
}


fun knnSplitter(knnList: List<CarsCSV>, row: Int = 0, k: Int):Double{
    return knn(knnList, knnList[row].Car_Name, knnList[row].year, knnList[row].Selling_Price, knnList[row]
        .Present_Price,
        knnList[row].Kms_Driven, knnList[row].Fuel_Type, k)
}

fun main() {
    val stream = readFileAsTextUsingInputStream("src/main/kotlin/cardekho_data.csv")
    val movies = readCsv(stream)
    var k=13
    if(k%2==0)
        k++
    println(movies.take(3).toPrettyString("CarsCSV"))
    println("Hello World!")
    var test_index_of_car=0 //change which car index do you want to test
    println(movies[test_index_of_car].year)
    println(movies[test_index_of_car].Present_Price)
    println(movies[test_index_of_car].Selling_Price)
    println(knnSplitter(movies, test_index_of_car, k))
}