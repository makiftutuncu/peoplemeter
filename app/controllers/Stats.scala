package controllers

import play.api.mvc._
import utilities.{SidebarItems, Authenticated}

/**
 * Stats controller which controls everything about managing stats
 */
object Stats extends Controller {
  def renderPage = Authenticated { implicit request =>
    Ok(views.html.stats(request.context, sidebarItems = SidebarItems.activate("Stats")))
  }
}