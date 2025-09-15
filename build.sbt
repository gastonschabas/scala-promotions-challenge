ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.7.3"

lazy val root = (project in file("."))
  .settings(
    name := "scala-promotions-challenge",
    libraryDependencies ++= Seq(
      "org.scalatest"     %% "scalatest"       % "3.2.19"   % Test,
      "org.scalatestplus" %% "scalacheck-1-18" % "3.2.19.0" % Test
    )
  )
