scalaVersion := "2.13.3"

name := "ticket-booking"
organization := "com.rochala"
version := "1.0"

val akkaHttpVersion = "10.2.4"
val akkaVersion     = "2.6.13"
val circeVersion    = "0.12.3"

libraryDependencies ++= Seq(
  "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.2",
  "com.typesafe.slick"     %% "slick"                    % "3.3.3",
  "org.postgresql"          % "postgresql"               % "42.1.4",
  "org.slf4j"               % "slf4j-nop"                % "1.6.4",
  "com.typesafe.slick"     %% "slick-hikaricp"           % "3.3.3",
  "com.github.tminglei"    %% "slick-pg"                 % "0.19.5",
  "com.github.tminglei"    %% "slick-pg_circe-json"      % "0.19.5",
  "org.scalatest"          %% "scalatest"                % "3.2.5",
  "com.typesafe.akka"      %% "akka-http"                % akkaHttpVersion,
  "com.typesafe.akka"      %% "akka-actor-typed"         % akkaVersion,
  "com.typesafe.akka"      %% "akka-stream"              % akkaVersion,
  "com.typesafe.akka"      %% "akka-http-testkit"        % akkaHttpVersion % Test,
  "com.typesafe.akka"      %% "akka-actor-testkit-typed" % akkaVersion     % Test,
  "org.scalatest"          %% "scalatest"                % "3.2.7"         % Test,
  "org.scalatestplus"      %% "mockito-3-4"              % "3.2.7.0"       % "test",
  "io.circe"               %% "circe-core"               % circeVersion,
  "io.circe"               %% "circe-generic"            % circeVersion,
  "io.circe"               %% "circe-parser"             % circeVersion,
  "de.heikoseeberger"      %% "akka-http-circe"          % "1.36.0",
  "org.typelevel"          %% "cats-core"                % "2.1.1"
)
