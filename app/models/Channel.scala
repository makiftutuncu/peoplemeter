package models

import anorm.SqlParser._
import anorm._
import play.api.db.DB
import anorm.~
import play.api.Logger
import play.api.Play.current

/**
 * A model for keeping a channel
 *
 * @param id            Id of the channel which is an auto incremented number
 * @param name          Email address of the channel
 * @param logoFilePath  Path to logo image of the channel
 */
case class Channel(id: Long, name: String, logoFilePath: String)

/**
 * Companion object of Channel acting as data access layer
 */
object Channel {
  /**
   * A result set parser for channel records in database, maps records to an Channel object
   */
  val channelParser = {
    get[Long]("id") ~ get[String]("name") ~ get[String]("logo_file_path") map {
      case id ~ name ~ logoFilePath => Channel(id, name, logoFilePath)
    }
  }

  /**
   * Creates an channel with given information and inserts it to the database
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
   * @param id  Id of the channel which is an auto incremented number
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
        Logger.error(s"Channel.read() - Channel reading failed with id $id, ${e.getMessage}")
        None
    }
  }
}
