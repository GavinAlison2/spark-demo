package com.alison.basic

import org.apache.spark.{SparkConf, SparkContext}

object TestFoldByKey {
  def main(args: Array[String]): Unit = {
    val sc = new SparkContext(new SparkConf().setAppName("TestFoldByKey").setMaster("local[*]"))
    val rdd = sc.parallelize(Seq((1, 2), (1, 3), (2, 4), (2, 5), (3, 6), (3, 7)), 2)
    val result = rdd.foldByKey(0)(_+_)
    result.foreach(println)

    println("="*10)

    testCombineByKey(sc) // 调用 testCombineByKey 方法
  }

  def testCombineByKey(sc: SparkContext): Unit = { // 修正了返回类型，并将 SparkContext 作为参数传入
    val rdd = sc.parallelize(Seq((1, 2), (1, 3), (2, 4), (2, 5), (3, 6), (3, 7)), 2)
    val result = rdd.combineByKey(
      (v) => v, // 创建组合器函数 createCombiner
      (acc: Int, v: Int) => acc + v, // 合并值 mergeValue
      (acc1: Int, acc2: Int) => acc1 + acc2 // 合并组合器  mergeCombiners
    )
    result.foreach(println)
  }
/*
分区 1 处理过程
对于键 1：
第一次遇到 (1, 2)，调用 createCombiner，初始组合器为 2。
遇到 (1, 3)，调用 mergeValue，组合器更新为 2 + 3 = 5。
对于键 2：
第一次遇到 (2, 4)，调用 createCombiner，初始组合器为 4。
分区 2 处理过程
对于键 2：
第一次遇到 (2, 5)，调用 createCombiner，初始组合器为 5。
对于键 3：
第一次遇到 (3, 6)，调用 createCombiner，初始组合器为 6。
遇到 (3, 7)，调用 mergeValue，组合器更新为 6 + 7 = 13。
合并分区
对于键 1：只有分区 1 有，结果为 5。
对于键 2：分区 1 的组合器为 4，分区 2 的组合器为 5，调用 mergeCombiners，结果为 4 + 5 = 9。
对于键 3：只有分区 2 有，结果为 13。
 */
}
