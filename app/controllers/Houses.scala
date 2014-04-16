package controllers

import play.api.mvc._
import utilities.{SidebarItems, Authenticated}

/**
 * Houses controller which controls everything about managing houses
 */
object Houses extends Controller {
  def renderPage = Authenticated { implicit request =>
    Ok(views.html.houses(request.context, sidebarItems = SidebarItems.activate("Houses")))
  }
}