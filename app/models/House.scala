package models

import anorm._
import anorm.SqlParser._
import play.api.db.DB
import play.api.Logger
import play.api.Play.current
import utilities.JSONSerializable
import play.api.libs.json.{Json, JsValue}

/**
 * A model for keeping a house
 *
 * @param id            Id of the house which is an auto incremented number
 * @param deviceId      Id of the peoplemeter device installed in the house
 * @param familyName    Name of the family living in the house
 * @param district      District name in which the house is located
 * @param street        Street name in which the house is located
 * @param buildingName  Building name of the house
 * @param doorNumber    Door number of the house
 * @param postalCode    Postal code of the address at which the house is located
 * @param town          Name of the town in which the house is located
 * @param city          Name of the city in which the house is located
 */
case class House(id: Long, deviceId: String, familyName: String,
                 district: String, street: String, buildingName: String, doorNumber: String,
                 postalCode: String, town: String, city: String) extends PeoplemeterModel

/**
 * Companion object of [[models.House]] acting as data access layer
 */
object House {
  /**
   * A result set parser for house records in database, maps records to a [[models.House]] object
   */
  val houseParser = {
    get[Long]("id") ~ get[String]("device_id") ~ get[String]("family_name") ~
    get[String]("district") ~ get[String]("street") ~ get[String]("building_name") ~ get[String]("door_number") ~
    get[String]("postal_code") ~ get[String]("town") ~ get[String]("city") map {
      case id ~ deviceId ~ familyName ~ district ~ street ~ buildingName ~ doorNumber ~ postalCode ~ town ~ city =>
        House(id, deviceId, familyName, district, street, buildingName, doorNumber, postalCode, town, city)
    }
  }

  /**
   * Creates a house with given information and inserts it to the database
   *
   * @param deviceId      Id of the peoplemeter device installed in the house
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
  def create(deviceId: String, familyName: String, district: String,
             street: String, buildingName: String, doorNumber: String, postalCode: String,
             town: String, city: String): Option[House] = {
    try {
      DB.withConnection { implicit c =>
        val insertResult: Option[Long] = SQL(
          """insert into houses (device_id, family_name, district, street, building_name, door_number, postal_code, town, city)
             values ({deviceId}, {familyName}, {district}, {street}, {buildingName}, {doorNumber}, {postalCode}, {town}, {city})""")
          .on("deviceId" -> deviceId, "familyName" -> familyName, "district" -> district,
              "street" -> street, "buildingName" -> buildingName,
              "doorNumber" -> doorNumber, "postalCode" -> postalCode,
              "town" -> town, "city" -> city)
          .executeInsert()
        if(!insertResult.isEmpty)
          Option(House(insertResult.get, deviceId, familyName, district, street, buildingName, doorNumber, postalCode, town, city))
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
   * Updates a house with given information on the database
   *
   * @param id            Id of the house which is an auto incremented number
   * @param deviceId      Id of the peoplemeter device installed in the house
   * @param familyName    Name of the family living in the house
   * @param district      District name in which the house is located
   * @param street        Street name in which the house is located
   * @param buildingName  Building name of the house
   * @param doorNumber    Door number of the house
   * @param postalCode    Postal code of the address at which the house is located
   * @param town          Name of the town in which the house is located
   * @param city          Name of the city in which the house is located
   *
   * @return              true if successfully deleted, false if any error occurs
   */
  def update(id: Long, deviceId: String, familyName: String, district: String,
             street: String, buildingName: String, doorNumber: String, postalCode: String,
             town: String, city: String): Boolean = {
    try {
      DB.withConnection { implicit c =>
        val affectedRows = SQL(
          """update houses
             set device_id={deviceId}, family_name={familyName}, district={district},
                 street={street}, building_name={buildingName},
                 door_number={doorNumber}, postal_code={postalCode},
                 town={town}, city={city}
             where id={id}""")
          .on("id" -> id, "deviceId" -> deviceId, "familyName" -> familyName, "district" -> district,
            "street" -> street, "buildingName" -> buildingName,
            "doorNumber" -> doorNumber, "postalCode" -> postalCode,
            "town" -> town, "city" -> city)
          .executeUpdate()
        val result: Boolean = affectedRows > 0
        if(!result) {
          Logger.error(s"House.update() - House update failed for $id, cannot update!")
        }
        result
      }
    }
    catch {
      case e: Exception =>
        Logger.error(s"House.update() - House update failed for id $id, ${e.getMessage}")
        false
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
          """select id, device_id, family_name, district, street, building_name, door_number, postal_code, town, city from houses
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
   * Reads a house by given device id from the database
   *
   * @param deviceId  Id of the peoplemeter device installed in the house
   *
   * @return          An optional [[models.House]] if successful, None if any error occurs
   */
  def read(deviceId: String): Option[House] = {
    try {
      DB.withConnection { implicit c =>
        SQL(
          """select id, device_id, family_name, district, street, building_name, door_number, postal_code, town, city from houses
             where device_id={deviceId} limit 1""").on("deviceId" -> deviceId).as(houseParser singleOpt)
      }
    }
    catch {
      case e: Exception =>
        Logger.error(s"House.read() - House reading failed for device id $deviceId, ${e.getMessage}")
        None
    }
  }

  /**
   * Reads list of house from the database
   *
   * @return    A list of [[models.House]] if successful, Nil if not found or any error occurs
   */
  def read: List[House] = {
    try {
      DB.withConnection { implicit c =>
        SQL(
          """select id, device_id, family_name, district, street, building_name, door_number, postal_code, town, city from houses""").as(houseParser *)
      }
    }
    catch {
      case e: Exception =>
        Logger.error(s"House.read() - House reading failed, ${e.getMessage}")
        Nil
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

  implicit object HouseAsJSON extends JSONSerializable[House]
  {
    def toJSON(house: House): JsValue = Json.obj(
      "id" -> house.id,
      "deviceId" -> house.deviceId,
      "familyName" -> house.familyName,
      "district" -> house.district,
      "street" -> house.street,
      "buildingName" -> house.buildingName,
      "doorNumber" -> house.doorNumber,
      "postalCode" -> house.postalCode,
      "town" -> house.town,
      "city" -> house.city
    )

    def fromJSON(json: JsValue): House =
    {
      ((json \ "id").asOpt[Long],
       (json \ "deviceId").asOpt[String],
       (json \ "familyName").asOpt[String],
       (json \ "district").asOpt[String],
       (json \ "street").asOpt[String],
       (json \ "buildingName").asOpt[String],
       (json \ "doorNumber").asOpt[String],
       (json \ "postalCode").asOpt[String],
       (json \ "town").asOpt[String],
       (json \ "city").asOpt[String]) match
      {
        case (Some(id: Long),
              Some(deviceId: String),
              Some(familyName: String),
              Some(district: String),
              Some(street: String),
              Some(buildingName: String),
              Some(doorNumber: String),
              Some(postalCode: String),
              Some(town: String),
              Some(city: String)) => House(id, deviceId, familyName, district, street, buildingName, doorNumber, postalCode, town, city)
        case _ => throw new IllegalArgumentException("Invalid House JSON!")
      }
    }
  }
}
