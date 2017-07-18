package com.zzcm.comsumer

import scala.collection.JavaConverters._
import scala.collection.immutable.Seq
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

import akka.actor.ActorSystem
import akka.kafka.ConsumerMessage
import akka.kafka.ConsumerMessage.{CommittableMessage, CommittableOffset}
import akka.pattern.ask
import akka.stream.Materializer
import akka.util.Timeout

import com.zzcm.fileActor.OriginMsgActor
import com.zzcm.fileActor.OriginMsgActor.OriginMsg
import org.apache.kafka.common.serialization.StringDeserializer



/**
  * Created by lms on 17-7-15.
  */
class StatsConsumerService(implicit system: ActorSystem, materializer: Materializer, ec:ExecutionContext) {

  val subScriptTopics = system.settings.config.getStringList("kafka.consumer.topics").asScala.toSet
  val group = system.settings.config.getString("kafka.consumer.groupId")
  val rootPath = system.settings.config.getString("adx-logs.file.rootPath")
  val fileName = system.settings.config.getString("adx-logs.file.fileName")




  object StatsLogConsumerPipeLine extends  CousumePipeLine[String,String]{
    val consumerKeyDeSerializer = new StringDeserializer
    val consumerValueDeSerializer = new StringDeserializer
    override def consumMsg(msgSeq: Seq[CommittableMessage[String, String]]): Future[Seq[CommittableOffset]] = {
                 implicit val timeout = Timeout(1 seconds) // needed for `?` below
                 val oriMsgActor = system.actorOf(OriginMsgActor.props(rootPath,fileName))
                 val offsetBatch: Future[Seq[CommittableOffset]] = (oriMsgActor ? OriginMsg(msgSeq)).mapTo[Seq[ConsumerMessage.CommittableOffset]]
                  offsetBatch
    }
  }





  /**
    * 消费数据
    */
  def consumeStats() ={
    StatsLogConsumerPipeLine.consume(group, subScriptTopics)
  }

}
