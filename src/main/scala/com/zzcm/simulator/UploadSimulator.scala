package com.zzcm.simulator

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration._

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

import com.zzcm.upload.UploadStream
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext.Implicits.global._

/**
  * Created by lms on 17-8-24.
  */
class UploadSimulator {
  val logger = LoggerFactory.getLogger(getClass)

  implicit val system: ActorSystem = ActorSystem("consumActor")

  implicit val mat: ActorMaterializer = ActorMaterializer()

  implicit val ec: ExecutionContextExecutor = system.dispatcher

  val uploadStream =  UploadStream(10.seconds)
  val uploadStreamFlow = uploadStream.flow()
  sys.addShutdownHook(uploadStreamFlow.cancel())

}
