package controllers

import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import play.api.Logger
import models.{Account, Session}
import utilities.Generators

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
      "password" -> text(6)
    )(LoginFormAccount.apply)(LoginFormAccount.unapply)
  )

  /**
   * Action that validates login data and performs login operation,
   * takes user to index page if not authorized
   */
  def submit = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      errors =>
        giveFormError("Login.submit() - Username or password in login form was invalid! " + errors.errorsAsJson, "username_password_invalid"),
      loginFormUser => {
        Account.read(loginFormUser.email) map { account: Account =>
            if(account.password == Generators.generateSHA512(loginFormUser.password)) {
              Session.create(account.id) map { session: Session =>
                Ok.withSession("puser" -> session.id)
              } getOrElse {
                giveError(s"Login.submit() - Session creation failed for ${loginFormUser.email}")
              }
            } else {
              giveFormError(s"Login.submit() - Email and password doesn't match for $loginFormUser!", "username_password_mismatch")
            }
        } getOrElse {
          giveFormError(s"Login.submit() - Email and password doesn't match for $loginFormUser!", "username_password_mismatch")
        }
      }
    )
  }

  /**
   * Generates a result for a form error, logs it and returns to login page with a bad request
   *
   * @param logMsg        Message to write to log
   * @param formErrorMsg  Message code for identifying error message in login page
   *
   * @return  A SimpleResult with a bad request for login page
   */
  private def giveFormError(logMsg: String, formErrorMsg: String): SimpleResult = {
    Logger.error(logMsg)
    BadRequest
  }

  /**
   * Generates a result for an error, logs it and returns to index page with redirect
   *
   * @param logMsg  Message to write to log
   *
   * @return  A SimpleResult with a redirect for index page
   */
  private def giveError(logMsg: String): SimpleResult = {
    Logger.error(logMsg)
    Redirect(routes.Application.main())
  }
}

/**
 * A model of the login form
 *
 * @param email     Entered email
 * @param password  Entered password
 */
case class LoginFormAccount(email: String, password: String)