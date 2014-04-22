package controllers

import play.api.mvc._
import utilities.{SidebarItems, Authenticated}
import play.api.data.Form
import play.api.data.Forms._
import controllers.HouseFormData
import models.{Person, House}
import java.util.Date

/**
 * People controller which controls everything about managing people
 */
object People extends Controller {
  /**
   * A form matcher for the person form, maps the form data to a [[PersonFormData]] object
   */
  val personForm: Form[PersonFormData] = Form(
    mapping(
      "name" -> nonEmptyText,
      "birthDate" -> date,
      "isMale" -> boolean,
      "houseId" -> longNumber,
      "buttonNumber" -> number
    )(PersonFormData.apply)(PersonFormData.unapply)
  )

  def renderPage = Authenticated { implicit request =>
    val personList: List[Person] = Person.read
    Ok(views.html.people(people = personList, context = request.context, sidebarItems = SidebarItems.activate("People")))
  }

  def renderEditPersonPage(id: Long) = Authenticated { implicit request =>
    Ok(views.html.personDetails(person = Person.read(id), context = request.context, sidebarItems = SidebarItems.activate("People")))
  }

  def renderAddPersonPage = Authenticated { implicit request =>
    Ok(views.html.personDetails(isAddingPerson = true, context = request.context, sidebarItems = SidebarItems.activate("People")))
  }

  def addPerson = Authenticated { implicit request =>
    personForm.bindFromRequest().fold(
      errors => {
        // Log
        Redirect(routes.People.renderPage())
      },
      personFormData => {
        Person.create(personFormData.name,
          personFormData.birthDate,
          personFormData.isMale,
          personFormData.houseId,
          personFormData.buttonNumber) map {
          person: Person => Redirect(routes.People.renderPage())
        } getOrElse {
          // Log
          Redirect(routes.People.renderPage())
        }
      }
    )
  }

  def editPerson(id: Long) = Authenticated { implicit request =>
    personForm.bindFromRequest().fold(
      errors => {
        // Log
        Redirect(routes.People.renderPage())
      },
      personFormData => {
        val result: Boolean = Person.update(id,
          personFormData.name,
          personFormData.birthDate,
          personFormData.isMale,
          personFormData.houseId,
          personFormData.buttonNumber)
        if(!result) {
          // Log
        }
        Redirect(routes.People.renderPage())
      }
    )
  }

  def deletePerson(id: Long) = Authenticated { implicit request =>
    Person.delete(id)
    Redirect(routes.People.renderPage())
  }
}

/**
 * A model of person form
 *
 * @param name          Name of the person
 * @param birthDate     Birth date of the person
 * @param isMale        Gender of the person, male if true, female if false
 * @param houseId       Id of house of the person
 * @param buttonNumber  Button number of the person on the remote controller
 */
case class PersonFormData(name: String, birthDate: Date, isMale: Boolean, houseId: Long, buttonNumber: Int)