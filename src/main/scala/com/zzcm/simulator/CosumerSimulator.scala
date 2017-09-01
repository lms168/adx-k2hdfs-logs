package com.zzcm.simulator

import scala.concurrent.ExecutionContextExecutor

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

import com.zzcm.comsumer.StatsConsumerService
import com.zzcm.config.K2hdfsConfig
import org.slf4j.LoggerFactory

/**
  * Created by lms on 17-7-15.
  */
object CosumerSimulator extends App{
  val logger = LoggerFactory.getLogger(getClass)

  implicit val system: ActorSystem = ActorSystem("consumActor")

  implicit val mat: ActorMaterializer = ActorMaterializer()

  implicit val ec: ExecutionContextExecutor = system.dispatcher

  val subScriptTopics = K2hdfsConfig().consumerConfig.subScriptTopics
  val group = K2hdfsConfig().consumerConfig.group
  val statsConsumerService = new StatsConsumerService(subScriptTopics, group)

  statsConsumerService.consumeStats()

}
