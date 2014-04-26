package utilities

import play.api.mvc._
import controllers.routes

/**
 * A helper model to use in templates for sidebar items
 *
 * @param link      Link of the item
 * @param text      Text of the item
 * @param isActive  Indicates if this item is selected and active
 */
case class TabItem(link: Call, text: String, isActive: Boolean)

object TabItems {
  val items: List[TabItem] = List(
    TabItem(routes.Stats.renderChannelsTab(), "Channels", isActive = false),
    TabItem(routes.Stats.renderAgeTab(), "Age", isActive = false),
    TabItem(routes.Stats.renderGenderTab(), "Gender", isActive = false)
  )

  def activate(text: String): List[TabItem] = {
    items map { item: TabItem =>
      if(item.text == text) item.copy(isActive = true) else item.copy(isActive = false)
    }
  }
}
