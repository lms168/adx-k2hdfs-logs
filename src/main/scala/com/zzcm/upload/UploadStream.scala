package com.zzcm.upload

import scala.collection.immutable.IndexedSeq
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

import akka.actor.{ActorSystem, Cancellable}
import akka.pattern._
import akka.stream.scaladsl.{Sink, Source}
import akka.stream.{ActorAttributes, Materializer, Supervision}
import akka.util.Timeout

import com.zzcm.actor.UploadFileActor
import com.zzcm.actor.UploadFileActor.PathMsg
import com.zzcm.util.DateUtil
/**
  * Created by lms on 17-8-17.
  */
object UploadStream{
  def apply(interval: FiniteDuration)
           (implicit system: ActorSystem, mt: Materializer, ec: ExecutionContext): UploadStream = new UploadStream(interval)
}

class UploadStream(interval: FiniteDuration)
                  (implicit system: ActorSystem, mt: Materializer, ec: ExecutionContext) {
  implicit val timeout: Timeout = akka.util.Timeout(10.minutes)
  def flow()(implicit ec: ExecutionContext): Cancellable = {
    Source
      .tick(0.seconds, interval, ())
      .mapConcat(_ => genSegment)
      .mapAsync(1) { case (path) => {
        val uploadFileActor = system.actorOf(UploadFileActor.props())
        val f: Future[Any] = uploadFileActor ? PathMsg(path)
        f.mapTo[Option[String]]
      }
      }.map(x=>x match {
            case Some(x) => println(x)
            case None =>
          }
       )
      .withAttributes(ActorAttributes.supervisionStrategy(decider))
      .initialTimeout(1.minutes)
      .to(Sink.ignore)
      .run()
  }


 private[this] def genSegment: IndexedSeq[String] = {
    val segments = 24
    val d =  1.hours.toMillis
    val t0 = System.currentTimeMillis();
    val t = t0 - (t0 % d)
    (1 to segments).map{
      n=>{
        val time = t - n * d
        DateUtil.formateTimestampPath(time)
      }
    }
  }


  private[this] val decider: Supervision.Decider = {
    case NonFatal(e) =>
      system.log.error("{} Stream error! will retry {}ms later. cause: {}", getClass, 10.seconds, e)
      Thread.sleep(10000L)
      Supervision.Resume
  }

}
