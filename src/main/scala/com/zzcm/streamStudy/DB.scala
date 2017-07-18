package com.zzcm.streamStudy

import java.util.concurrent.atomic.AtomicLong

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContextExecutor, Future}

import akka.actor.ActorSystem
import akka.stream.scaladsl.Source
import akka.stream.{ActorMaterializer, ThrottleMode}
import akka.{Done, NotUsed}

import org.apache.kafka.clients.consumer.ConsumerRecord

/**
  * Created by lms on 17-7-5.
  */
class DB {
  private val offset = new AtomicLong

  def save(record: ConsumerRecord[Array[Byte], String]): Future[Done] = {
    println(s"DB.save:key=${record.key()},offset=${record.offset()},value=${record.value()}")
    offset.set(record.offset())
    Future.successful(Done)
  }


  def  loadOffset(): Future[Long] = Future.successful(offset.get())


  def update(data: String): Future[Done] = {
    println(s"DB.update:${data}")
    Future.successful(Done)

  }

}

class Rocket {
  def launch(destination: String): Future[Done] = {
    println(s"Rocket launched to $destination")
    Future.successful(Done)
  }
}




object ConsumerMain extends App {

  val logger = org.slf4j.LoggerFactory.getLogger(getClass)
  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val ec: ExecutionContextExecutor = system.dispatcher


  val sources = Source(1 to 100)
//  sources.runForeach(i => println(i))


  val factorials: Source[BigInt, NotUsed] = sources.scan(BigInt(1))((acc, next)=>acc * next)
//factorials.map(num => ByteString(s"$num\n"))
//    .runWith(FileIO.toPath(Paths.get("/home/lms/factorials.txt")))



  factorials.zipWith(Source(0 to 100))((num, idx)=> s"$idx!=$num")
    .throttle(1,1.second,3,ThrottleMode.shaping)
    .runForeach(println)


//
//
//  println("开始消费了=================================================================")
//  val consumerSettings = ConsumerSettings(system, new ByteArrayDeserializer, new StringDeserializer)
//    .withBootstrapServers("192.168.0.61:9092")
//    .withGroupId("group_hdfs")
//    .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"earliest")
//
//
//  //1.将offset存储在外部,借助于外部存储系统与代码逻辑,可以实现原子操作以此实现精确的只消费一次
//  val db = new DB
//  db.loadOffset().foreach(fromOffset =>{
//    val partition = 0
//    val subscription = Subscriptions.assignmentWithOffset(new TopicPartition("adx-log",partition) -> fromOffset)
//    val done = Consumer.plainSource(consumerSettings,subscription).mapAsync(1)(db.save).runWith(Sink.ignore())
//  })
//
//
//
//  val done = Consumer.committableSource(consumerSettings, Subscriptions.topics("adx-log"))
//    .mapAsync(1){
//      msg => db.update(msg.record.value()).map(_ => msg)
//    }
//    .mapAsync(1){
//      msg => msg.committableOffset.commitScaladsl()
//    }.runWith(Sink.ignore())
//
//
//
//  val done1 = Consumer.committableSource(consumerSettings, Subscriptions.topics("topic1"))
//    .mapAsync(1){
//      msg => db.update(msg.record.value()).map(_=>msg.committableOffset)
//    }
//    .batch(max = 20, frist =>CommittableOffsetBatch.empty.updated(frist)){(batch, elem) =>
//      batch.updated(elem)
//  }
//    .mapAsync(3)(_.commitScaladsl())
//    .runWith(Sink.ignore())
//
//
//  val done2 = Consumer.committablePartitionedSource(consumerSettings, Subscriptions.topics("topic1"))
//    .flatMapMerge(2, _._2)
////    .via(business)
//    .batch(max = 20, first => CommittableOffsetBatch.empty.updated(first.committableOffset)) { (batch, elem) =>
//      batch.updated(elem.committableOffset)
//    }
//    .mapAsync(3)(_.commitScaladsl())
//    .runWith(Sink.ignore)
//
//




















}

