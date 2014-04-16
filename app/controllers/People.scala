package controllers

import play.api.mvc._
import utilities.{SidebarItems, Authenticated}

/**
 * People controller which controls everything about managing people
 */
object People extends Controller {
  def renderPage = Authenticated { implicit request =>
    Ok(views.html.people(request.context, sidebarItems = SidebarItems.activate("People")))
  }
}