package com.zzcm.actor

import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

import scala.util.Try

import akka.actor.{Actor, Props}

import com.zzcm.actor.FormateMsgActor.FormateMsg
import com.zzcm.model.StatLogScala
import com.zzcm.util.{FileUtil, JsonUtil}


/**
  * Created by lms on 17-7-18.
  * 将接受到的数据格式化,并且将其保存到本地,留待之后上传到hdfs做数据分析
  */
class FormateMsgActor(rootPath: String, fileName: String) extends Actor {
  override def receive: Receive = {
    case FormateMsg(values) => {
      Try{
      values.foreach(x =>{
         val statList: List[StatLogScala] = JsonUtil.toObject[List[StatLogScala]](x)
          statList.map(x => JsonUtil.fromObject(x))
            .map(value => (TimestampPath.extractTimestamp(value) -> value)) //(timestamp->record)
            .groupBy(_._1)
            .map(x => (x._1, x._2.map(_._2))) //(timestamp->List(record))
            .filter(elem => elem._1 != -1)   //对于没有正确提取到时间戳的数据,直接丢弃
            .foreach(elem => FileUtil.writeToFile(elem._2.mkString("\n"), TimestampPath.initTimestampFile(elem._1, rootPath, fileName)))
      })
      }.recover {
        case e: Exception =>{
          val sdf = new SimpleDateFormat("yyyy-MM-dd")
          val datePath = sdf.format(new Date());
          val file= new File(rootPath.concat(datePath), "formateError.txt")
          FileUtil.writeToFile(values.mkString("\n"),file)
        }
        case _ => println("未知错误")
      }
    }
  }
}


object FormateMsgActor {
  case class FormateMsg(values: Seq[String])
  def props(rootPath: String, fileName: String) = Props(new FormateMsgActor(rootPath, fileName))
}