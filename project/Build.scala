import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "simpleBanking"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    jdbc,
    anorm
  )

 lazy val scct_settings = Defaults.defaultSettings ++ Seq(ScctPlugin.instrumentSettings: _*)


  val main = play.Project(appName, appVersion, appDependencies,settings = scct_settings).settings(
    // Add your own project settings here      
  )

}
