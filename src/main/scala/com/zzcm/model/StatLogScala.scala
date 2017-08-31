package com.zzcm.model

/**
  * Created by lms on 17-6-14.
  * 该类中的属性没有使用Option包装
  * 因为通过测试发现,对于None的数据,json4s会直接舍弃该属性
  * 为了最大程度的与原java代码兼容,故该scala的类型都使用原始类型
  */

final case class StatLogScala(
   createTime: Long = 1504075325000l//System.currentTimeMillis
  , ecpm: Double = .0
  , pubAppAdId: Int = 0
  , pubId: Int = 0
  , pubAppId: Int = 0
  , reqId: String = null
  , adType: Int = 0
  , dspType: Int = 0
  , ip: String = null
  , longitude: Double = 0.0
  , latitude: Double = 0.0
  , machType: String = null
  , netCoonType: String = null
  , tMobile: String = null
  , imei: String = null
  , imsi: String = null
  , did: String = null
  , apiVersion: Int = 0
  , dspId: Int = 0
  , dspAppId: Int = 0
  , operator: String = null
  , expireTime: Int = 0
  , ua: String = null
  , uaMd5: String = null
  , calcCount: Int = 1
  , time: Long = System.currentTimeMillis
   , interactionType :Int = 0         //广告类型
   , pkgName     : String = null          //包名
   , title       : String = null          //标题

                             )
