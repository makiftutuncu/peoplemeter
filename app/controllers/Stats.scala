package controllers

import play.api.mvc._
import utilities.{TabItems, SidebarItems, Authenticated}

/**
 * Stats controller which controls everything about managing stats
 */
object Stats extends Controller {
  def renderChannelsTab = Authenticated { implicit request =>
    Ok(views.html.stats(request.context, sidebarItems = SidebarItems.activate("Stats"), tabItems = TabItems.activate("Channels")))
  }

  def renderAgeTab = Authenticated { implicit request =>
    Ok(views.html.stats(request.context, sidebarItems = SidebarItems.activate("Stats"), tabItems = TabItems.activate("Age")))
  }

  def renderGenderTab = Authenticated { implicit request =>
    Ok(views.html.stats(request.context, sidebarItems = SidebarItems.activate("Stats"), tabItems = TabItems.activate("Gender")))
  }
}