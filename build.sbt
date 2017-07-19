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
  libraryDependencies ++= Seq(
    "com.typesafe.slick" %% "slick" % "3.2.0",
    "org.slf4j" % "slf4j-nop" % "1.6.4",
    "com.typesafe.slick" %% "slick-hikaricp" % "3.2.0",
    "mysql" % "mysql-connector-java" % "5.1.18",
    "com.typesafe" % "config" % "1.2.1",
    "com.typesafe.akka" %% "akka-stream-kafka" % "0.16",
    "com.typesafe.akka" %% "akka-stream" % "2.4.2",
    "org.json4s" %% "json4s-core" % "3.2.10",
    "org.json4s" %% "json4s-native" % "3.2.10",
    "org.json4s" %% "json4s-jackson" % "3.2.10"
  ) ++ Seq(
    "simpleclient"
    , "simpleclient_hotspot"
    , "simpleclient_common"
    , "simpleclient_pushgateway"
  ).map("io.prometheus" % _ % "0.0.16")
}





