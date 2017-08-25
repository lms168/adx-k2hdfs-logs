package com.zzcm.actor

import akka.actor.{Actor, Props}
import akka.event.Logging

import com.zzcm.actor.UploadStatusActor.{EndUpload, StartUpload}


/**
  * Created by lms on 17-8-25.
  */


object UploadStatusActor {
  case class StartUpload(path: String)
  case class EndUpload(path: String)
  def props = Props(new UploadStatusActor())
}

class UploadStatusActor extends Actor{
  val log = Logging(context.system, this)
  var startUploadTime = 0l  ;

  override def receive: Receive = {
    case StartUpload(path) => {
      startUploadTime =  System.currentTimeMillis()
      log.info(s"文件开始上传time=[$startUploadTime]\tpath====$path")
    }
    case EndUpload(path) =>{
      val successUploadTime =  System.currentTimeMillis()
      log.info(s"文件成功上传time=${successUploadTime}成功path====$path,历时=${(successUploadTime-startUploadTime)/60000}分钟")
    }
    case _ => log.error("未知类型的信息")
  }
}
