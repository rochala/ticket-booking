package utils

class DatabaseConnector {
  val profile = slick.driver.PostgresDriver

  import profile.api._

  val db = Database.forConfig("database")

  db.createSession()
}
