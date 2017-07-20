package com.zzcm.actor

import java.io.{File, FileWriter}
import java.util.Calendar

/**
  * Created by lms on 17-7-18.
  */
object TimestampPath {



//  /**
////    * 将接受到的数据保存到本地文件中
////    * @param values
////    */
//  def saveOriginalMsg(values: Seq[String], rootPath: String, fileName: String) ={
//
//
//    val valuesGroup: Map[Long, List[String]] = values.map(value=>(extractTimestamp(value) -> value))   //(timestamp->record)
//      .toList
//      .groupBy(_._1)
//      .map( x =>(x._1, x._2.map(_._2)))
//
//
//    //对于没有正确提取到时间戳的数据,单独保存在根目录下的一个名字叫${rootPath}/yyyy-mm-dd/originalError.txt的文件夹
//    val errorValues = valuesGroup.filter(elem=>elem._1 == -1).flatMap(_._2).mkString("/")
//    FileUtil.writeToFile(errorValues,new File(rootPath,"originalError.txt"))
//
//
//    //保存正常的数据
//    valuesGroup.filter(elem=>elem._1 != -1)
//      .foreach(elem  => FileUtil.writeToFile(elem._2.mkString("\n"), initTimestampFile(elem._1, rootPath, fileName)))
//  }
//
//
//
//  /**
//    * 将接受到的数据保存到本地文件中
//    * @param values
//    */
//  def saveFormateMsg(values: Seq[String],rootPath: String, fileName: String) ={
//    values.map(value=>(extractTimestamp(value) -> value))   //(timestamp->record)
//      .toList
//      .groupBy(_._1)
//      .map( x =>(x._1, x._2.map(_._2)))                      //(timestamp->List(record))
//
//      .filter(elem=>elem._1 != -1)                                //对于没有正确提取到时间戳的数据,直接丢弃
//      .foreach(elem  => FileUtil.writeToFile(elem._2.mkString("\n"), initTimestampFile(elem._1, rootPath, fileName)))
//  }
//



  /**
    * 创建文件
    *
    * @param timestamp
    * @return
    */
  def initFormateTimestampFile(timestamp: Long, rootPath: String, fileName: String): File = {
    var filePrefix = "0"
    val time = timestamp - timestamp % 3600000 //按照小时进行切割
    val c = Calendar.getInstance()
    c.setTimeInMillis(time)

    val timeElemList =  c.get(Calendar.YEAR) ::  formateTimeElemStyle(c.get(Calendar.MONTH) + 1) :: formateTimeElemStyle(c.get(Calendar.DATE)) :: formateTimeElemStyle(c.get(Calendar.HOUR_OF_DAY)) :: Nil
    val timePath = timeElemList.mkString("/")


    val systemNow = System.currentTimeMillis()
    val systemCalendar = Calendar.getInstance()
    systemCalendar.setTimeInMillis(systemNow)


   if ((systemNow>timestamp) && (systemCalendar.get(Calendar.HOUR_OF_DAY) - c.get(Calendar.HOUR_OF_DAY))>0){
     filePrefix = "1"
   }

    val finalFileName = filePrefix.concat("-").concat(fileName)


   var file =  new File(rootPath + timePath, finalFileName)

    if (!file.exists()) {  //不存在则创建文件
      if (!file.getParentFile.exists()) {
        file.getParentFile.mkdirs();
      }
      file.createNewFile()
    }else if (finalFileName.startsWith("1")){
        //非正点写入的数据理论上都会写入以1为前缀的文件,但是如果文件正在上传,则上传程序会在该文件夹下面创建一个1-tmp为前缀的文件
        //如果存在1-tmp前缀文件(说明前缀为1的文件正在执行上传文件的操作),则要将数据优先写入到1-temp开头的文件
        // demo非整点上传的例子:例如3点接受到了timestap值为2点的数据,则这些数据就会写入1为前缀的文件
        val listFile: Array[File] = file.getParentFile.listFiles().filter(f=>f.isFile).filter(file=>file.getName.startsWith("1-temp"))
        if (listFile!=null && listFile.length==1){
          file = listFile(0)
        }
    }


    file

  }


  /**
    * 创建文件
    *
    * @param timestamp
    * @return
    */
   def initOriginalTimestampFile(timestamp: Long, rootPath: String, fileName: String): File = {

    val time = timestamp - timestamp % 3600000 //按照小时进行切割
    val c = Calendar.getInstance()
    c.setTimeInMillis(time)

    val timeElemList =  c.get(Calendar.YEAR) ::  formateTimeElemStyle(c.get(Calendar.MONTH) + 1) :: formateTimeElemStyle(c.get(Calendar.DATE)) :: formateTimeElemStyle(c.get(Calendar.HOUR_OF_DAY)) :: Nil
    val timePath = timeElemList.mkString("/")



     new File(rootPath + timePath, fileName)

  }




  /**
    * 提取该条记录的时间戳:可以认为同一批次数据的所有记录的时间戳指是一样的,随意提取一个时间戳值就行
    * @param record
    * @return
    */
   def extractTimestamp(record: String):Long = {
    val pattern = """"(createTime"):(\d+)""".r
    val result: Option[String] = pattern.findFirstIn(record)
    val timeStamp = result match {
      case Some(x) =>{
        val pattern(tag,value) = x
        value.toLong
      }
      case None => {
//        log.error(s"数据msg=$record 未提取到时间戳,请查询发送的数据格式是否正确,该条记录将不会保存到日志文件中")
        println(s"数据msg=$record 未提取到时间戳,请查询发送的数据格式是否正确,该条记录将不会保存到日志文件中")
        throw new Exception((s"数据msg=$record 未提取到时间戳,请查询发送的数据格式是否正确,该条记录将不会保存到日志文件中"))
      }
    }
    return timeStamp
  }


  /**
    * 格式化时间元素,例如将 2 补充为 02
    * @param elem
    * @return
    */
    def formateTimeElemStyle(elem: Int) = {
    if(elem<10) "0"+elem else elem.toString
  }

}
