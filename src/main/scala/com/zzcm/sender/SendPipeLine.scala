package com.zzcm.sender

import akka.actor.ActorSystem
import akka.kafka.scaladsl.Producer
import akka.kafka.{ProducerMessage, ProducerSettings}
import akka.stream.scaladsl._
import akka.stream.{Materializer, OverflowStrategy}
import io.prometheus.client.Counter
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.Serializer
/**
  * K: Kafka key type
  * V: Kafka value type
  * T: received message's type
  */
trait SendPipeLine[K, V, T] {
  val producerKeySerializer: Serializer[K]
  val producerValueSerializer: Serializer[V]

  val bufferSize: Int = 10000
  val overflowStrategy: OverflowStrategy = akka.stream.OverflowStrategy.backpressure

  def keyTransfer(t: T): K
  def valueTransfer(t: T): V

  val sentCounterM: Counter = Counter.build()
    .name("sent_logs_count")
    .help("sent_logs_count")
    .register()

  def queue(topic: String)(implicit system: ActorSystem, mt: Materializer): SourceQueueWithComplete[T] =
    Source
      .queue[T](bufferSize, overflowStrategy)
      .map(e => ProducerMessage.Message(new ProducerRecord(topic, keyTransfer(e), valueTransfer(e)), e))
      .via(Producer.flow(ProducerSettings(system, producerKeySerializer, producerValueSerializer): ProducerSettings[K, V]))
      .map(_ => sentCounterM.inc())
      .to(Sink.ignore)
      .run()
}