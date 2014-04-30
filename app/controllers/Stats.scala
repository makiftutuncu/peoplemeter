package controllers

import play.api.mvc._
import utilities.{TabItems, SidebarItems, Authenticated}
import play.api.db.DB
import anorm.SqlParser._
import anorm._
import play.api.Play.current
import play.api.templates.Html

/**
 * Stats controller which controls everything about managing stats
 */
object Stats extends Controller {
  def renderChannelsTab = Authenticated { implicit request =>
    val channelStatsParser = {
      get[String]("name") ~ get[Double]("duration") map {
        case name ~ duration => (name, duration)
      }
    }
    val initialStats: List[(String, Double)] = DB.withConnection { implicit c =>
      SQL("""SELECT name, SUM(duration) AS duration FROM channel_stats_view GROUP BY name""").as(channelStatsParser *)
    }
    val totalDuration: Double = initialStats.foldLeft(0.0)(_ + _._2)
    val channelStats: List[(String, Double)] = initialStats.map(s => (s._1, (s._2 / totalDuration) * 100))
    val labels: String = channelStats.map(s => s._1).mkString("\"", "\",\"", "\"")
    val data: String = channelStats.map(s => s._2).mkString(",")
    val script: Html = Html(
      s"""{
        labels : [$labels],
        datasets : [{
          fillColor : "rgba(16,16,255,0.6)",
          strokeColor : "rgba(128,128,128,0.9)",
          data : [$data]
        }]
      }""")
    Ok(views.html.stats(chartScript = script, context = request.context, sidebarItems = SidebarItems.activate("Stats"), tabItems = TabItems.activate("Channels")))
  }

  def renderAgeTab = Authenticated { implicit request =>
    val ageStatsParser = {
      get[Long]("count_of_age") ~ get[Int]("age") map {
        case count ~ age => (count, age)
      }
    }
    val initialStats: List[(Long, Int)] = DB.withConnection { implicit c =>
      SQL(
        """SELECT COUNT(records.id) AS count_of_age,
                  (EXTRACT(YEAR FROM NOW()) - EXTRACT(YEAR FROM people.birth_date)) AS age
           FROM records, people
           WHERE records.house_id = people.house_id AND
                 records.button_number = people.button_number
           GROUP BY age""").as(ageStatsParser *)
    }
    val totalCount: Long = initialStats.foldLeft(0.asInstanceOf[Long])(_ + _._1)
    val ageStats: List[(Double, Int)] = initialStats.map(s => ((s._1.asInstanceOf[Double] / totalCount) * 100, s._2))
    val labels: String = ageStats.map(s => s._2).mkString("\"", "\",\"", "\"")
    val data: String = ageStats.map(s => s._1).mkString(",")
    val script: Html = Html(
      s"""{
        labels : [$labels],
        datasets : [{
          fillColor : "rgba(16,16,255,0.6)",
          strokeColor : "rgba(128,128,128,0.9)",
          data : [$data]
        }]
      }""")
    Ok(views.html.stats(chartScript = script, context = request.context, sidebarItems = SidebarItems.activate("Stats"), tabItems = TabItems.activate("Age")))
  }

  def renderGenderTab = Authenticated { implicit request =>
    val genderStatsParser = {
      get[Long]("count_of_gender") ~ get[Boolean]("people.is_male") map {
        case count ~ gender => (count, gender)
      }
    }
    val initialStats: List[(Long, Boolean)] = DB.withConnection { implicit c =>
      SQL(
        """SELECT COUNT(records.id) AS count_of_gender, people.is_male AS gender
           FROM records, people
           WHERE records.house_id = people.house_id AND records.button_number = people.button_number
           GROUP BY gender""").as(genderStatsParser *)
    }
    val totalCount: Long = initialStats.foldLeft(0.asInstanceOf[Long])(_ + _._1)
    val genderStats: List[(Double, Boolean)] = initialStats.map(s => ((s._1.asInstanceOf[Double] / totalCount) * 100, s._2))
    val labels: String = genderStats.map(s => if(s._2) "Male" else "Female").mkString("\"", "\",\"", "\"")
    val data: String = genderStats.map(s => s._1).mkString(",")
    val script: Html = Html(
      s"""{
        labels : [$labels],
        datasets : [{
          fillColor : "rgba(16,16,255,0.6)",
          strokeColor : "rgba(128,128,128,0.9)",
          data : [$data]
        }]
      }""")
    Ok(views.html.stats(chartScript = script, context = request.context, sidebarItems = SidebarItems.activate("Stats"), tabItems = TabItems.activate("Gender")))
  }
}