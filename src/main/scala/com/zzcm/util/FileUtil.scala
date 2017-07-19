package com.zzcm.util

import java.io.{File, FileWriter}
import java.util.Calendar

/**
  * Created by lms on 17-7-18.
  */
object FileUtil {
  /**
    * 将接受到的数据保存到本地文件中
    * @param values
    */
  def saveMsg(values: Seq[String],rootPath: String, fileName: String) ={

    values.map(value=>(extractTimestamp(value) -> value))   //(timestamp->record)
//      .filter(_._1 != -1)                                 //对于没有正确提取到时间戳的数据,单独保存在一个名字叫errorFormat的文件夹
      .toList
      .groupBy(_._1)                                        //(timestamp->List(record))
      .map( x =>(x._1, x._2.map(_._2)))
        .foreach(elem  =>{
        val timestamp = elem._1
        val value = elem._2.mkString("\n")              //将该批次的所有数据连接成字符串以换行符为分割
        val file: File = createFile(timestamp, rootPath, fileName)
        val out = new FileWriter(file,true)
        out.write(value + "\n")
        out.close();
      })
  }


  /**
    * 创建文件
    * @param timestamp
    * @return
    */
  private def createFile(timestamp: Long, rootPath: String, fileName: String): File = {
    val time = timestamp - timestamp % 3600000 //按照小时进行切割
    val c = Calendar.getInstance()
    c.setTimeInMillis(time)

    val timeElemList =  c.get(Calendar.YEAR) ::  formateTimeElemStyle(c.get(Calendar.MONTH) + 1) :: formateTimeElemStyle(c.get(Calendar.DATE)) :: formateTimeElemStyle(c.get(Calendar.HOUR_OF_DAY)) :: Nil
    val timePath = timeElemList.mkString("/")

    val file = new File(rootPath + timePath, fileName)
    if (!file.exists()) {
      if (!file.getParentFile.exists()) {
        file.getParentFile.mkdirs();
      }
      file.createNewFile()
    }
    file
  }

  /**
    * 提取该条记录的时间戳:可以认为同一批次数据的所有记录的时间戳指是一样的,随意提取一个时间戳值就行
    * @param record
    * @return
    */
  private def extractTimestamp(record: String):Long = {
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
        -1.toLong
      }
    }
    return timeStamp
  }


  /**
    * 格式化时间元素,例如将 2 补充为 02
    * @param elem
    * @return
    */
  private def formateTimeElemStyle(elem: Int) = {
    if(elem<10) "0"+elem else elem.toString
  }

}
