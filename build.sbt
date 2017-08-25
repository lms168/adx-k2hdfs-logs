//System sbt config
import NativePackagerHelper._

enablePlugins(JavaAppPackaging)
enablePlugins(UniversalPlugin)

mappings in Universal += file("src/main/config/app.sh") -> "app.sh"
mappings in Universal ++= {
  ((sourceDirectory in Compile).value / s"config/${System.getProperty("conf")}" * "*").get.map { f =>
    f -> s"config/${f.name}"
  }
}
addCommandAlias("pkg", ";universal:packageZipTarball")

//Config - Root
lazy val root = (project in file(".")).settings(name := "adx-k2hdfs-logs", commonSettings, librarySettings)

lazy val commonSettings = Seq(
  organization := "com.chinamobiad.adx",
  scalaVersion := "2.11.8",
  version      := "0.1.0"
)

lazy val librarySettings = {
  val akkaV = "2.4.17"
  val akkaHttpV = "10.0.4"
  val slickV = "3.2.0"
  val hadoopV = "2.7.3"

  libraryDependencies ++= (Seq(
    "akka-slf4j"
    , "akka-actor"
    , "akka-stream"
  ).map("com.typesafe.akka" %% _ % akkaV) ++ Seq(
    "akka-http-spray-json"
    , "akka-http-core"
    , "akka-http"
  ).map("com.typesafe.akka" %% _ % akkaHttpV) ++ Seq(
    "simpleclient"
    , "simpleclient_hotspot"
    , "simpleclient_common"
  ).map("io.prometheus" % _ % "0.0.16") ++ Seq(
    //显示的导入slf4j的接口和logback的实现类
    "ch.qos.logback"  %  "logback-classic"  % "1.1.7"
    ,"ch.qos.logback" % "logback-core" % "1.1.7"
    ,"org.slf4j" % "slf4j-api" % "1.7.25"
    ,"org.slf4j" % "jcl-over-slf4j" % "1.7.25"
    ,"org.slf4j" % "log4j-over-slf4j" % "1.7.25"
  ) ++ Seq(
    "com.typesafe.akka"       %% "akka-stream-kafka"      % "0.14"
    , "com.typesafe.slick"  %% "slick"                 % slickV
    , "com.typesafe.slick"  %% "slick-hikaricp"        % slickV
    , "joda-time"           %  "joda-time"             % "2.9.4"
    , "org.mariadb.jdbc"    %  "mariadb-java-client"   % "1.3.7"
    , "org.json4s"          %% "json4s-jackson"        % "3.4.2"
    , "com.fasterxml.jackson.core" % "jackson-databind" % "2.8.4"

  )++ Seq(
    "hadoop-common"
    , "hadoop-hdfs"
    , "hadoop-client"
  ).map("org.apache.hadoop" % _ % hadoopV)
    )
    .map(_.excludeAll(

       ExclusionRule("commons-logging", "commons-logging")   //不要将jcl的api再桥接到slf上面去,否则可能导致死循环stackoverflow
      , ExclusionRule("log4j","log4j")                    //因为使用了logback的使用故不需要log4j的实现包
      , ExclusionRule("org.slf4j","slf4j-log4j12")        //因为使用了logback,logback直接实现了sfl4j的接口,不需要转换器
  ))

}





