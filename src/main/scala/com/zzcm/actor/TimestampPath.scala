package com.zzcm.actor

import java.io.File

import com.zzcm.util.DateUtil

/**
  * Created by lms on 17-7-18.
  */
object TimestampPath {



  /**
    * 创建文件
    *
    * @param timestamp
    * @return
    */
  def initFormateTimestampFile(timestamp: Long, rootPath: String, fileName: String): File = {
    val time = timestamp - timestamp % 3600000 //按照小时进行切割

    val timePath = DateUtil.formateTimestampPath(time)

    val dirPath = new File(rootPath + timePath) //如果当前的写入目录不存在,则创建当前文件的写入目录
    if (!dirPath.exists()){
      dirPath.mkdirs();
    }



    //计算当前系统时间与接受到的数据中的时间戳时间间隔几个小时
    val systemNow = System.currentTimeMillis()
    val diffHour = ((systemNow - systemNow % 3600000)-time)/3600000
    //当时间戳与记录的时间戳相隔1天 或者 都在当天但是当前小时要大于记录中时间戳代表的时间
   if (diffHour>=1){

     //找寻跨点写入的所有文件
     val listFile: Array[File] = dirPath.listFiles().filter(x=>x.isFile).filterNot(x=>(x.getName.startsWith("0-")||(x.getName.startsWith("uploaded"))))

     if (listFile!=null&&listFile.size>0){  //存在一系列时间戳为前缀名的文件,则选择时间戳离现在最近的那个文件写入
       listFile.map(x=>{
         val fileNameInfo = x.getName.split("-")
         (fileNameInfo(0),x)
       }).reduceLeft((x,y)=>{
         if (x._1.compareTo(y._1)>0){
           x
         }else{
           y
         }
       })._2
     } else {  //不存在时间戳为前缀名的文件,则创建一个时间时间戳为前缀的文件
       val file = new File(dirPath,systemNow.toString.concat("-").concat(fileName))
       if (!file.exists()) {
         file.createNewFile()
       }
       file
     }



   } else { //正点写入
     val file = new File(dirPath, "0".concat("-").concat(fileName))
     if (!file.exists()) {  //不存在则创建文件
       file.createNewFile()
     }
     file
   }

  }


  /**
    * 创建文件
    *
    * @param timestamp
    * @return
    */
   def initOriginalTimestampFile(timestamp: Long, rootPath: String, fileName: String): File = {
    val time = timestamp - timestamp % 3600000 //按照小时进行切割
    val timePath = DateUtil.formateTimestampPath(time)
     new File(rootPath + timePath, fileName)
  }




  /**
    * 提取该条记录的时间戳:可以认为同一批次数据的所有记录的时间戳指是一样的,随意提取一个时间戳值就行
    * @param record
    * @return
    */
   def extractTimestamp(record: String):Long = {
    val pattern = """"(timestamp"):(\d+)""".r
    val result: Option[String] = pattern.findFirstIn(record)
    val timeStamp = result match {
      case Some(x) =>{
        val pattern(tag,value) = x
        value.toLong
      }
      case None => {
//        log.error(s"数据msg=$record 未提取到时间戳,请查询发送的数据格式是否正确,该条记录将不会保存到日志文件中")
//        println(s"数据msg=$record 未提取到时间戳,请查询发送的数据格式是否正确,该条记录将不会保存到日志文件中")
        throw new Exception((s"数据msg=$record 未提取到时间戳,请查询发送的数据格式是否正确,该条记录将不会保存到日志文件中"))
      }
    }
    return timeStamp
  }




}
