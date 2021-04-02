package core.movies

import core.Movie
import utils.DatabaseConnector

private[core] trait MovieDataTable {

  protected val databaseConnector: DatabaseConnector
  import databaseConnector.profile.api._

  class Movies(tag: Tag) extends Table[Movie](tag, "MOVIES") {
    def id       = column[Long]("MOVIE_ID", O.PrimaryKey, O.AutoInc)
    def imbdID   = column[String]("IMBD_ID")
    def title    = column[String]("TITLE")
    def duration = column[Long]("DURATION")

    override def * = (id.?, imbdID, title, duration).<>(Movie.tupled, Movie.unapply)
  }

  protected val movies = TableQuery[Movies]
}
