package com.zzcm

/**
  * Created by lms on 17-9-1.
  */
trait MsgEtl[T]{
    def extract(msg :List[T]):Seq[String]
}
