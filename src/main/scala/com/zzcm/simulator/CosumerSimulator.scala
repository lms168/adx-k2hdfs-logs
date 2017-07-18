package com.zzcm.simulator

import scala.concurrent.ExecutionContextExecutor

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

import com.zzcm.comsumer.StatsConsumerService
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext.Implicits.global._

/**
  * Created by lms on 17-7-15.
  */
object CosumerSimulator extends App{
  val logger = LoggerFactory.getLogger(getClass)

  implicit val system: ActorSystem = ActorSystem("consumActor")

  implicit val mat: ActorMaterializer = ActorMaterializer()

  implicit val ec: ExecutionContextExecutor = system.dispatcher

  val statsConsumerService = new StatsConsumerService()

  statsConsumerService.consumeStats()

}
