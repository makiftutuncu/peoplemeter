name := "Peoplemeter"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache, "mysql" % "mysql-connector-java" % "5.1.8"
)     

play.Project.playScalaSettings
