package com.zzcm.config

import akka.actor.ActorSystem

import com.typesafe.config.Config

/**
  * Created by lms on 17-8-23.
  */
final case class K2hdfsConfig(productConfig: ProductConfig
                              , consumerConfig: ConsumerConfig
                              , writeToLocalConfig: WriteLocalConfig
                              , uploadConfig: UploadConfig)



object K2hdfsConfig{
  def apply()(implicit  system: ActorSystem): K2hdfsConfig = this.apply(system.settings.config)
  def apply(config: Config): K2hdfsConfig =  K2hdfsConfig(
    ProductConfig(config)
    , ConsumerConfig(config)
    , WriteLocalConfig(config)
    , UploadConfig(config))
}
