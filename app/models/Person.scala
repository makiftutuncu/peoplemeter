package models

import anorm._
import anorm.SqlParser._
import java.util.Date
import play.api.db.DB
import play.api.Logger
import play.api.Play.current

/**
 * A model for keeping a person
 *
 * @param id            Id of the person which is an auto incremented number
 * @param name          Name of the person
 * @param birthDate     Birth date of the person
 * @param isMale        Gender of the person, male if true, female if false
 * @param houseId       Id of house of the person
 * @param buttonNumber  Button number of the person on the remote controller
 */
case class Person(id: Long, name: String, birthDate: Date, isMale: Boolean, houseId: Long, buttonNumber: Int)

/**
 * Companion object of [[models.Person]] acting as data access layer
 */
object Person {
  /**
   * A result set parser for person records in database, maps records to a [[models.Person]] object
   */
  val personParser = {
    get[Long]("id") ~ get[String]("name") ~
    get[Date]("birth_date") ~ get[Boolean]("is_male") ~
    get[Long]("house_id") ~ get[Int]("button_number") map {
      case id ~ name ~ birthDate ~ isMale ~ houseId ~ buttonNumber =>
        Person(id, name, birthDate, isMale, houseId, buttonNumber)
    }
  }

  /**
   * Creates a person with given information and inserts it to the database
   *
   * @param name          Name of the person
   * @param birthDate     Birth date of the person
   * @param isMale        Gender of the person, male if true, female if false
   * @param houseId       Id of house of the person
   * @param buttonNumber  Button number of the person on the remote controller
   *
   * @return              An optional [[models.Person]] if successful, None if any error occurs
   */
  def create(name: String, birthDate: Date, isMale: Boolean, houseId: Long, buttonNumber: Int): Option[Person] = {
    try {
      DB.withConnection { implicit c =>
        val insertResult: Option[Long] = SQL(
          """insert into people (name, birth_date, is_male, house_id, button_number)
             values ({name}, {birthDate}, {isMale}, {houseId}, {buttonNumber})""")
          .on("name" -> name, "birthDate" -> birthDate, "isMale" -> isMale, "houseId" -> houseId, "buttonNumber" -> buttonNumber).executeInsert()
        if(!insertResult.isEmpty)
          Option(Person(insertResult.get, name, birthDate, isMale, houseId, buttonNumber))
        else {
          Logger.error(s"Person.create() - Person creation failed for $name, cannot insert!")
          None
        }
      }
    }
    catch {
      case e: Exception =>
        Logger.error(s"Person.create() - Person creation failed for $name, ${e.getMessage}")
        None
    }
  }

  /**
   * Reads a person by given id from the database
   *
   * @param id  Id of the person
   *
   * @return    An optional [[models.Person]] if successful, None if any error occurs
   */
  def read(id: Long): Option[Person] = {
    try {
      DB.withConnection { implicit c =>
        SQL(
          """select id, name, birth_date, is_male, house_id, button_number from people
             where id={id} limit 1""").on("id" -> id).as(personParser singleOpt)
      }
    }
    catch {
      case e: Exception =>
        Logger.error(s"Person.read() - Person reading failed for $id, ${e.getMessage}")
        None
    }
  }

  /**
   * Deletes a person with given id from the database
   *
   * @param id  Id of the person
   *
   * @return    true if successfully deleted, false if any error occurs
   */
  def delete(id: Long): Boolean = {
    try {
      DB.withConnection { implicit c =>
        SQL("""delete from people where id={id}""")
          .on("id" -> id).executeUpdate() > 0
      }
    }
    catch {
      case e: Exception =>
        Logger.error(s"Person.delete() - Person deleting failed for $id, ${e.getMessage}")
        false
    }
  }
}