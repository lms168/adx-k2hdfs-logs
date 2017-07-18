package com.zzcm.comsumer

import scala.collection.immutable.Seq
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

import akka.actor.ActorSystem
import akka.kafka.ConsumerMessage.{CommittableMessage, CommittableOffset, CommittableOffsetBatch}
import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.Materializer
import akka.stream.scaladsl.Sink

import org.apache.kafka.common.serialization.Deserializer

/**
  * Created by lms on 17-7-15.
  */
trait CousumePipeLine[K, V] {
  val consumerKeyDeSerializer: Deserializer[K]
  val consumerValueDeSerializer: Deserializer[V]



  def consumMsg(msgSeq: Seq[CommittableMessage[K, V]]):Future[Seq[CommittableOffset]]


  /**
    * 消费数据
    * @param ts  要订阅的队列名列表
    * @param group            组的定义
    * @param system
    * @param mt
    */
  def consume(group: String,ts: Set[String])(implicit system: ActorSystem, mt: Materializer, ec:ExecutionContext)= {
    val consumerSettings = ConsumerSettings(system, consumerKeyDeSerializer, consumerValueDeSerializer).withGroupId(group)
     Consumer.committableSource(consumerSettings, Subscriptions.topics(ts))
       .groupedWithin(1000, 1.seconds)
       .mapAsync(1){ (msgSeq: Seq[CommittableMessage[K, V]]) => {consumMsg(msgSeq)}}
       .map{offsetSeq => {offsetSeq.foldLeft(CommittableOffsetBatch.empty) { (batch: CommittableOffsetBatch, elem: CommittableOffset) => batch.updated(elem)}}}
       .mapAsync(3)(_.commitScaladsl()).runWith(Sink.ignore)







//
//下面方式为非actor方式直接消费数据
//
//       .mapAsync(1) { (msg: CommittableMessage[K, V]) =>{
//          println("topic:"+msg.record.topic()+"\t partition:"+msg.record.partition()+"\t offset="+ msg.committableOffset+"\t key:"+msg.record.key()+"\t value="+msg.record.value())
//          Future(msg.committableOffset)
//        }
//     }
////       .batch(max = 20, first => CommittableOffsetBatch.empty.updated(first)) { (batch, elem) => batch.updated(elem)}
////        //第二种批量提交的方式
//       .groupedWithin(1000, 1.seconds).map((group: Seq[CommittableOffset]) => group.foldLeft(CommittableOffsetBatch.empty) { (batch: CommittableOffsetBatch, elem: CommittableOffset) => {
//         println(s"要保存的${elem.partitionOffset.offset}")
//         batch.updated(elem)
//         }
//        })
//       .mapAsync(3)(_.commitScaladsl()).runWith(Sink.ignore)
  }
}
