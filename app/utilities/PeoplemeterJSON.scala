package utilities

import models.PeoplemeterModel
import play.api.libs.json.JsValue

/**
 * A helper object that can represent [[models.PeoplemeterModel]] objects as JSON and
 * generate objects of type [[models.PeoplemeterModel]] from JSON
 */
object PeoplemeterJSON
{
  /**
   * Represents given object as JSON
   *
   * @param obj Object to represent as JSON
   *
   * @tparam T Type of the object
   *
   * @return JSON representation of the object
   */
  def toJSON[T <: PeoplemeterModel : JSONSerializable](obj: T): JsValue =
  {
    implicitly[JSONSerializable[T]].toJSON(obj)
  }

  /**
   * Generates an object from given JSON
   *
   * @param json JSON from which the object will be generated
   *
   * @tparam T Type of the object
   *
   * @return Object generated from JSON
   */
  def fromJSON[T <: PeoplemeterModel : JSONSerializable](json: JsValue): T =
  {
    implicitly[JSONSerializable[T]].fromJSON(json)
  }
}
