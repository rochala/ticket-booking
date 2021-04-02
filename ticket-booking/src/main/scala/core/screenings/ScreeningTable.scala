package core.screenings

import core.Screening
import core.movies.MovieDataTable
import core.halls.HallDataTable
import utils.DatabaseConnector
import java.sql.Timestamp
import slick.driver.H2Driver.api._
import slick.sql.SqlProfile.ColumnOption.SqlType

trait ScreeningTable extends MovieDataTable with HallDataTable {

  protected val databaseConnector: DatabaseConnector
  import databaseConnector.profile.api._

  class Screenings(tag: Tag) extends Table[Screening](tag, "SCREENINGS") {
    def id      = column[Long]("SCREENING_ID", O.PrimaryKey, O.AutoInc)
    def hallID  = column[Long]("HALL_ID")
    def movieID = column[Long]("MOVIE_ID")
    def screeningTime = column[Timestamp](
      "SCREENING_TIME",
      SqlType("TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    )
    override def * =
      (id.?, hallID, movieID, screeningTime).<>(Screening.tupled, Screening.unapply)

    def hall  = foreignKey("HALL_FK", hallID, TableQuery[Halls])(_.id)
    def movie = foreignKey("MOVIE_FK", movieID, TableQuery[Movies])(_.id)
  }

  val screenings = TableQuery[Screenings]
}
