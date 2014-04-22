package controllers

import play.api.mvc._
import utilities.{SidebarItems, Authenticated}
import models.House
import play.api.data.Form
import play.api.data.Forms._

/**
 * Houses controller which controls everything about managing houses
 */
object Houses extends Controller {
  /**
   * A form matcher for the house form, maps the form data to a [[HouseFormData]] object
   */
  val houseForm: Form[HouseFormData] = Form(
    mapping(
      "familyName" -> nonEmptyText,
      "district" -> nonEmptyText,
      "street" -> nonEmptyText,
      "buildingName" -> nonEmptyText,
      "doorNumber" -> nonEmptyText,
      "postalCode" -> nonEmptyText,
      "town" -> nonEmptyText,
      "city" -> nonEmptyText
    )(HouseFormData.apply)(HouseFormData.unapply)
  )

  def renderPage = Authenticated { implicit request =>
    val houseList: List[House] = House.read
    Ok(views.html.houses(houses = houseList, context = request.context, sidebarItems = SidebarItems.activate("Houses")))
  }

  def renderEditHousePage(id: Long) = Authenticated { implicit request =>
    Ok(views.html.houseDetails(house = House.read(id), context = request.context, sidebarItems = SidebarItems.activate("Houses")))
  }

  def renderAddHousePage = Authenticated { implicit request =>
    Ok(views.html.houseDetails(isAddingHouse = true, context = request.context, sidebarItems = SidebarItems.activate("Houses")))
  }

  def addHouse = Authenticated { implicit request =>
    houseForm.bindFromRequest().fold(
      errors => {
        // Log
        Redirect(routes.Houses.renderPage())
      },
      houseFormData => {
        House.create(houseFormData.familyName,
          houseFormData.district,
          houseFormData.street,
          houseFormData.buildingName,
          houseFormData.doorNumber,
          houseFormData.postalCode,
          houseFormData.town,
          houseFormData.city) map {
          house: House => Redirect(routes.Houses.renderPage())
        } getOrElse {
          // Log
          Redirect(routes.Houses.renderPage())
        }
      }
    )
  }

  def editHouse(id: Long) = Authenticated { implicit request =>
    houseForm.bindFromRequest().fold(
      errors => {
        // Log
        Redirect(routes.Houses.renderPage())
      },
      houseFormData => {
        val result: Boolean = House.update(id,
          houseFormData.familyName,
          houseFormData.district,
          houseFormData.street,
          houseFormData.buildingName,
          houseFormData.doorNumber,
          houseFormData.postalCode,
          houseFormData.town,
          houseFormData.city)
        if(!result) {
          // Log
        }
        Redirect(routes.Houses.renderPage())
      }
    )
  }

  def deleteHouse(id: Long) = Authenticated { implicit request =>
    House.delete(id)
    Redirect(routes.Houses.renderPage())
  }
}

/**
 * A model of house form
 *
 * @param familyName    Name of the family living in the house
 * @param district      District name in which the house is located
 * @param street        Street name in which the house is located
 * @param buildingName  Building name of the house
 * @param doorNumber    Door number of the house
 * @param postalCode    Postal code of the address at which the house is located
 * @param town          Name of the town in which the house is located
 * @param city          Name of the city in which the house is located
 */
case class HouseFormData(familyName: String, district: String, street: String, buildingName: String, doorNumber: String, postalCode: String, town: String, city: String)