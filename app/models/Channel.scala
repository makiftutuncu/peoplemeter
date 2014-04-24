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
 * @param name          Name of the channel
 */
case class Channel(id: Long, name: String) extends PeoplemeterModel

/**
 * Companion object of [[models.Channel]] acting as data access layer
 */
object Channel {
  /**
   * A result set parser for channel records in database, maps records to a [[models.Channel]] object
   */
  val channelParser = {
    get[Long]("id") ~ get[String]("name") map {
      case id ~ name => Channel(id, name)
    }
  }

  /**
   * Creates a channel with given information and inserts it to the database
   *
   * @param name  Name of the channel
   *
   * @return      An optional [[models.Channel]] if successful, None if any error occurs
   */
  def create(name: String): Option[Channel] = {
    try {
      DB.withConnection { implicit c =>
        val insertResult: Option[Long] = SQL(
          """insert into channels (name)
             values ({name})""")
          .on("name" -> name).executeInsert()
        if(!insertResult.isEmpty)
          Option(Channel(insertResult.get, name))
        else {
          Logger.error(s"Channel.create() - Channel creation failed for $name, cannot insert!")
          None
        }
      }
    }
    catch {
      case e: Exception =>
        Logger.error(s"Channel.create() - Channel creation failed for $name, ${e.getMessage}")
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
          """select id, name from channels
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
   * Reads list of channels from the database
   *
   * @return  A list of [[models.Channel]] if successful, Nil if not found or any error occurs
   */
  def read: List[Channel] = {
    try {
      DB.withConnection { implicit c =>
        SQL(
          """select id, name from channels""").as(channelParser *)
      }
    }
    catch {
      case e: Exception =>
        Logger.error(s"Channel.read() - Channel reading failed, ${e.getMessage}")
        Nil
    }
  }

  /**
   * Updates a channel with given information on the database
   *
   * @param id    Id of the channel which is an auto incremented number
   * @param name  Name of the channel
   *
   * @return      true if successful, false if any error occurs
   */
  def update(id: Long, name: String): Boolean = {
    try {
      DB.withConnection { implicit c =>
        val affectedRows = SQL("""update channels set name={name} where id={id}""").on("id" -> id, "name" -> name).executeUpdate()
        val result: Boolean = affectedRows > 0
        if(!result) {
          Logger.error(s"Channel.update() - Channel update failed for id $id, cannot update!")
        }
        result
      }
    }
    catch {
      case e: Exception =>
        Logger.error(s"Channel.update() - Channel update failed for id $id, ${e.getMessage}")
        false
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
      "name" -> channel.name
    )

    def fromJSON(json: JsValue): Channel =
    {
      ((json \ "id").asOpt[Long], (json \ "name").asOpt[String]) match
      {
        case (Some(id: Long), Some(name: String)) => Channel(id, name)
        case _ => throw new IllegalArgumentException("Invalid Channel JSON!")
      }
    }
  }
}
