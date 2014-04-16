package utilities

import play.api.mvc._
import scala.concurrent.Future
import controllers.routes

/**
  * A custom Action that allows only authenticated requests
  */
object NotAuthenticated extends ActionBuilder[Request] with Results {
  def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[SimpleResult]) = {
    val maybeContext: Option[Context] = Authentication.getContextForRequest(request)
    maybeContext map {
      context: Context =>
        Future.successful(Redirect(routes.Application.index()))
    } getOrElse {
      block(request)
    }
  }
}