package com.alison.dataframe

import org.apache.spark.sql.expressions.Aggregator
import org.apache.spark.sql.{Dataset, Encoder, Encoders, SparkSession, TypedColumn, functions}

object TestUDAF {
  def main(args: Array[String]): Unit = {
    try {
      val spark = SparkSession.builder().appName("TestUDAF").master("local[*]").getOrCreate()
      import spark.implicits._

      // 创建 DataFrame 并转换为 DataSet
      val df = Seq(
        ("Alice", 20),
        ("Bob", 30),
        ("Charlie", 40)
      ).toDF("username", "age")

      val ds: Dataset[User01] = df.as[User01]

      // 创建自定义聚合函数并查询
      val myAgeUdaf1 = new MyAvgAgeUDAF1
      val col: TypedColumn[User01, Double] = myAgeUdaf1.toColumn
      ds.select(col).show()

      ds.createTempView("user")
      // 使用强类型的 Aggregator 创建 UDAF 函数
      val udaf = new MyAvgAgeUDAF
      // 注册到 SparkSQL 中
      spark.udf.register("avgAge", functions.udaf(udaf))

      // 在 SQL 中使用聚合函数
      spark.sql("select avgAge(age) from user").show
    } catch {
      case e: Exception => println(s"发生错误: ${e.getMessage}")
    }
  }
}

// 输入数据类型
case class User01(username: String, age: Long)

// 缓存类型
case class AgeBuffer(var sum: Long, var count: Long)

// 定义第一个自定义聚合函数
class MyAvgAgeUDAF1 extends Aggregator[User01, AgeBuffer, Double] {
  override def zero: AgeBuffer = AgeBuffer(0L, 0L)

  override def reduce(buffer: AgeBuffer, user: User01): AgeBuffer = {
    buffer.sum += user.age
    buffer.count += 1
    buffer
  }

  override def merge(b1: AgeBuffer, b2: AgeBuffer): AgeBuffer = {
    b1.sum += b2.sum
    b1.count += b2.count
    b1
  }

  override def finish(buffer: AgeBuffer): Double = buffer.sum.toDouble / buffer.count

  // DataSet 默认编解码器，用于序列化
  override def bufferEncoder: Encoder[AgeBuffer] = Encoders.product

  // 输出编码器
  override def outputEncoder: Encoder[Double] = Encoders.scalaDouble

}

// 缓存类型
case class Buff(var sum: Long, var cnt: Long)
//
//// 定义第二个自定义聚合函数
class MyAvgAgeUDAF extends Aggregator[Long, Buff, Double] {
  override def zero: Buff = Buff(0, 0)

  override def reduce(buffer: Buff, age: Long): Buff = {
    buffer.sum += age
    buffer.cnt += 1
    buffer
  }

  override def merge(b1: Buff, b2: Buff): Buff = {
    b1.sum += b2.sum
    b1.cnt += b2.cnt
    b1
  }

  override def finish(buffer: Buff): Double = {
    if (buffer.cnt == 0) Double.NaN else buffer.sum.toDouble / buffer.cnt
  }

  // DataSet 默认编解码器，用于序列化
  override def bufferEncoder: Encoder[Buff] = Encoders.product

  // 输出编码器
  override def outputEncoder: Encoder[Double] = Encoders.scalaDouble
}
