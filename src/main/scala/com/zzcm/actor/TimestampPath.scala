package com.zzcm.actor

import java.io.File
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
   def initTimestampFile(timestamp: Long, rootPath: String, fileName: String): File = {
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
