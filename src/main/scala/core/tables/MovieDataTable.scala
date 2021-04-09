package core.tables

import java.sql.Time

import core.Movie
import slick.sql.SqlProfile.ColumnOption.SqlType
import utils.DatabaseConnector

private[core] trait MovieDataTable {

  protected val databaseConnector: DatabaseConnector

  import databaseConnector.profile.api._

  protected val movies = TableQuery[Movies]

  class Movies(tag: Tag) extends Table[Movie](tag, "movies") {
    override def * = (id.?, imdbID, title, duration).<>(Movie.tupled, Movie.unapply)

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def imdbID = column[String]("imdb_id")

    def title = column[String]("title")

    def duration = column[Time]("duration", SqlType("TIME NOT NULL"))
  }

}
