package controllers

import play.api.mvc._
import utilities.{SidebarItems, Authenticated}

/**
 * Channels controller which controls everything about managing channels
 */
object Channels extends Controller {
  def renderPage = Authenticated { implicit request =>
    Ok(views.html.channels(request.context, sidebarItems = SidebarItems.activate("Channels")))
  }
}