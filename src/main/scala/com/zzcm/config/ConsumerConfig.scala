package com.zzcm.config

import com.typesafe.config.Config
import scala.collection.JavaConverters._

import akka.actor.ActorSystem
/**
  * Created by lms on 17-8-23.
  */
final  case class ConsumerConfig(subScriptTopics: Set[String], group: String )

object ConsumerConfig {

  def apply(config: Config): ConsumerConfig = {
    val subScriptTopics = config.getStringList("kafka.consumer.topics").asScala.toSet
    val group = config.getString("kafka.consumer.groupId")
    ConsumerConfig(subScriptTopics,group)
  }

  def apply()(implicit system: ActorSystem): ConsumerConfig =  this.apply(system.settings.config)
}
