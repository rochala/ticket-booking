package core.screenings

import java.sql.Timestamp

import core.Screening
import core.halls.HallDataTable
import core.movies.MovieDataTable
import slick.lifted
import slick.lifted.TableQuery
import slick.sql.SqlProfile.ColumnOption.SqlType
import utils.DatabaseConnector

trait ScreeningTable extends MovieDataTable with HallDataTable {

  val screenings = TableQuery[Screenings]

  protected val databaseConnector: DatabaseConnector

  import databaseConnector.profile.api._

  class Screenings(tag: Tag) extends Table[Screening](tag, "screenings") {
    override def * =
      (id.?, hallID, movieID, screeningTime).<>(Screening.tupled, Screening.unapply)

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def screeningTime = column[Timestamp](
      "screening_time",
      SqlType("TIMESTAMP NOT NULL")
    )

    def hall = foreignKey("hall_fk", hallID, lifted.TableQuery[Halls])(_.id)

    def hallID = column[Long]("hallid")

    def movie = foreignKey("movie_fk", movieID, lifted.TableQuery[Movies])(_.id)

    def movieID = column[Long]("movieid")
  }

}
