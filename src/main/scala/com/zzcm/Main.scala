package com.zzcm

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration._

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

import com.zzcm.comsumer.StatsConsumerService
import com.zzcm.config.K2hdfsConfig
import com.zzcm.upload.UploadStream
import org.slf4j.LoggerFactory

/**
  * Created by lms on 17-7-15.
  */
object Main extends App{
  val logger = LoggerFactory.getLogger(getClass)

  implicit val system: ActorSystem = ActorSystem("consumAndUploadActor")

  implicit val mat: ActorMaterializer = ActorMaterializer()

  implicit val ec: ExecutionContextExecutor = system.dispatcher


  val subScriptTopics = K2hdfsConfig().consumerConfig.subScriptTopics
  val group = K2hdfsConfig().consumerConfig.group
  val statsConsumerService = new StatsConsumerService(subScriptTopics, group)
  statsConsumerService.consumeStats()


  val uploadSwitch = system.settings.config.getBoolean("upload.switch");
  if (uploadSwitch) {
    val uploadStream = UploadStream(10.seconds)
    val uploadStreamFlow = uploadStream.flow().run()
    sys.addShutdownHook(uploadStreamFlow.cancel())
  }
}
