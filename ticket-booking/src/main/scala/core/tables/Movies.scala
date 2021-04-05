package core.tables

import core.Movie
import utils.DatabaseConnector
import slick.driver.PostgresDriver.api._

class Movies(tag: Tag) extends Table[Movie](tag, "movies") {
  def id       = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def imdbID   = column[String]("imdbid")
  def title    = column[String]("title")
  def duration = column[Long]("duration")

  override def * = (id.?, imdbID, title, duration).<>(Movie.tupled, Movie.unapply)
}
