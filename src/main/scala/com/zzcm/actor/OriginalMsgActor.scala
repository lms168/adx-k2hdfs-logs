package com.zzcm.actor

import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

import scala.util.Try

import akka.actor.{Actor, Props}
import akka.event.Logging

import com.zzcm.actor.OriginalMsgActor.OriginalMsg
import com.zzcm.util.FileUtil

object OriginalMsgActor{
  case class  OriginalMsg(values: Seq[String])
  def props(rootPath: String, fileName: String, timeField: String ) = Props(new OriginalMsgActor(rootPath, fileName, timeField))

}

/**
  * Created by lms on 17-7-19.
  */
class OriginalMsgActor(rootPath: String, fileName: String, timeField: String ) extends Actor{
  val log = Logging(context.system, this)
  override def receive: Receive = {
    case OriginalMsg(values)=> {
      Try {
          values.map(value => (TimestampPath.extractTimestamp(value,"createTime") -> value)) //(timestamp->record)
          .toList
          .groupBy(_._1)
          .map(x => (x._1, x._2.map(_._2)))
            .foreach(elem => FileUtil.writeToFile(elem._2.mkString("\n"), TimestampPath.initFormateTimestampFile(elem._1, rootPath, fileName)))
      } recover {
        case e: Exception =>{
          log.error(e,s"OriginalMsgActor:msg=${values.mkString("\n")}")
          val sdf = new SimpleDateFormat("yyyy-MM-dd")
          val datePath = sdf.format(new Date());
          val file= new File(rootPath.concat(datePath), "originalError.txt")
          FileUtil.writeToFile(values.mkString("\n"),file)
        }
        case _ => log.error("OriginalMsgActor遇到了未知错误!!!!!!!!!!!!!!!!!!!!!!!!!")
      }
    }
  }
}
