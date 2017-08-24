package com.zzcm.util

import java.util.Calendar

/**
  * Created by lms on 17-8-17.
  */
object DateUtil {

  /**
    * 将毫秒时间戳转换为 yyyy/MM/dd/day  的路径
    * @param milliSeconds
    * @return
    */
  def formateTimestampPath(milliSeconds: Long): String ={
    val c = Calendar.getInstance()
    c.setTimeInMillis(milliSeconds)
    val timeElemList =  c.get(Calendar.YEAR) ::  DateUtil.formateTimeElemStyle(c.get(Calendar.MONTH) + 1) :: DateUtil.formateTimeElemStyle(c.get(Calendar.DATE)) :: DateUtil.formateTimeElemStyle(c.get(Calendar.HOUR_OF_DAY)) :: Nil
    timeElemList.mkString("/")
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
