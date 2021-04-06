package utils

class DatabaseConnector {
  val profile = slick.jdbc.PostgresProfile

  import profile.api._

  val db: profile.backend.DatabaseDef = Database.forConfig("database")

  db.createSession()
}
