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
    "ch.qos.logback"  %  "logback-classic"  % "1.1.7"
    , "org.slf4j"     %  "log4j-over-slf4j" % "1.7.21"
  ) ++ Seq(
    // "org.typelevel" %% "cats" % "0.9.0"
    "com.typesafe.akka"       %% "akka-stream-kafka"      % "0.14" exclude("log4j", "log4j") exclude("org.slf4j","slf4j-log4j12")
    , "com.typesafe.slick"  %% "slick"                 % slickV
    , "com.typesafe.slick"  %% "slick-hikaricp"        % slickV
    , "joda-time"           %  "joda-time"             % "2.9.4"
    , "org.mariadb.jdbc"    %  "mariadb-java-client"   % "1.3.7"
    , "org.json4s"          %% "json4s-jackson"        % "3.4.2"
    , "com.fasterxml.jackson.core" % "jackson-databind" % "2.8.4"

    // , "org.apache.spark"    %% "spark-core"            % "2.1.0" % "provided"
    // , "org.apache.spark"    %% "spark-sql"            % "2.1.0"  // %  "provided"
    // , "org.apache.hadoop"   % "hadoop-client"          % "2.7.3"

  )++ Seq(
    "hadoop-common"
    , "hadoop-hdfs"
    , "hadoop-client"
  ).map("org.apache.hadoop" % _ % hadoopV)
    ).map(_.excludeAll(
    // ExclusionRule("commons-logging", "commons-logging"),
    // ExclusionRule("log4j", "log4j"),
    ExclusionRule("org.log4j")
  ))



}





