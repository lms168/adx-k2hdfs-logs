package com.zzcm.util

import java.io.{File, FileWriter}

/**
  * Created by lms on 17-7-19.
  */
object FileUtil {
  /**
    * 将数据写入到文件文件中
    * @param value   要写入的值
    * @param file    文件引用
    * @param append  追加还是覆盖(true:追加, false:覆盖),默认是追加
    */
    def  writeToFile(value: String, file: File, append: Boolean = true) = {
    if (!file.exists()) {
      if (!file.getParentFile.exists()) {
        file.getParentFile.mkdirs();
      }
      file.createNewFile()
    }
    val out = new FileWriter(file, append)
    out.write(value + "\n")
    out.close()
  }

}
