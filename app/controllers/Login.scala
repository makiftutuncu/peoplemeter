package controllers

import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import play.api.Logger
import models.{Account, Session}
import utilities.{NotAuthenticated, Authentication, Generators}

/**
 * Login controller which controls everything about logging an account into the system
 */
object Login extends Controller {
  /**
   * A form matcher for the login form, maps the form data to a [[LoginFormAccount]] object
   */
  val loginForm: Form[LoginFormAccount] = Form(
    mapping(
      "email" -> email,
      "password" -> text(6, 32)
    )(LoginFormAccount.apply)(LoginFormAccount.unapply)
  )

  def renderPage = NotAuthenticated {
    Ok(views.html.login(loginForm))
  }

  def submit = NotAuthenticated { implicit request =>
    loginForm.bindFromRequest.fold(
      errors =>
        giveFormError("Login.submit() - Email or password in login form was invalid! " + errors.errorsAsJson, errors, "Email and/or password is invalid!"),
      loginFormAccount => {
        Account.read(loginFormAccount.email) map { account: Account =>
            if(account.password == Generators.generateSHA512(loginFormAccount.password)) {
              Session.create(account.id) map { session: Session =>
                Redirect(routes.Application.index()).withSession(Authentication.cookieName -> session.id)
              } getOrElse {
                giveError(s"Login.submit() - Session creation failed for ${loginFormAccount.email}")
              }
            } else {
              giveFormError(s"Login.submit() - Email and password doesn't match for $loginFormAccount!", loginForm, "Email and password doesn\'t match!")
            }
        } getOrElse {
          giveFormError(s"Login.submit() - No account is found for $loginFormAccount!", loginForm, "Email and password doesn\'t match!")
        }
      }
    )
  }

  /**
   * Generates a result for a form error, logs it and returns to login page with a bad request
   *
   * @param logMsg          Message to write to log
   * @param formWithErrors  Form object containing errors
   * @param formErrorMsg    Message code for identifying error message in login page
   *
   * @return  A SimpleResult with a bad request for login page
   */
  private def giveFormError[T](logMsg: String, formWithErrors: Form[LoginFormAccount], formErrorMsg: String)(implicit request: Request[T]): SimpleResult = {
    Logger.error(logMsg)
    BadRequest(views.html.login(formWithErrors, formErrorMsg))
  }

  /**
   * Generates a result for an error, logs it and returns to index page with redirect
   *
   * @param logMsg  Message to write to log
   *
   * @return  A SimpleResult with a redirect for index page
   */
  private def giveError[T](logMsg: String)(implicit request: Request[T]): SimpleResult = {
    Logger.error(logMsg)
    Redirect(routes.Application.index())
  }
}

/**
 * A model of the login form
 *
 * @param email     Entered email
 * @param password  Entered password
 */
case class LoginFormAccount(email: String, password: String)