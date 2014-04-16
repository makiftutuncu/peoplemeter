package utilities

import play.api.mvc._
import play.api.Logger
import models.{Account, Session}

/**
 * A utility object for helping account authentication
 */
object Authentication {
  /**
   * Key of the cookie that will be created to identify account
   */
  val cookieName: String = "pmacc"

  /**
   * A general purpose authentication check for each request,
   * it checks cookies for a session id matching an account.
   *
   * @param   request Request of the action
   * @tparam  T       Type of the request
   *
   * @return          A [[utilities.Context]] for the authenticated account optionally if authentication is successful, None otherwise
   */
  def getContextForRequest[T](request: Request[T]): Option[Context] = {
    try {
      request.session.get(cookieName) map { id: String =>
        Session.read(id) map { session: Session =>
          Account.read(session.accountId) map { account: Account =>
            Option(Context(session.id, account.id, account.email))
          } getOrElse {
            Logger.error(s"Authentication.getContextForRequest() - Authentication failed, Account couldn\'t be read with id ${session.accountId} provided by session")
            None
          }
        } getOrElse {
          Logger.error(s"Authentication.getContextForRequest() - Authentication failed, Session couldn\'t be read with id $id provided by cookie, this might be an attack")
          None
        }
      } getOrElse {
        None
      }
    } catch {
      case e: Exception =>
        Logger.error(s"Authentication.getContextForRequest() - Authentication failed, ${e.getMessage}!")
        None
    }
  }
}
