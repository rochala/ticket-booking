package utils

class DatabaseConnector {
  val profile = slick.driver.H2Driver

  import profile.api._

  val db = Database.forConfig("database")

  db.createSession()
}
