package com.alison.sourceandsink

import org.apache.spark.SparkConf
import org.apache.spark.sql.{DataFrame, SparkSession}

import java.util.Properties

/*
create database if not exists `spark-sql-demo`;
show databases;
use `spark-sql-demo`;
drop table if exists `user`;
create table if not exists `users`(
    id int,
    username varchar(20),
    age int,
    email varchar(30)
)engine = InnoDB default charset = utf8mb4;

show tables;
insert into `users` values
                        (1, 'Alice', 25, 'alice@example.com'),
                        (2, 'Bob', 30, 'bob@example.com'),
                        (3, 'Charlie', 35, 'charlie@example.com'),
                        (4, 'David', 40, 'david@example.com');

select * from users limit 10;

 */
object SourceJdbcMysql {

  def main(args: Array[String]): Unit = {
    println("Hello, SourceJdbcMysql!")
    val conf: SparkConf = new SparkConf().setMaster("local[*]").setAppName ("SourceJdbcMysql");
    val spark: SparkSession = SparkSession.builder().config(conf).getOrCreate()
    //方式 1：通用的 load 方法读取
    spark.read.format("jdbc")
      .option("url", "jdbc:mysql://localhost:3306/spark-sql-demo")
      .option("driver", "com.mysql.cj.jdbc.Driver")
      .option("user", "root")
      .option("password", "root")
      .option("dbtable", "users")
      .load().show()
    println("*"*100)
    //方式 2:通用的 load 方法读取 参数另一种形式
    spark.read.format("jdbc").options(
      Map(
        "url"->"jdbc:mysql://localhost:3306/spark-sql-demo?user=root&password=root",
      "dbtable"->"users",
      "driver"->"com.mysql.cj.jdbc.Driver")
    ).load().show
    println("*"*100)
    //方式 3:使用 jdbc 方法读取
    val props: Properties = new Properties()
    props.setProperty("user", "root")
    props.setProperty("password", "root")
    val df: DataFrame = spark.read.jdbc("jdbc:mysql://localhost:3306/spark-sql-demo", "users", props)
    df.show
    //释放资源
    spark.stop()
  }

}
