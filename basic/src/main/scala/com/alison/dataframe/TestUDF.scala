package com.alison.dataframe

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.udf
import org.apache.spark.util.AccumulatorV2
import org.apache.spark.{SparkConf, SparkContext}

object TestUDF {

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().appName("TestUDF").master("local[*]").getOrCreate()

    import spark.implicits._

    val df = Seq(
      ("Alice", 20),
      ("Bob", 30),
      ("Charlie", 40)
    ).toDF("name", "age")

    df.show()

    // define a UDF
    val addOne = udf((x: Int) => x + 1)

    // apply the UDF to the age column
    df.select(addOne(df("age")).alias("age_plus_one")).show()

    // register the UDF as a temporary function
    df.createOrReplaceTempView("people")
    spark.udf.register("addOne", addOne)
    spark.sql("SELECT name, addOne(age) as age_plus_one FROM people").show()

    // clean up
    spark.stop()
    testUDAF()
  }

  def testUDAF(): Unit = {
    val conf: SparkConf = new SparkConf().setAppName("app").setMaster("local[*]")
    val sc: SparkContext = new SparkContext(conf)
    val res: (Int, Int) = sc.makeRDD(List(("zhangsan", 20), ("lisi", 30), ("wangw", 40))).map {
      case (name, age) => {
        (age, 1)
      }
    }.reduce {
      (t1, t2) => {
        (t1._1 + t2._1, t1._2 + t2._2)
      }
    }
    println(res._1 / res._2)
  }
}

class MyAC extends AccumulatorV2[Int,Int]{
  var sum:Int = 0
  var count:Int = 0
  override def isZero: Boolean = {
    return sum ==0 && count == 0
  }
  override def copy(): AccumulatorV2[Int, Int] = {
    val newMyAc = new MyAC
    newMyAc.sum = this.sum
    newMyAc.count = this.count
    newMyAc
  }
  override def reset(): Unit = {
    sum =0
    count = 0
  }
  override def add(v: Int): Unit = {
    sum += v
    count += 1
  }

  override def merge(other: AccumulatorV2[Int, Int]): Unit = {
    other match {
      case o:MyAC=>{
        sum += o.sum
        count += o.count
      }
      case _=>
    }
  }
  override def value: Int = sum/count
}


