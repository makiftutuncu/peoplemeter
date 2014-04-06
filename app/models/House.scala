package models

import anorm._
import anorm.SqlParser._
import play.api.db.DB
import play.api.Logger
import play.api.Play.current

/**
 * A model for keeping a house
 *
 * @param id            Id of the house which is an auto incremented number
 * @param familyName    Name of the family living in the house
 * @param district      District name in which the house is located
 * @param street        Street name in which the house is located
 * @param buildingName  Building name of the house
 * @param doorNumber    Door number of the house
 * @param postalCode    Postal code of the address at which the house is located
 * @param town          Name of the town in which the house is located
 * @param city          Name of the city in which the house is located
 */
case class House(id: Long, familyName: String,
                 district: String, street: String,
                 buildingName: String, doorNumber: String,
                 postalCode: String, town: String, city: String)

/**
 * Companion object of [[models.House]] acting as data access layer
 */
object House {
  /**
   * A result set parser for house records in database, maps records to a [[models.House]] object
   */
  val houseParser = {
    get[Long]("id") ~ get[String]("family_name") ~
    get[String]("district") ~ get[String]("street") ~
    get[String]("building_name") ~ get[String]("door_number") ~
    get[String]("postal_code") ~ get[String]("town") ~
    get[String]("city") map {
      case id ~ familyName ~ district ~ street ~ buildingName ~ doorNumber ~ postalCode ~ town ~ city =>
        House(id, familyName, district, street, buildingName, doorNumber, postalCode, town, city)
    }
  }

  /**
   * Creates a house with given information and inserts it to the database
   *
   * @param familyName    Name of the family living in the house
   * @param district      District name in which the house is located
   * @param street        Street name in which the house is located
   * @param buildingName  Building name of the house
   * @param doorNumber    Door number of the house
   * @param postalCode    Postal code of the address at which the house is located
   * @param town          Name of the town in which the house is located
   * @param city          Name of the city in which the house is located
   *
   * @return              An optional [[models.House]] if successful, None if any error occurs
   */
  def create(familyName: String, district: String,
             street: String, buildingName: String,
             doorNumber: String, postalCode: String,
             town: String, city: String): Option[House] = {
    try {
      DB.withConnection { implicit c =>
        val insertResult: Option[Long] = SQL(
          """insert into houses (family_name, district, street, building_name, door_number, postal_code, town, city)
             values ({familyName}, {district}, {street}, {buildingName}, {doorNumber}, {postalCode}, {town}, {city})""")
          .on("familyName" -> familyName, "district" -> district,
              "street" -> street, "buildingName" -> buildingName,
              "doorNumber" -> doorNumber, "postalCode" -> postalCode,
              "town" -> town, "city" -> city)
          .executeInsert()
        if(!insertResult.isEmpty)
          Option(House(insertResult.get, familyName, district, street, buildingName, doorNumber, postalCode, town, city))
        else {
          Logger.error(s"House.create() - House creation failed for $familyName, cannot insert!")
          None
        }
      }
    }
    catch {
      case e: Exception =>
        Logger.error(s"House.create() - House creation failed for $familyName, ${e.getMessage}")
        None
    }
  }

  /**
   * Reads a house by given id from the database
   *
   * @param id  Id of the house
   *
   * @return    An optional [[models.House]] if successful, None if any error occurs
   */
  def read(id: Long): Option[House] = {
    try {
      DB.withConnection { implicit c =>
        SQL(
          """select id, family_name, district, street, building_name, door_number, postal_code, town, city from houses
             where id={id} limit 1""").on("id" -> id).as(houseParser singleOpt)
      }
    }
    catch {
      case e: Exception =>
        Logger.error(s"House.read() - House reading failed for $id, ${e.getMessage}")
        None
    }
  }

  /**
   * Deletes a house with given id from the database
   *
   * @param id  Id of the house
   *
   * @return    true if successfully deleted, false if any error occurs
   */
  def delete(id: Long): Boolean = {
    try {
      DB.withConnection { implicit c =>
        SQL("""delete from houses where id={id}""")
          .on("id" -> id).executeUpdate() > 0
      }
    }
    catch {
      case e: Exception =>
        Logger.error(s"House.delete() - House deleting failed for $id, ${e.getMessage}")
        false
    }
  }
}
