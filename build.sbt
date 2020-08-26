import Dependencies._

ThisBuild / scalaVersion := "2.12.10"
ThisBuild / crossScalaVersions := Seq("2.12.10", "2.13.2")
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.thiefspin"
ThisBuild / organizationName := "thiefspin"

lazy val root = (project in file("."))
  .settings(
    name := "async-kafka-client",
    libraryDependencies ++=
      akkaDependencies ++
        jsonDependencies ++
        loggingDependencies ++
        kafkaDependencies ++
        scalaTestDependencies
  )
