package models

import anorm._
import anorm.SqlParser._
import utilities.Generators
import play.api.db.DB
import play.api.Logger
import play.api.Play.current

/**
 * Model of a session
 *
 * @param id        Id of the session
 * @param accountId Id of the account to which session belongs
 */
case class Session(id: String, accountId: Long)

/**
 * Companion object of [[models.Session]] acting as data access layer
 */
object Session {
  /**
   * A result set parser for session records in database, maps records to a [[models.Session]]
   */
  val sessionParser = {
    get[String]("id") ~ get[Long]("account_id") map {
      case id ~ accountId => Session(id, accountId)
    }
  }

  /**
   * Creates a session and inserts it to the database
   *
   * @param accountId Id of the account
   *
   * @return          Inserted [[models.Session]] optionally if successful, None if any error occurs
   */
  def create(accountId: Long): Option[Session] = {
    try {
      val id = Generators.generateUUID
      DB.withConnection { implicit c =>
        val insertResult: Int = SQL(
          """insert into sessions (id, account_id)
             values ({id}, {accountId})""")
          .on("id" -> id, "accountId" -> accountId).executeUpdate()
        if(insertResult > 0)
          Option(Session(id, accountId))
        else {
          Logger.error(s"Session.create() - Session creation failed for $accountId, cannot insert!")
          None
        }
      }
    }
    catch {
      case e: Exception =>
        Logger.error(s"Session.create() - Session creation failed for $accountId, ${e.getMessage}")
        None
    }
  }

  /**
   * Reads a session with given id from the database
   *
   * @param id  Id of the session
   *
   * @return    An optional [[models.Session]] if found, None if not found
   */
  def read(id: String): Option[Session] = {
    try {
      DB.withConnection { implicit c =>
        SQL(
          """select id, account_id from sessions
             where id={id} limit 1""").on("id" -> id).as(sessionParser singleOpt)
      }
    }
    catch {
      case e: Exception =>
        Logger.error(s"Session.read() - Session reading failed for $id, ${e.getMessage}")
        None
    }
  }

  /**
   * Reads a session with given account id from the database
   *
   * @param accountId Id of the user to which session belongs
   *
   * @return          An optional [[models.Session]] if found, None if not found
   */
  def read(accountId: Long): Option[Session] = {
    try {
      DB.withConnection { implicit c =>
        SQL(
          """select id, account_id from sessions
             where account_id={accountId} limit 1""").on("accountId" -> accountId).as(sessionParser singleOpt)
      }
    }
    catch {
      case e: Exception =>
        Logger.error(s"Session.read() - Session reading failed for $accountId, ${e.getMessage}")
        None
    }
  }

  /**
   * Deletes a session with given id from the database
   *
   * @param id  Id of the session
   *
   * @return    true if successfully deleted, false if any error occurs
   */
  def delete(id: String): Boolean = {
    try {
      DB.withConnection { implicit c =>
        SQL("""delete from sessions where id={id}""")
          .on("id" -> id).executeUpdate() > 0
      }
    }
    catch {
      case e: Exception =>
        Logger.error(s"Session.delete() - Session deleting failed for $id, ${e.getMessage}")
        false
    }
  }
}
