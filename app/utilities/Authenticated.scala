package utilities

import play.api.mvc._
import scala.concurrent.Future
import controllers.routes

/**
 * A custom Action that allows only authenticated requests
 */
object Authenticated extends ActionBuilder[AuthenticatedRequest] with Results {
  def invokeBlock[A](request: Request[A], block: (AuthenticatedRequest[A]) => Future[SimpleResult]) = {
    Authentication.getContextForRequest(request) map {
      context: Context =>
        block(new AuthenticatedRequest(context, request))
    } getOrElse {
      Future.successful(Redirect(routes.Application.index()).withNewSession.flashing("error.authentication" -> "Not authenticated!"))
    }
  }
}

/**
 * A helper model to wrap requests with a [[utilities.Context]] object
 */
class AuthenticatedRequest[A](val context: Context, request: Request[A]) extends WrappedRequest[A](request)