import sbt._

object Dependencies {

  lazy val akkaVersion = "2.6.8"

  lazy val scalaTestDependencies = Seq(
    "org.scalatest" %% "scalatest" % "3.1.1" % Test
  )

  lazy val akkaDependencies = Seq(
    "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
    "com.typesafe.akka" %% "akka-protobuf" % akkaVersion,
    "com.typesafe.akka" %% "akka-stream" % akkaVersion
  )

  lazy val kafkaDependencies: Seq[ModuleID] = Seq(
    "org.apache.kafka" % "kafka-streams" % "2.2.0",
    "com.typesafe.akka" %% "akka-stream-kafka" % "2.0.4"
  )

  lazy val jsonDependencies: Seq[ModuleID] = Seq(
    "com.typesafe.play" %% "play-json" % "2.7.4",
    "com.typesafe.play" %% "play-json-joda" % "2.7.4"
  )

  lazy val loggingDependencies: Seq[ModuleID] = Seq(
    "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
    "ch.qos.logback" % "logback-classic" % "1.2.3"
  )
}
