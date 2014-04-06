package models

import anorm._
import anorm.SqlParser._
import java.util.Date
import play.api.db.DB
import play.api.Logger
import play.api.Play.current

/**
 * A model for keeping a record
 *
 * @param id            Id of the record which is an auto incremented number
 * @param houseId       Id of the house to which this record belongs
 * @param buttonNumber  Button number of the person to which this record belongs
 * @param channelId     Id of the channel being watched during the time of the record
 * @param startTime     Start time of the record
 * @param endTime       End time of the record
 */
case class Record(id: Long, houseId: Long, buttonNumber: Int, channelId: Long, startTime: Date, endTime: Date)

/**
 * Companion object of [[models.Record]] acting as data access layer
 */
object Record {
  /**
   * A result set parser for record records in database, maps records to a [[models.Record]] object
   */
  val recordParser = {
    get[Long]("id") ~ get[Long]("house_id") ~
    get[Int]("button_number") ~ get[Long]("channel_id") ~
    get[Date]("start_time") ~ get[Date]("end_time") map {
      case id ~ houseId ~ buttonNumber ~ channelId ~ startTime ~ endTime =>
        Record(id, houseId, buttonNumber, channelId, startTime, endTime)
    }
  }

  /**
   * Creates a record with given information and inserts it to the database
   *
   * @param houseId       Id of the house to which this record belongs
   * @param buttonNumber  Button number of the person to which this record belongs
   * @param channelId     Id of the channel being watched during the time of the record
   * @param startTime     Start time of the record
   * @param endTime       End time of the record
   *
   * @return              An optional [[models.Record]] if successful, None if any error occurs
   */
  def create(houseId: Long, buttonNumber: Int, channelId: Long, startTime: Date, endTime: Date): Option[Record] = {
    try {
      DB.withConnection { implicit c =>
        val insertResult: Option[Long] = SQL(
          """insert into records (house_id, button_number, channel_id, start_time, end_time)
             values ({familyName}, {district}, {street}, {buildingName}, {doorNumber}, {postalCode}, {town}, {city})""")
          .on("houseId" -> houseId, "buttonNumber" -> buttonNumber,
              "channelId" -> channelId, "startTime" -> startTime, "endTime" -> endTime)
          .executeInsert()
        if(!insertResult.isEmpty)
          Option(Record(insertResult.get, houseId, buttonNumber, channelId, startTime, endTime))
        else {
          Logger.error(s"Record.create() - Record creation failed for house id $houseId, cannot insert!")
          None
        }
      }
    }
    catch {
      case e: Exception =>
        Logger.error(s"Record.create() - Record creation failed for house id $houseId, ${e.getMessage}")
        None
    }
  }

  /**
   * Reads a record by given id from the database
   *
   * @param id  Id of the record
   *
   * @return    An optional [[models.Record]] if successful, None if any error occurs
   */
  def read(id: Long): Option[Record] = {
    try {
      DB.withConnection { implicit c =>
        SQL(
          """select id, house_id, button_number, channel_id, start_time, end_time from record
             where id={id} limit 1""").on("id" -> id).as(recordParser singleOpt)
      }
    }
    catch {
      case e: Exception =>
        Logger.error(s"Record.read() - Record reading failed for $id, ${e.getMessage}")
        None
    }
  }

  /**
   * Deletes a record with given id from the database
   *
   * @param id  Id of the record
   *
   * @return    true if successfully deleted, false if any error occurs
   */
  def delete(id: Long): Boolean = {
    try {
      DB.withConnection { implicit c =>
        SQL("""delete from record where id={id}""")
          .on("id" -> id).executeUpdate() > 0
      }
    }
    catch {
      case e: Exception =>
        Logger.error(s"Record.delete() - Record deleting failed for $id, ${e.getMessage}")
        false
    }
  }
}
