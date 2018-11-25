name := "firebase-demo"

version := "0.1"

scalaVersion := "2.12.5"

libraryDependencies ++= Seq(
  "com.google.firebase" % "firebase-admin" % "4.0.1",
  "com.twitter" %% "finagle-http" % "18.11.0",
  "com.tokbox" % "opentok-server-sdk" % "4.3.0",
  /*"io.circe" %% "circe-generic" % "0.10.1",*/
  "io.circe" %% "circe-parser" % "0.10.1",
  "io.circe" %% "circe-generic" % "0.10.1",
  "com.github.finagle" %% "finch-core" % "0.26.0",
  "com.github.finagle" %% "finch-circe" % "0.26.0",
  "io.circe" %% "circe-optics" % "0.10.0"
)