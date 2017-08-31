package com.zzcm.simulator

import scala.concurrent.ExecutionContextExecutor

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

import com.zzcm.model.StatLogScala
import com.zzcm.sender.StatsSendService
import org.slf4j.LoggerFactory



/**
  * Created by lms on 17-7-15.
  */
object SenderSimulator extends App{
  val logger = LoggerFactory.getLogger(getClass)

  implicit val system: ActorSystem = ActorSystem()

  implicit val mat: ActorMaterializer = ActorMaterializer()

  implicit val ec: ExecutionContextExecutor = system.dispatcher

  val statsService = new StatsSendService


  logger.info("===============================开始发送数据了==================================")
  val stat = new StatLogScala()
  for (i <- 1 to 100){
    val newStat = stat.copy(reqId = String.valueOf(i),operator = "CLICK",interactionType = 1,pkgName = "ssssss")
    statsService.statisticOperator(newStat)
  }
  logger.info("=================================发送100条数据结束=============================================")
  system.terminate()

}
