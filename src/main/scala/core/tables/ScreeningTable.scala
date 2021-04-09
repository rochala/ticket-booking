package core.tables

import java.time.LocalDateTime

import core.Screening
import slick.lifted
import slick.lifted.TableQuery
import utils.DatabaseConnector

trait ScreeningTable extends MovieDataTable with HallDataTable {

  val screenings = TableQuery[Screenings]

  protected val databaseConnector: DatabaseConnector

  import databaseConnector.profile.api._

  class Screenings(tag: Tag) extends Table[Screening](tag, "screenings") {
    override def * =
      (id.?, hallID, movieID, screeningTime).<>(Screening.tupled, Screening.unapply)

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def screeningTime = column[LocalDateTime]("screening_time")

    def hall = foreignKey("hall_fk", hallID, lifted.TableQuery[Halls])(_.id)

    def hallID = column[Long]("hall_id")

    def movie = foreignKey("movie_fk", movieID, lifted.TableQuery[Movies])(_.id)

    def movieID = column[Long]("movie_id")
  }

}
