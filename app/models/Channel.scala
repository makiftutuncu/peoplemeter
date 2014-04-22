package models

import anorm.SqlParser._
import anorm._
import play.api.db.DB
import anorm.~
import play.api.Logger
import play.api.Play.current
import utilities.JSONSerializable
import play.api.libs.json.{Json, JsValue}

/**
 * A model for keeping a channel
 *
 * @param id            Id of the channel which is an auto incremented number
 * @param name          Email address of the channel
 * @param logoFilePath  Path to logo image of the channel
 */
case class Channel(id: Long, name: String, logoFilePath: String) extends PeoplemeterModel

/**
 * Companion object of [[models.Channel]] acting as data access layer
 */
object Channel {
  /**
   * A result set parser for channel records in database, maps records to a [[models.Channel]] object
   */
  val channelParser = {
    get[Long]("id") ~ get[String]("name") ~ get[String]("logo_file_path") map {
      case id ~ name ~ logoFilePath => Channel(id, name, logoFilePath)
    }
  }

  /**
   * Creates a channel with given information and inserts it to the database
   *
   * @param name          Email address of the channel
   * @param logoFilePath  Path to logo image of the channel
   *
   * @return              An optional [[models.Channel]] if successful, None if any error occurs
   */
  def create(name: String, logoFilePath: String): Option[Channel] = {
    try {
      DB.withConnection { implicit c =>
        val insertResult: Option[Long] = SQL(
          """insert into channels (name, logo_file_path)
             values ({name}, {logoFilePath})""")
          .on("name" -> name, "logoFilePath" -> logoFilePath).executeInsert()
        if(!insertResult.isEmpty)
          Option(Channel(insertResult.get, name, logoFilePath))
        else {
          Logger.error(s"Channel.create() - Channel creation failed for $name - $logoFilePath, cannot insert!")
          None
        }
      }
    }
    catch {
      case e: Exception =>
        Logger.error(s"Channel.create() - Channel creation failed for $name - $logoFilePath, ${e.getMessage}")
        None
    }
  }

  /**
   * Reads an channel by given id from the database
   *
   * @param id  Id of the channel
   *
   * @return    An optional [[models.Channel]] if successful, None if any error occurs
   */
  def read(id: Long): Option[Channel] = {
    try {
      DB.withConnection { implicit c =>
        SQL(
          """select id, name, logo_file_path from channels
             where id={id} limit 1""").on("id" -> id).as(channelParser singleOpt)
      }
    }
    catch {
      case e: Exception =>
        Logger.error(s"Channel.read() - Channel reading failed for $id, ${e.getMessage}")
        None
    }
  }

  /**
   * Deletes a channel with given id from the database
   *
   * @param id  Id of the channel
   *
   * @return    true if successfully deleted, false if any error occurs
   */
  def delete(id: Long): Boolean = {
    try {
      DB.withConnection { implicit c =>
        SQL("""delete from channels where id={id}""")
          .on("id" -> id).executeUpdate() > 0
      }
    }
    catch {
      case e: Exception =>
        Logger.error(s"Channel.delete() - Channel deleting failed for $id, ${e.getMessage}")
        false
    }
  }

  implicit object ChannelAsJSON extends JSONSerializable[Channel]
  {
    def toJSON(channel: Channel): JsValue = Json.obj(
      "id" -> channel.id,
      "name" -> channel.name,
      "logoFilePath" -> channel.logoFilePath
    )

    def fromJSON(json: JsValue): Channel =
    {
      ((json \ "id").asOpt[Long], (json \ "name").asOpt[String], (json \ "logoFilePath").asOpt[String]) match
      {
        case (Some(id: Long), Some(name: String), Some(logoFilePath: String)) => Channel(id, name, logoFilePath)
        case _ => throw new IllegalArgumentException("Invalid Channel JSON!")
      }
    }
  }
}
