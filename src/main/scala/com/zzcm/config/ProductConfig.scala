package com.zzcm.config

import akka.actor.ActorSystem

import com.typesafe.config.Config

/**
  * Created by lms on 17-8-23.
  */
final case class ProductConfig(topicOut: String) {

}

object ProductConfig {
  def apply(config: Config): ProductConfig = {
    ProductConfig(config.getString("kafka.topic-out"))
  }
  def apply()(implicit system: ActorSystem): ProductConfig =  this.apply(system.settings.config)
}
