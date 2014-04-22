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

  def renderEditHousePage(id: Long) = Authenticated { implicit request =>
    Ok(views.html.houseDetails(house = House.read(id), context = request.context, sidebarItems = SidebarItems.activate("Houses")))
  }

  def renderAddHousePage = Authenticated { implicit request =>
    Ok(views.html.houseDetails(isAddingHouse = true, context = request.context, sidebarItems = SidebarItems.activate("Houses")))
  }

  def addHouse = TODO

  def editHouse(id: Long) = TODO

  def deleteHouse(id: Long) = TODO
}