package utilities

import play.api.libs.json.JsValue

/**
 * A generic trait giving classes extending it capabilities of representing
 * themselves as JSON and generating objects of themselves from JSON
 *
 * @tparam T Type of the object that will have JSON capabilities
 */
trait JSONSerializable[T]
{
  /**
   * Represents given object as JSON
   *
   * @param obj Object to represent as JSON
   *
   * @return JSON representation of the object
   */
  def toJSON(obj: T): JsValue

  /**
   * Generates an object from given JSON
   *
   * @param json JSON from which the object will be generated
   *
   * @return Object generated from JSON
   */
  def fromJSON(json: JsValue): T
}