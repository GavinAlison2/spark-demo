package com.alison.basic

import org.apache.spark.{SparkConf, SparkContext}

object TestAggregate {
  def main(args: Array[String]): Unit = {
    val sc = new SparkContext(new SparkConf().setAppName("TestAggregate").setMaster("local[*]"))
    val rdd = sc.makeRDD(List( ("a", 1), ("a", 2), ("c", 3), ("b", 4), ("c", 5), ("a", 6) ), 2)
    val resultRDD = rdd.aggregateByKey(10)(
      (x, y) => math.max(x, y),
      (x, y) => x + y
    )
    resultRDD.collect().foreach(println)
  }
  /*

  运行过程
分区 0：("a",1),("a",2),("c",3)
对于键 "a"：初始值为 10，math.max(10, 1) = 10，math.max(10, 2) = 10，最终 "a" 的累加器值为 10。
对于键 "c"：初始值为 10，math.max(10, 3) = 10，最终 "c" 的累加器值为 10。
分区 1：("b",4),("c",5),("c",6)
对于键 "b"：初始值为 10，math.max(10, 4) = 10，最终 "b" 的累加器值为 10。
对于键 "c"：初始值为 10，math.max(10, 5) = 10，math.max(10, 6) = 10，最终 "c" 的累加器值为 10。
合并分区：
键 "a"：只有一个分区有值，结果为 10。
键 "b"：只有一个分区有值，结果为 10。
键 "c"：两个分区的值相加，10 + 10 = 20。

   */

}
