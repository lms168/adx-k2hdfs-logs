package com.zzcm.actor

import akka.actor.{Actor, Props}
import akka.event.Logging
import akka.kafka.ConsumerMessage.{CommittableMessage, CommittableOffset}

import com.zzcm.actor.ConsumeMsgActor.OriginMsg
import com.zzcm.actor.FormateMsgActor.FormateMsg
import com.zzcm.actor.OriginalMsgActor.OriginalMsg

//接收的数据类型
object ConsumeMsgActor {
    case class OriginMsg(msgSeq: Seq[CommittableMessage[String, String]])
    def props(fileInfoMap: Map[String, String]) = Props(new ConsumeMsgActor(fileInfoMap))
}


/**
  * Created by lms on 17-7-17.
  * 存储最原始的记录
  */
class ConsumeMsgActor(fileInfoMap: Map[String, String]) extends Actor{
  val log = Logging(context.system, this)
  override def receive: Receive = {
    case OriginMsg(msgSeq) => {
      val offsetWithValue: Seq[(CommittableOffset, String)] = msgSeq.map((msg) =>{
//        log.info("topic:"+msg.record.topic()+"\t partition:"+msg.record.partition()+"\t offset="+ msg.committableOffset+"\t key:"+msg.record.key()+"\t value="+msg.record.value())
        (msg.committableOffset,msg.record.value())
      })
      val offsets: Seq[CommittableOffset] = offsetWithValue.map(_._1)
      val values: Seq[String] = offsetWithValue.map(_._2)


      //保存原始数据
      val originalMsgActor = context.actorOf(OriginalMsgActor.props(fileInfoMap.getOrElse("originalRootPath","home/lms/adx-logs/ori2/"),fileInfoMap.getOrElse("originalFileName","/home/lms/adx-logs/ori2/")))
      originalMsgActor ! OriginalMsg(values)

//      //将数据格式化后保存
      val formateMsgActor = context.actorOf(FormateMsgActor.props(fileInfoMap.getOrElse("formateRootPath","/home/lms/adx-logs/formate2/"),fileInfoMap.getOrElse("formateFileName","/home/lms/adx-logs/formate2/")))
      formateMsgActor ! FormateMsg(values)

      sender() ! offsets
    }
  }




}