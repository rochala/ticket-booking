scalaVersion := "2.13.3"

name := "ticket-booking"
organization := "com.rochala"
version := "1.0"

val akkaHttpVersion = "10.2.4"
val akkaVersion = "2.6.13"
val circeVersion = "0.12.3"

libraryDependencies ++= Seq(
  "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.2",
  "com.typesafe.slick" %% "slick" % "3.3.3",
  "com.h2database" % "h2" % "1.4.200",
  "org.slf4j" % "slf4j-nop" % "1.6.4",

  "com.typesafe.akka" %% "akka-http"                % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json"     % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-actor-typed"         % akkaVersion,
  "com.typesafe.akka" %% "akka-stream"              % akkaVersion,
  /* "ch.qos.logback"    % "logback-classic"           % "1.2.3", */

  "com.typesafe.akka" %% "akka-http-testkit"        % akkaHttpVersion % Test,
  "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion     % Test,
  "org.scalatest"     %% "scalatest"                % "3.1.4"         % Test,

  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion,

  "de.heikoseeberger" %% "akka-http-circe" % "1.36.0",

)

