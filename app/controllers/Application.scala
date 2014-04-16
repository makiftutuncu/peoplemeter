package controllers

import play.api.mvc._
import utilities.{SidebarItems, Context, Authentication}
import play.api.i18n.Messages

object Application extends Controller {
  def index = Action { implicit request =>
    Authentication.getContextForRequest(request) map { context: Context =>
      Ok(views.html.index(context = context,
                          errorMessage = if(request.flash.get("error.authentication").isDefined)
                                            Messages("error.authentication")
                                         else "",
                          sidebarItems = SidebarItems.activate("Index")))
    } getOrElse {
      Redirect(routes.Login.renderPage())
    }
  }
}
