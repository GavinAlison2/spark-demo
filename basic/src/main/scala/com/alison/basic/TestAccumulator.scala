package com.alison.basic

import org.apache.spark.sql.SparkSession
import java.util.Arrays

object TestAccumulator {
  def main(args: Array[String]): Unit = {
    val ss: SparkSession = SparkSession.builder().appName("test-ccumulator").master("local[2]").getOrCreate()
    val sc = ss.sparkContext
    val longAccumulator = sc.longAccumulator("My longAccumulator")
    sc.makeRDD(Seq(1, 2, 3, 4)).foreach(v => {
      println(Thread.currentThread().getName + "---" + v)
      longAccumulator.add(v)
      println(Thread.currentThread().getName + " longAccumulator=" + longAccumulator.value)
    })
    println("Driver " + Thread.currentThread().getName + " longAccumulator=" + longAccumulator.value)

    Thread.sleep(10 * 60 * 1000) // http://localhost:4040
  }
}