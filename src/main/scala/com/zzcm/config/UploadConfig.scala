package com.zzcm.config

import akka.actor.ActorSystem

import com.typesafe.config.Config





final case class UploadConfig(
                               user: String
                              , srcPath: String
                              , destHdfsPath: String
                              , defaultFS: String)


object UploadConfig {
  def apply(config: Config): UploadConfig = {
    val c = config.getConfig("upload")
    UploadConfig(
      user = c.getString("user")
      , srcPath = c.getString("srcPath")
      , destHdfsPath = c.getString("destHdfsPath")
      , defaultFS = c.getString("defaultFS"))

  }
  def apply()(implicit  system: ActorSystem): UploadConfig =  this.apply(system.settings.config)
}
