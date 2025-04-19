package com.alison.dataframe

import org.apache.spark.sql.{DataFrame, SparkSession}

object TestRDD2DF {

  def main(args: Array[String]): Unit = {
    val sc = SparkSession.builder().appName("TestRDD2DF").master("local[*]").getOrCreate()
    import sc.implicits._
    val rdd = sc.sparkContext.parallelize(Seq(
      ("Alice", 25),      // 0
      ("Bob", 30),        // 1
      ("Charlie", 35),    // 2
      ("David", 40),      // 3
      ("Emily", 45)       // 4
    ))
    val df = rdd.toDF("name", "age")
    df.show()
//    sc.stop()

    println("createUserDF")
    createUserDF(sc)
  }


  case class User(name:String, age:Int)

  def createUserDF(sc:SparkSession):Unit = {
    import sc.implicits._
    val rdd = sc.sparkContext.parallelize(Seq(
       ("Alice", 25),      // 0
       ("Bob", 30),        // 1
       ("Charlie", 35),    // 2
       ("David", 40),      // 3
       ("Emily", 45)       // 4
    ))
    val df:DataFrame = rdd.map(t => User(t._1, t._2)).toDF
    df.show()
    df.rdd.foreach(println)

    }
}
