name := """awesome"""
organization := "com.vera.camilo"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.12.8"

libraryDependencies += guice
libraryDependencies += javaJpa
libraryDependencies += "com.h2database" % "h2" % "1.4.197"
libraryDependencies += "org.hibernate" % "hibernate-core" % "5.4.3.Final"

libraryDependencies += javaWs % "test"

libraryDependencies += "org.awaitility" % "awaitility" % "3.1.5" % "test"
libraryDependencies += "org.assertj" % "assertj-core" % "3.11.1" % "test"
libraryDependencies += "org.mockito" % "mockito-core" % "2.23.4" % "test"

Test / testOptions += Tests.Argument(TestFrameworks.JUnit, "-a", "-v")

ThisBuild / javacOptions ++= List("-Xlint:unchecked", "-Xlint:deprecation", "-Werror")

PlayKeys.externalizeResourcesExcludes += baseDirectory.value / "conf" / "META-INF" / "persistence.xml"