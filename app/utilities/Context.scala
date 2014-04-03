package utilities

/**
 * A helper model containing user and session information
 *
 * @param sessionId Id of the session
 * @param accountId Id of the account to which session belongs
 * @param email     E-mail address of the user
 */
case class Context(sessionId: String, accountId: Long, email: String)
