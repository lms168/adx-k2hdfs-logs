package com.zzcm.actor

import akka.actor.{Actor, Props}

import com.zzcm.actor.OriginalMsgActor.OriginalMsg
import com.zzcm.util.FileUtil

object OriginalMsgActor{
  case class  OriginalMsg(values: Seq[String])
  def props(rootPath: String, fileName: String) = Props(new OriginalMsgActor(rootPath, fileName))

}

/**
  * Created by lms on 17-7-19.
  */
class OriginalMsgActor(rootPath: String, fileName: String) extends Actor{
  override def receive: Receive = {
    case OriginalMsg(values)=>{
      FileUtil.saveMsg(values,rootPath,fileName)  //将接收到的数据保存到文件中
    }
  }
}
