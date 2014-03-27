package models

import anorm._
import anorm.SqlParser._
import play.api.db.DB
import play.api.Logger
import play.api.Play.current
import utilities.Generators

/**
 * A model for keeping an account
 *
 * @param id        Id of the account which is an auto incremented number
 * @param email     Email address of the account
 * @param password  Password of the account
 * @param name      Name of the account owner
 */
case class Account(id: Long, email: String, password: String, name: String)

/**
 * Companion object of Account acting as data access layer
 */
object Account {
  /**
   * A result set parser for account records in database, maps records to an Account object
   */
  val accountParser = {
    get[Long]("id") ~ get[String]("email") ~ get[String]("password") ~ get[String]("name") map {
      case id ~ email ~ password ~ name => Account(id, email, password, name)
    }
  }

  /**
   * Creates an account with given information and inserts it to the database
   *
   * @param email       Email address of the account
   * @param rawPassword Raw password of the account that will be hashed
   * @param name        Name of the account owner
   *
   * @return            An optional [[models.Account]] if successful, None if any error occurs
   */
  def create(email: String, rawPassword: String, name: String): Option[Account] = {
    try {
      val password = Generators.generateSHA512(rawPassword)

      DB.withConnection { implicit c =>
        val insertResult: Option[Long] = SQL(
          """insert into accounts (email, password, name)
             values ({email}, {password}, {name})""")
          .on("email" -> email, "password" -> password, "name" -> name).executeInsert()
        if(!insertResult.isEmpty)
          Option(Account(insertResult.get, email, password, name))
        else {
          Logger.error(s"Account.create() - Account creation failed for $name <$email>, cannot insert!")
          None
        }
      }
    }
    catch {
      case e: Exception =>
        Logger.error(s"Account.create() - Account creation failed for $name <$email>, ${e.getMessage}")
        None
    }
  }

  /**
   * Reads an account by given id from the database
   *
   * @param id  Id of the account which is an auto incremented number
   *
   * @return    An optional [[models.Account]] if successful, None if any error occurs
   */
  def read(id: Long): Option[Account] = {
    try {
      DB.withConnection { implicit c =>
        SQL(
          """select id, email, password, name from accounts
             where id={id} limit 1""").on("id" -> id).as(accountParser singleOpt)
      }
    }
    catch {
      case e: Exception =>
        Logger.error(s"Account.read() - Account reading failed with id id, ${e.getMessage}")
        None
    }
  }

  /**
   * Reads an account by given id from the database
   *
   * @param email       Email address of the account
   *
   * @return    An optional [[models.Account]] if successful, None if any error occurs
   */
  def read(email: String): Option[Account] = {
    try {
      DB.withConnection { implicit c =>
        SQL(
          """select id, email, password, name from accounts
             where email={email} limit 1""").on("email" -> email)
        .as(accountParser singleOpt)
      }
    }
    catch {
      case e: Exception =>
        Logger.error(s"Account.read() - Account reading failed with email $email, ${e.getMessage}")
        None
    }
  }
}