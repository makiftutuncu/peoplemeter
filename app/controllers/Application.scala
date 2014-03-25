package controllers

import play.api.mvc._

object Application extends Controller {
  def main = Action {
    Ok(views.html.index())
  }
}
