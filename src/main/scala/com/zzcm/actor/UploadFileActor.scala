package com.zzcm.actor

import java.io.File

import scala.collection.mutable.ArrayBuffer

import akka.actor.{Actor, ActorSystem, Props}
import akka.event.Logging

import com.zzcm.actor.UploadFileActor.PathMsg
import com.zzcm.config.K2hdfsConfig
import com.zzcm.upload.HdfsClient

object UploadFileActor {
  case class PathMsg(path: String)
  def props() = Props(new UploadFileActor())
}

/**
  * Created by lms on 17-8-17.
  */
class UploadFileActor() extends Actor{

  val log = Logging(context.system, this)

  private[this] implicit val system: ActorSystem = context.system

  val k2hdfsConfig = K2hdfsConfig()
  val writeLocalConfig = k2hdfsConfig.writeToLocalConfig
  val uploadConfig  = k2hdfsConfig.uploadConfig

  //正点写入文件前缀
  val onTimeFileSign = writeLocalConfig.onTimeFileSign
  //已上传文件前缀
  val uploadedSign = writeLocalConfig.uploadedSign
  //文件5分钟没有发生变更则认为该文件可以上传了
  val uploadWaitTime = writeLocalConfig.uploadWaitTime
  //本地要上传文件所在的根目录
  val srcPath = uploadConfig.srcPath
  //要上传的文件目录
  val destHdfsPath = uploadConfig.destHdfsPath
  //hdfs的路径
  val defaultFS = uploadConfig.defaultFS
  //hdfs的用户
  val user = uploadConfig.user


  override def receive: Receive = {
    case PathMsg(timePath) => {

      val path  = srcPath.concat(timePath)
      //   /home/lms/adx-logs/original/2017/08/23/06
//       log.info(s"path======================$path")

      val uploadedfileNames = new ArrayBuffer[String]

      val dir = new File(path)
      if (dir.exists()) {

        //1.获取所有未上传的文件
        val unUploadFiles = dir.listFiles().filter(x => x.isFile).filterNot(x => x.getName.startsWith(uploadedSign))

        //2.对于所有未上传的0开头的文件:如果跨小时了且该以0开始的文件离现在有五分钟(防止服务器时间不准造成写和上传冲突)未改动了,则上传该以0开头的文件
        val onTimeFiles = unUploadFiles.filter(x => x.getName.startsWith(onTimeFileSign))
        if (onTimeFiles != null && onTimeFiles.length > 0) {
          val onTimeFile = onTimeFiles(0)
          val uploadFileName = upload(path, onTimeFile, defaultFS, user)
          uploadFileName match {
            case Some(fileName) =>  uploadedfileNames += fileName
            case _ =>
          }


        }

        //3.对于所有跨点的文件,找时间戳前缀离现在最近的那个文件,如果五分钟未变动了,则创建一个新的文件,并上传该文件
        val crossTimeFiles = unUploadFiles.filterNot(x => x.getName.startsWith(onTimeFileSign))
        if (crossTimeFiles != null && crossTimeFiles.size > 0) {
          //存在一系列时间戳为前缀名的文件,则选择时间戳离现在最近的那个文件写入
          val crossFile: File = crossTimeFiles.map(x => {
            val fileNameInfo = x.getName.split("-")
            (fileNameInfo(0), x)
          }).reduceLeft((x, y) => {
            if (x._1.compareTo(y._1) > 0) {
              x
            } else {
              y
            }
          })._2
          val uploadFileName = upload(path, crossFile, defaultFS, user)
          uploadFileName match {
            case Some(fileName) =>  uploadedfileNames += fileName
            case _ =>
          }
        }
      }

      if (uploadedfileNames.length>0)
        sender() !  Some(path.concat("==uploaded==").concat(uploadedfileNames.mkString("[",",","]")))
      else
        sender() !  None

    }


      def upload(path: String, file: File, defaultFS: String, user: String) = {
        val now = System.currentTimeMillis()
        val lastModifyTime = file.lastModified()
        //如果5分钟没有变更过,则开始更改文件名(加上上传标记),然后上传文件
        if (now - lastModifyTime > uploadWaitTime) {
          val newFile = new File(path, uploadedSign.concat(file.getName))
          file.renameTo(newFile)
          val hdfsClient = new HdfsClient
          hdfsClient.init(defaultFS,user)
          hdfsClient.uploadFileToHdfs(path.concat(File.separator).concat(newFile.getName), destHdfsPath)
          Some(file.getName)
        }else{
          None
        }
      }
  }
}
