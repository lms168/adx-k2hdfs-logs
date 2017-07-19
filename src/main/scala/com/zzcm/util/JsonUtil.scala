package com.zzcm.util

import org.json4s

/**
  * Created by lms on 17-6-14.
  * 对象与字符串的相互转换
  */

object JsonUtil {
  import org.json4s._
  import org.json4s.jackson.JsonMethods._

  implicit val formats: json4s.DefaultFormats.type = DefaultFormats

  /**
    * 将字符串转换为对象
    * @param str
    * @param manifest
    * @tparam A
    * @return
    */
  def  toObject[A](str: String)(implicit manifest: Manifest[A]): A ={
    val t = parse(str)
    val result = t.extract[A]
    result
  }

  /**
    * 将对象转换为字符串
    * @param s
    * @return
    */
  def fromObject(s: AnyRef): String ={
    val result = org.json4s.jackson.Serialization.write(s)
     result
  }

}



