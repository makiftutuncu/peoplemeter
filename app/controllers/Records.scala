package controllers

import play.api.mvc._
import models.{Record, House}
import models.Record.RecordAsJSON

object Records extends Controller {
  /**
   * Action to add a record
   *
   * POST requests will be sent from peoplemeter devices to this action as JSON
   *
   * An example body:
   *
   * {"deviceId":"7b970ec12c3d4b1e8eaf16c9c26c4539", "data":{"id":-1,"houseId":2,"buttonNumber":1,"channelId":3,"startTime":1399112900000,"endTime":1399112940000}}
   */
  def addRecord = Action { implicit request =>
    request.body.asJson map { json =>
      (json \ "deviceId").asOpt[String] map { deviceId =>
        House.read(deviceId) map { house =>
          try {
            val record: Record = RecordAsJSON.fromJSON(json \ "data")
            Record.create(record.houseId, record.buttonNumber, record.channelId, record.startTime, record.endTime) map { r =>
              Ok
            } getOrElse BadRequest("Error creating record!")
          } catch {
            case e: Exception => BadRequest("Invalid JSON!")
          }
        } getOrElse BadRequest("Invalid device id!")
      } getOrElse BadRequest("Invalid JSON!")
    } getOrElse BadRequest("Invalid request!")
  }
}
