package com.alison.dataframe

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

object TestRead {

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().appName("TestRead").master("local[*]").getOrCreate()
    //case1: read json file
//    val df = spark.read.format("json").load("data/input/user.json")
//    df.show()
//    df.printSchema()

    //case2: read json file using sql
//    val df = spark.read.json("data/input/user.json")
//    df.createOrReplaceTempView("user")
//    val sqlDF = spark.sql("SELECT * FROM user")
//    sqlDF.show()
//    spark.stop()

    //case3: creat global temp view and query it
//    val df2 = spark.read.json("data/input/user1.json")
//    df2.createGlobalTempView("user_global")
//    spark.sql("SELECT * FROM global_temp.user_global").show()


    //case4: dsl query
    val df3 = spark.read.json("data/input/user1.json")
    df3.printSchema()
    df3.select("username", "age").show()

    df3.select($"username", ($"age" + 1).alias("newage")).show()
//    df3.select('username, 'age  + 1).show()
//    df3.select('username, 'age + 1 as "newage").show()
  }
}
