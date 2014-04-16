package utilities

import play.api.mvc._
import controllers.routes

/**
 * A helper model to use in templates for sidebar items
 *
 * @param link      Link of the item
 * @param icon      Icon class of the item
 * @param text      Text of the item
 * @param isActive  Indicates if this item is selected and active
 */
case class SidebarItem(link: Call, icon: String, text: String, isActive: Boolean)

object SidebarItems {
  val items: List[SidebarItem] = List(
    SidebarItem(routes.Application.index(), "glyphicon-home", "Home", isActive = false),
    SidebarItem(routes.Houses.renderPage(), "glyphicon-th-large", "Houses", isActive = false),
    SidebarItem(routes.People.renderPage(), "glyphicon-user", "People", isActive = false),
    SidebarItem(routes.Channels.renderPage(), "glyphicon-list", "Channels", isActive = false),
    SidebarItem(routes.Stats.renderPage(), "glyphicon-stats", "Stats", isActive = false)
  )

  def activate(text: String): List[SidebarItem] = {
    items map { item: SidebarItem =>
      if(item.text == text) item.copy(isActive = true) else item.copy(isActive = false)
    }
  }
}
