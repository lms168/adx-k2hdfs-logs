package example

import com.typesafe.config.ConfigFactory

object Hello extends Greeting with App {

  println(greeting)

  //Load Config
  val config= ConfigFactory.load()
  val httpConfig = config.getConfig("main.http")
  val host = httpConfig.getString("host")
  val port = httpConfig.getInt("port")
  println(s"$host:$port")

}

trait Greeting {
  lazy val greeting: String = "hello"
}
