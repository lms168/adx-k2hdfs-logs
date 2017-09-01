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

import com.zzcm.actor.ConsumeMsgActor
import com.zzcm.actor.ConsumeMsgActor.OriginMsg
import com.zzcm.config.K2hdfsConfig
import org.apache.kafka.common.serialization.StringDeserializer



/**
  * Created by lms on 17-7-15.
  */
class StatsConsumerService(subScriptTopics: Set[String],group: String)(implicit system: ActorSystem, materializer: Materializer, ec:ExecutionContext) {






  object StatsLogConsumerPipeLine extends  CousumePipeLine[String,String]{
    val consumerKeyDeSerializer = new StringDeserializer
    val consumerValueDeSerializer = new StringDeserializer
    override def consumMsg(msgSeq: Seq[CommittableMessage[String, String]]): Future[Seq[CommittableOffset]] = {
                 implicit val timeout = Timeout(1 seconds) // needed for `?` below
                 val oriMsgActor = system.actorOf(ConsumeMsgActor.props())
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
