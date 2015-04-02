import AssemblyKeys._ // Assembly plugin settings

assemblySettings

jarName in assembly := "anki.jar"

name:= "scriptlets"

organization := "eu.lhoest"

scalaVersion := "2.11.6"

libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test"

