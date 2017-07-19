package com.zzcm.actor

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
      values.foreach(x =>{
        val statList: List[StatLogScala] = JsonUtil.toObject[List[StatLogScala]](x)
        val values = statList.map(x => JsonUtil.fromObject(x))
        FileUtil.saveMsg(values, rootPath, fileName)
      })
    }
  }
}


object FormateMsgActor {
  case class FormateMsg(values: Seq[String])
  def props(rootPath: String, fileName: String) = Props(new FormateMsgActor(rootPath, fileName))
}