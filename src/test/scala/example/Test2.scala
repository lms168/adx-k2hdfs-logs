package example

import java.text.DateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.regex.Pattern

/**
  * Created by lms on 17-7-24.
  */
object Test2 extends App{
  val p  = """"create\s+table\s+(.*?)\(.*""".r
  var sql = "create  table a(createTime bigint)"



  println(p.pattern.matcher(sql).matches())



  val pattern = "create\\s+(external)?\\s+table\\s+(.*?)\\(.*"
  val r = Pattern.compile(pattern)
  val m = r.matcher("create  table a(createTime bigint)")
  if(m.matches()){
    println(m.group(2))
  }

  val date = LocalDate.now()
  println(date.plusDays(3))
  val formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")
  val text = date.format(formatter)



  println(s"text=$text")
  val parseDate = LocalDate.parse(text,formatter)
  println(parseDate.getMonth.getValue)


}
