package com.zzcm.sender

import scala.concurrent.Future

import akka.actor.ActorSystem
import akka.stream.scaladsl.SourceQueueWithComplete
import akka.stream.{Materializer, QueueOfferResult}

import com.zzcm.model.StatLogScala
import com.zzcm.util.JsonUtil
import org.apache.kafka.common.serialization.StringSerializer

/**
  * Created by lms on 17-6-14.
  * 统计服务
  */

class StatsSendService(implicit system: ActorSystem, materializer: Materializer) {
  /**
    * 发送统计的实现
    */
  object StatLogPipeLine extends SendPipeLine[String, String, StatLogScala] {
    val producerKeySerializer = new StringSerializer
    val producerValueSerializer = new StringSerializer

    override def keyTransfer(t: StatLogScala): String =  java.util.UUID.randomUUID().toString.toUpperCase()
    override def valueTransfer(t: StatLogScala): String = JsonUtil.fromObject(List(t))
  }

  private [this] val topic = system.settings.config.getString("kafka.topic-out")

  /**
    * 发送统计数据
    * @param topic             //要发送的队列名称
    * @param statLogScala      //要发送的数据
    */
  def statisticOperator(topic: String, statLogScala: StatLogScala): Future[QueueOfferResult] = {
    val queue: SourceQueueWithComplete[StatLogScala] = StatLogPipeLine.queue(topic)
    queue.offer(statLogScala)
  }

  def statisticOperator(statLogScala: StatLogScala): Future[QueueOfferResult] = statisticOperator(topic, statLogScala)
}
