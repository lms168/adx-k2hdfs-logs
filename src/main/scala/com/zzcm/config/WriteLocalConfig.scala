package com.zzcm.config

import akka.actor.ActorSystem

import com.typesafe.config.Config

/**
  * Created by lms on 17-8-23.
  */
/**
  * Created by lms on 17-8-23.
  */
final case class  WriteLocalConfig(
                                   originalSwitch: Boolean
                                   ,formateSwitch: Boolean
                                   , originalRootPath: String
                                   , originalFileName: String
                                   , formateRootPath: String
                                   , formateFileName: String
                                   , onTimeFileSign: String
                                   , uploadedSign: String
                                   , uploadWaitTime: Long)

object WriteLocalConfig {
  def apply(config: Config): WriteLocalConfig = {
    val c = config.getConfig("adx-logs.file")
    WriteLocalConfig(


       originalSwitch= c.getBoolean("original.switch")
      ,formateSwitch = c.getBoolean("formate.switch")
      ,originalRootPath = c.getString("original.rootPath")
      , originalFileName= c.getString("original.fileName")
      , formateRootPath = c.getString("formate.rootPath")
      , formateFileName = c.getString("formate.fileName")
      , onTimeFileSign = c.getString("onTimeFileSign")
      , uploadedSign = c.getString("uploadedSign")
      , uploadWaitTime = c.getDuration("uploadWaitTime").toMillis )
  }
  def apply()(implicit  system: ActorSystem): WriteLocalConfig = this.apply(system.settings.config)
}
