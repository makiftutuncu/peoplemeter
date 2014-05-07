package models

import anorm._
import anorm.SqlParser._
import java.util.Date
import play.api.db.DB
import play.api.Logger
import play.api.Play.current
import utilities.JSONSerializable
import play.api.libs.json.{Json, JsValue}

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
case class Record(id: Long, houseId: Long, buttonNumber: Int, channelId: Long, startTime: Date, endTime: Date) extends PeoplemeterModel

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
             values ({houseId}, {buttonNumber}, {channelId}, {startTime}, {endTime})""")
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
   * Reads the last record from the database
   *
   * @param houseId       Id of the house to which this record belongs
   * @param buttonNumber  Button number of the person to which this record belongs
   * @param channelId     Id of the channel being watched during the time of the record
   *
   * @return              An optional [[models.Record]] if successful, None if not found or any error occurs
   */
  def readLast(houseId: Long, buttonNumber: Int, channelId: Long): Option[Record] = {
    try {
      DB.withConnection { implicit c =>
        SQL(
          """select id, house_id, button_number, channel_id, start_time, end_time from records
             where id=(select max(id) from records) AND house_id={houseId} AND button_number={buttonNumber} AND channel_id={channelId}
             limit 1""").on("houseId" -> houseId, "buttonNumber" -> buttonNumber, "channelId" -> channelId)
          .as(recordParser singleOpt)
      }
    }
    catch {
      case e: Exception =>
        Logger.error(s"Record.readLast() - Record reading failed, ${e.getMessage}")
        None
    }
  }

  /**
   * Updates a record with given information on the database
   *
   * @param id            Id of the record which is an auto incremented number
   * @param houseId       Id of the house to which this record belongs
   * @param buttonNumber  Button number of the person to which this record belongs
   * @param channelId     Id of the channel being watched during the time of the record
   * @param startTime     Start time of the record
   * @param endTime       End time of the record
   *
   * @return              true if successful, false if any error occurs
   */
  def update(id: Long, houseId: Long, buttonNumber: Int, channelId: Long, startTime: Date, endTime: Date): Boolean = {
    try {
      DB.withConnection { implicit c =>
        val affectedRows = SQL(
          """update records set house_id={houseId}, button_number={buttonNumber},
             channel_id={channelId}, start_time={startTime}, end_time={endTime} where id={id}""")
          .on("id" -> id, "houseId" -> houseId, "buttonNumber" -> buttonNumber,
            "channelId" -> channelId, "startTime" -> startTime, "endTime" -> endTime).executeUpdate()
        val result: Boolean = affectedRows > 0
        if(!result) {
          Logger.error(s"Record.update() - Record update failed for id $id, cannot update!")
        }
        result
      }
    }
    catch {
      case e: Exception =>
        Logger.error(s"Record.update() - Record update failed for id $id, ${e.getMessage}")
        false
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

  implicit object RecordAsJSON extends JSONSerializable[Record]
  {
    def toJSON(record: Record): JsValue = Json.obj(
      "id" -> record.id,
      "houseId" -> record.houseId,
      "buttonNumber" -> record.buttonNumber,
      "channelId" -> record.channelId,
      "startTime" -> record.startTime,
      "endTime" -> record.endTime
    )

    def fromJSON(json: JsValue): Record =
    {
      ((json \ "id").asOpt[Long],
       (json \ "houseId").asOpt[Long],
       (json \ "buttonNumber").asOpt[Int],
       (json \ "channelId").asOpt[Long],
       (json \ "startTime").asOpt[Date],
       (json \ "endTime").asOpt[Date]) match
      {
        case (Some(id: Long),
              Some(houseId: Long),
              Some(buttonNumber: Int),
              Some(channelId: Long),
              Some(startTime: Date),
              Some(endTime: Date)) => Record(id, houseId, buttonNumber, channelId, startTime, endTime)
        case _ => throw new IllegalArgumentException("Invalid Record JSON!")
      }
    }
  }
}
