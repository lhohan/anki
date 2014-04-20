import AssemblyKeys._ // Assembly plugin settings

assemblySettings

jarName in assembly := "anki.jar"

name:= "scriptlets"

organization := "eu.lhoest"

libraryDependencies += "org.scalatest" % "scalatest_2.10" % "2.0" % "test"

