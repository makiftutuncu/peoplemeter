package controllers

import play.api.mvc._
import play.api.Logger
import models.Session
import utilities.Authenticated

/**
 * Logout controller which controls everything about logging an account out of the system
 */
object Logout extends Controller {
  def logout = Authenticated { implicit request =>
    if(!Session.delete(request.context.sessionId)) {
      Logger.error(s"Logout.logout() - Deleting session ${request.context.sessionId} failed!")
    }
    Redirect(routes.Application.index()).withNewSession
  }
}