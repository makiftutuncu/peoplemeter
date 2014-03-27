package utilities

/**
 * A helper model containing user and session information
 *
 * @param sessionId Id of the session
 * @param userId    Id of the user to which session belongs
 * @param username  Username of the user
 * @param email     E-mail address of the user
 */
case class Context(sessionId: String, userId: Long, username: String, email: String)
