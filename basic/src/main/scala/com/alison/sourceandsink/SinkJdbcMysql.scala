package com.alison.sourceandsink

import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}

case class User(id: Int, username: String, age: Int, email: String)

object SinkJdbcMysql {

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("SinkJdbcMysql")
      .master("local[*]")
      .getOrCreate()
    val data = Seq(
      User(5, "JohnDoe", 28, "johndoe@example.com"),
      User(6, "JaneSmith", 32, "janesmith@example.com"),
      User(7, "BobJohnson", 25, "bobjohnson@example.com")
    )
    import spark.implicits._
    val usersDS = spark.createDataset(data)

    // 定义 users 表对应的 DataFrame 结构
    val usersSchema = StructType(Seq(
      StructField("id", IntegerType, nullable = false),
      StructField("username", StringType, nullable = false),
      StructField("age", IntegerType, nullable = false),
      StructField("email", StringType, nullable = false)
    ))
    // create dataframe from data
    val usersDF = spark.createDataFrame(data).toDF(usersSchema.fieldNames: _*)

    // write dataframe to mysql table
    // 注意：需要在 mysql 中创建 users 表，并设置好相应的字段类型
    // 例如：CREATE TABLE users (id INT, username VARCHAR(255), age INT, email VARCHAR(255));
    // 然后在 jdbc url 中指定数据库名 test
    // 例如：jdbc:mysql://localhost:3306/test
    // 最后在 driver 类中指定 com.mysql.jdbc.Driver

    val connectionProperties = new java.util.Properties()
    connectionProperties.put("user", "root")
    connectionProperties.put("password", "root")
    usersDS.write.mode("append").jdbc("jdbc:mysql://localhost:3306/spark-sql-demo", "users",
      connectionProperties)

    // 方式2
    usersDS.write
      .format("jdbc")
      .option("url", "jdbc:mysql://localhost:3306/spark-sql-demo")
      .option("dbtable", "users")
      .option("user", "root")
      .option("password", "root")
      .mode(SaveMode.Append)
      .save()
    spark.stop()

  }
}

