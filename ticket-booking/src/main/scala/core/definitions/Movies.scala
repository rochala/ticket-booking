package core.definitions

import core.Movie
import slick.driver.H2Driver.api._

class Movies(tag: Tag) extends Table[Movie](tag, "MOVIES") {
  def id         = column[Long]("MOVIE_ID", O.PrimaryKey, O.AutoInc)
  def imbdID     = column[String]("IMBD_ID")
  def title      = column[String]("TITLE")
  def duration   = column[Long]("DURATION")

  override def * = (id.?, imbdID, title, duration).<>(Movie.tupled, Movie.unapply)
}
