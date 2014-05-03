package controllers

import play.api.mvc._
import models.{Record, House}
import models.Record.RecordAsJSON

object Records extends Controller {
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
