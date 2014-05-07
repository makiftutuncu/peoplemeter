package controllers

import play.api.mvc._
import models.{Record, House}
import models.Record.RecordAsJSON
import java.util.Date

object Records extends Controller {
  private def create(deviceId: String, record: Record): SimpleResult = {
    House.read(deviceId) map { house =>
      if(house.id == record.houseId) {
        Record.create(record.houseId, record.buttonNumber, record.channelId, record.startTime, record.endTime) map { r =>
          Ok
        } getOrElse BadRequest("Error creating record!")
      } else        BadRequest("Invalid device id!")
    } getOrElse     BadRequest("Invalid device id!")
  }

  private def updateEndTime(deviceId: String, record: Record, newEndTime: Date): SimpleResult = {
    House.read(deviceId) map { house =>
      if(house.id == record.houseId) {
        if(Record.update(record.id, record.houseId, record.buttonNumber, record.channelId, record.startTime, newEndTime)) {
          Ok
        } else  BadRequest("Error updating record!")
      } else    BadRequest("Invalid device id!")
    } getOrElse BadRequest("Invalid device id!")
  }

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
            val newRecord: Record = RecordAsJSON.fromJSON(json \ "data")

            Record.readLast map { lastRecord =>
              if(newRecord.houseId == lastRecord.houseId) { // Same house
                if(lastRecord.buttonNumber == newRecord.buttonNumber && lastRecord.channelId == newRecord.channelId) {
                  // Still watching same thing and no other data came from other houses
                  // Update the end time of lastRecord to newRecord.endTime
                  updateEndTime(deviceId, lastRecord, newRecord.endTime)
                } else {
                  // There is a previous record from this house but it is not the same channel or person
                  // Update the end time of lastRecord to newRecord.endTime
                  // This is a new record, create
                  updateEndTime(deviceId, lastRecord, newRecord.endTime)
                  create(deviceId, newRecord)
                }
              } else { // Different house
                Record.readLastByHouseId(newRecord.houseId) map { lastSameHouseRecord =>
                  if(newRecord.buttonNumber == lastSameHouseRecord.buttonNumber) {
                    if(newRecord.channelId == lastSameHouseRecord.channelId) {
                      // Still watching same thing but data came from different house before this
                      // Update the end time of lastSameRecord to newRecord.endTime
                      updateEndTime(deviceId, lastSameHouseRecord, newRecord.endTime)
                    } else {
                      // Not watching the same thing but data came from different house before this
                      // Update the end time of lastSameHouseRecord to newRecord.endTime
                      // This is a new record, create
                      updateEndTime(deviceId, lastSameHouseRecord, newRecord.endTime)
                      create(deviceId, newRecord)
                    }
                  } else {
                    // Another person is watching something but data came from different house before this
                    // Update the end time of lastSameHouseRecord to newRecord.endTime
                    // This is a new record, create
                    updateEndTime(deviceId, lastSameHouseRecord, newRecord.endTime)
                    create(deviceId, newRecord)
                  }
                } getOrElse {
                  // This is a completely new record, just create
                  create(deviceId, newRecord)
                }
              }
            } getOrElse {
              // There is no records at all
              // This is a completely new record, just create
              create(deviceId, newRecord)
            }
          } catch {
            case e: Exception => BadRequest("Invalid JSON!")
          }
        } getOrElse BadRequest("Invalid device id!")
      } getOrElse BadRequest("Invalid JSON!")
    } getOrElse BadRequest("Invalid request!")
  }
}
