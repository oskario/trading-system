name := "trading-system"

version := "1.0"

scalaVersion := "2.11.0"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
  "org.scalatest" % "scalatest_2.11" % "2.1.3" % "test",
  "org.slf4j" % "slf4j-api" % "1.7.7",
  "org.slf4j" % "log4j-over-slf4j" % "1.7.7",  // for any java classes looking for this
  "ch.qos.logback" % "logback-classic" % "1.1.2",
  "com.typesafe.scala-logging" %% "scala-logging-slf4j" % "2.1.2"
)
