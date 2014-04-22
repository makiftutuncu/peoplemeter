package controllers

import play.api.mvc._
import utilities.{SidebarItems, Authenticated}
import models.House

/**
 * Houses controller which controls everything about managing houses
 */
object Houses extends Controller {
  def renderPage = Authenticated { implicit request =>
    val houseList: List[House] = House.read
    Ok(views.html.houses(houses = houseList, context = request.context, sidebarItems = SidebarItems.activate("Houses")))
  }

  def editHouse(id: Long) = Authenticated { implicit request =>
    House.read(id) map { house: House =>
      Ok(views.html.houseDetails(house = Option(house), context = request.context, sidebarItems = SidebarItems.activate("Houses")))
    } getOrElse BadRequest
  }

  def addHouse = TODO
}