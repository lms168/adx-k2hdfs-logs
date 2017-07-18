package com.zzcm.streamStudy
import scala.concurrent._

import akka._
import akka.actor.Actor.Receive
import akka.actor._
import akka.stream._
import akka.stream.scaladsl._
import akka.util._


/**
  * Created by lms on 17-7-14.
  */
object Unit1 extends App{
  implicit val system = ActorSystem("TestSystem")
  implicit val materializer = ActorMaterializer()
  import system.dispatcher

  val s: Source[Nothing, NotUsed] = Source.empty;

  val s1: Source[String, NotUsed] = Source.single("single element")

  val s3: Source[Int, NotUsed] =  Source(1 to 3)

  val sink = Sink.foreach[Int](elem => println(s"sike received:$elem") )


  val invert = Flow[Int].map(elem => elem * -1)

  val  double = Flow[Int].map(elem => elem * 2)


  val runnable = s3.via(invert).via(double).to(sink)

  runnable.run()


  val flowResult1 = Source(1 to 3).via(invert).to(sink)
  val flowResult2 = Source(-3 to -1).via(invert).to(sink)
  flowResult1.run()
  flowResult2.run()



//  val flow: RunnableGraph[NotUsed] = s3.to(sink)
//  flow.run()




//
//  val actor = system.actorOf(Props(new Actor {
//    override def receive: Receive = {
//      case msg => println(s"actor received:$msg")
//    }
//  }))
//
//  val sink2 = Sink.actorRef(actor,onCompleteMessage = "stream completed")
//
//  val runnable = Source(1 to 3).to(sink2)
//  runnable.run()




//  s3.runForeach(println)

//
//  val s4 = Source.repeat(5)
//  val result4: Source[Int, NotUsed] = s4.take(10)
//  result4.runForeach(println)
//

//  def run(actor: ActorRef) = {
//    Future{Thread.sleep(300);actor ! 1}
//    Future{Thread.sleep(200);actor ! 2}
//    Future{Thread.sleep(100);actor ! 3}
//  }
//
//  val s5: Source[Nothing, Future[Unit]] = Source.actorRef(bufferSize = 0, OverflowStrategy.fail).mapMaterializedValue(run)
//  s5.runForeach(println)




  final case class Author(handle: String)

  final case class Hashtag(name: String)

  final case class Tweet(author: Author, timestamp: Long, body: String) {
    def hashtags: Set[Hashtag] =
      body.split(" ").collect { case t if t.startsWith("#") => Hashtag(t) }.toSet
  }

  val akkaTag = Hashtag("#akka")

  val tweets: Source[Tweet, NotUsed] = Source(
    Tweet(Author("rolandkuhn"), System.currentTimeMillis, "#akka rocks!") ::
      Tweet(Author("patriknw"), System.currentTimeMillis, "#akka !") ::
      Tweet(Author("bantonsson"), System.currentTimeMillis, "#akka !") ::
      Tweet(Author("drewhk"), System.currentTimeMillis, "#akka !") ::
      Tweet(Author("ktosopl"), System.currentTimeMillis, "#akka on the rocks!") ::
      Tweet(Author("mmartynas"), System.currentTimeMillis, "wow #akka !") ::
      Tweet(Author("akkateam"), System.currentTimeMillis, "#akka rocks!") ::
      Tweet(Author("bananaman"), System.currentTimeMillis, "#bananas rock!") ::
      Tweet(Author("appleman"), System.currentTimeMillis, "#apples rock!") ::
      Tweet(Author("drama"), System.currentTimeMillis, "we compared #apples to #oranges!") ::
      Nil)

  val count: Flow[Tweet, Int, NotUsed] = Flow[Tweet].map(_=>1)
  val sumSink = Sink.fold[Int, Int](0)(_+_)

  val counterGraph: RunnableGraph[Future[Int]] = tweets.via(count).toMat(sumSink)(Keep.right)

  val sum = counterGraph.run()

  sum.foreach(c => println(s"$c"))






}
