package com.zzcm.actor

import akka.actor.{Actor, ActorSystem, Props}
import akka.event.Logging
import akka.kafka.ConsumerMessage.{CommittableMessage, CommittableOffset}

import com.zzcm.actor.ConsumeMsgActor.OriginMsg
import com.zzcm.actor.FormateMsgActor.FormateMsg
import com.zzcm.actor.OriginalMsgActor.OriginalMsg
import com.zzcm.config.{K2hdfsConfig}

//接收的数据类型
object ConsumeMsgActor {
    case class OriginMsg(msgSeq: Seq[CommittableMessage[String, String]])
    def props() = Props(new ConsumeMsgActor())
}


/**
  * Created by lms on 17-7-17.
  * 存储最原始的记录
  */
class ConsumeMsgActor() extends Actor{
  val log = Logging(context.system, this)
  private[this] implicit val system: ActorSystem = context.system
  val writeLocalConfig =  K2hdfsConfig().writeToLocalConfig

  val originalRootPath = writeLocalConfig.originalRootPath
  val originalFileName = writeLocalConfig.originalFileName
  val formateRootPath =  writeLocalConfig.formateRootPath
  val formateFileName =  writeLocalConfig.formateFileName


  override def receive: Receive = {
    case OriginMsg(msgSeq) => {
      val offsetWithValue: Seq[(CommittableOffset, String)] = msgSeq.map((msg) =>{
//       log.info("topic:"+msg.record.topic()+"\t partition:"+msg.record.partition()+"\t offset="+ msg.committableOffset+"\t key:"+msg.record.key()+"\t value="+msg.record.value())
        (msg.committableOffset,msg.record.value())
      })
      val offsets: Seq[CommittableOffset] = offsetWithValue.map(_._1)
      val values: Seq[String] = offsetWithValue.map(_._2)


      //保存原始数据
      val originalMsgActor = context.actorOf(OriginalMsgActor.props(originalRootPath,originalFileName))
      originalMsgActor ! OriginalMsg(values)

      //将数据格式化后保存
      val formateMsgActor = context.actorOf(FormateMsgActor.props(formateRootPath,formateFileName))
      formateMsgActor ! FormateMsg(values)

      sender() ! offsets
    }
  }




}