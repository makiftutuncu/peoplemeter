package utilities

/**
 * A utility object for generating useful things like UUID and hash values
 */
object Generators {
  /**
   * Generates a universally unique identifier consisting of 32 hexadecimal characters
   *
   * @return  Generated UUID value
   */
  def generateUUID: String = {
    java.util.UUID.randomUUID().toString.replace("-", "")
  }

  /**
   * Generates the SHA-512 hashed value of given String
   *
   * @param s A value whose hash will be generated
   *
   * @return  Generated hash value consisting of 128 hexadecimal characters
   */
  def generateSHA512(s: String): String = {
    val messageDigest = java.security.MessageDigest.getInstance("SHA-512")
    val bytes = messageDigest.digest(s.getBytes)
    val stringBuilder: StringBuilder = new StringBuilder()
    for(byte <- bytes) stringBuilder.append(Integer.toString((byte & 0xff) + 0x100, 16).substring(1))
    stringBuilder.toString()
  }
}
