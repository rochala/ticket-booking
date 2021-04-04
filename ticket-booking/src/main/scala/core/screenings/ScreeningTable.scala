package core.screenings

import core.Screening
import core.movies.MovieDataTable
import core.halls.HallDataTable
import utils.DatabaseConnector
import java.sql.Timestamp
import slick.sql.SqlProfile.ColumnOption.SqlType

trait ScreeningTable extends MovieDataTable with HallDataTable {

  protected val databaseConnector: DatabaseConnector
  import databaseConnector.profile.api._

  class Screenings(tag: Tag) extends Table[Screening](tag, "screenings") {
    def id      = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def hallID  = column[Long]("hallid")
    def movieID = column[Long]("movieid")
    def screeningTime = column[Timestamp](
      "screening_time",
      SqlType("TIMESTAMP NOT NULL")
    )
    override def * =
      (id.?, hallID, movieID, screeningTime).<>(Screening.tupled, Screening.unapply)

    def hall  = foreignKey("hall_fk", hallID, TableQuery[Halls])(_.id)
    def movie = foreignKey("movie_fk", movieID, TableQuery[Movies])(_.id)
  }

  val screenings = TableQuery[Screenings]
}
