import java.io.{File, FileWriter, PrintWriter}
import java.util.{Calendar, Date}

import scala.util.matching.Regex


object Test extends App {




    //
    //    val result: Option[String] = pattern.findFirstIn(record)
    //    val createTime = result match {
    //        case Some(x) =>{
    //            val pattern(tag,value) = x
    //            value
    //        }
    //        case None => 0
    //    }




    var filePrefix = "0"

    val timestamp = 1500594602000l

    val time = timestamp - timestamp % 3600000 //按照小时进行切割
    val c = Calendar.getInstance()
    c.setTimeInMillis(time)


    val systemNow = System.currentTimeMillis()
    val systemCalendar = Calendar.getInstance()
    systemCalendar.setTimeInMillis(systemNow)


    //计算当前系统时间与接受到的数据中的时间戳时间间隔几天
    val diffDay = ((systemNow - systemNow % 3600000)-time)/3600000
    //当时间戳与记录的时间戳相隔1天 或者 都在当天但是当前小时要大于记录中时间戳代表的时间
    if (diffDay>=1 || (diffDay==0&&(systemCalendar.get(Calendar.HOUR_OF_DAY) - c.get(Calendar.HOUR_OF_DAY))>0)){
        filePrefix = "1"
    }

    println(filePrefix)


//    /**
//      * 将接受到的数据保存到本地文件中
//      * @param values
//      */
//    def saveMsg(values: Seq[String],file: File) ={
//        val out = new FileWriter(file,true)
//        values.foreach{
//            value => out.write(value+"\n")
//        }
//        out.close();
//
//    }
////
//    val values = Seq("1","2","3","4","5","6","7","8","9","10")
////
////
////    saveMsg(values,new File("/home/lms/text.txt"))
//
//
//    val record = "[{\"createTime\":1500102052363,\"ecpm\":0.0,\"pubAppAdId\":0,\"pubId\":0,\"pubAppId\":0,\"reqId\":\"97\",\"adType\":0,\"dspType\":0,\"ip\":null,\"longitude\":0.0,\"latitude\":0.0,\"machType\":null,\"netCoonType\":null,\"tMobile\":null,\"imei\":null,\"imsi\":null,\"did\":null,\"apiVersion\":0,\"dspId\":0,\"dspAppId\":0,\"operator\":null,\"expireTime\":0,\"ua\":null,\"uaMd5\":null,\"calcCount\":1,\"time\":1500102052363},{\"createTime\":1500102052363,\"ecpm\":0.0,\"pubAppAdId\":0,\"pubId\":0,\"pubAppId\":0,\"reqId\":\"97\",\"adType\":0,\"dspType\":0,\"ip\":null,\"longitude\":0.0,\"latitude\":0.0,\"machType\":null,\"netCoonType\":null,\"tMobile\":null,\"imei\":null,\"imsi\":null,\"did\":null,\"apiVersion\":0,\"dspId\":0,\"dspAppId\":0,\"operator\":null,\"expireTime\":0,\"ua\":null,\"uaMd5\":null,\"calcCount\":1,\"time\":1500102052363}]"
//
//
////    val numitemPattern = """([0-9]+) ([a-z]+)""".r
////
////    val numitemPattern(num,item) = "99 bottles"//将num设为99，item设为bottles
////
////    println(num+"\t"+item)
//
//
//
////    val numitemPattern="""([0-9]+) ([a-z]+)""".r
////    val line="93459 spark"
////    line match{
////        case numitemPattern(num,blog)=> println(num+"\t"+blog)
////        case _=>println("hahaha...")
////    }
////
//
//    val pattern = """"(createTime"):(\d+)""".r
//
//    val result: Option[String] = pattern.findFirstIn(record)
//    val createTime = result match {
//        case Some(x) =>{
//            val pattern(tag,value) = x
//            value
//        }
//        case None => 0
//    }
//
//    println(createTime)
//    val timestamp = 1500102052363l
//
//    val time = timestamp - timestamp % 3600000
//
////    val date = new Date
////    date.setTime(time)
//
//    val c = Calendar.getInstance()
//    c.setTimeInMillis(timestamp)
//
//
//
//
//    val rootPath = "/home/lms/"
////    val timePath = c.get(Calendar.YEAR)+"/"+formateTimeElemStyle(c.get(Calendar.MONTH)+1)+"/"+formateTimeElemStyle(c.get(Calendar.DATE))+"/"+formateTimeElemStyle(c.get(Calendar.HOUR_OF_DAY))
//
//    val timeElemList =  c.get(Calendar.YEAR) ::  formateTimeElemStyle(c.get(Calendar.MONTH) + 1) :: formateTimeElemStyle(c.get(Calendar.DATE)) :: formateTimeElemStyle(c.get(Calendar.HOUR_OF_DAY)) :: Nil
//    val timePath = timeElemList.mkString("/")
//
//    println(s"timePath================$timePath")
//
//    val fileName = "adx-logs.txt"
//
//    val fullPath = new File(rootPath+timePath,fileName)
//
//    if (!fullPath.exists()){
//        if (!fullPath.getParentFile.exists()){
//            fullPath.getParentFile.mkdirs();
//        }
//        fullPath.createNewFile()
//    }
//
//    saveMsg(values,fullPath)
//
//
//
//
//
//
//
//
////    println(timePath)
//
//
//
//    def formateTimeElemStyle(elem: Int) ={
//        if(elem<10) "0"+elem else elem.toString
//    }
//
//
////    println(time)
//
////
////    val pattern(tag,value) = result.getOrElse("没有")
////
////    println(value)
//
////
////    val pattern(createTimeTag,ceateTime) = record
////         println(ceateTime)
////    println(time)
//
////    val matche: Option[String] = timeRegex.findFirstIn(record)
//
//
//
//    //not found
////    println("test")


}