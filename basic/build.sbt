import Dependencies._
import Keys._

Build.Settings.project

name := "basic"

libraryDependencies ++= Seq(
  akka.analytics_cassandra % "provided",
  spark.core % "provided",
  spark.mllib % "provided",
  hadoop.client % "provided",

  akka.analytics_cassandra % "runtime",
  spray.client,
  akka.persistence % "runtime",
  akka.chill % "runtime",
  guava % "runtime",
  breeze % "runtime",
  breeze_natives % "runtime"
)
