package core.definitions

import java.sql.Timestamp
import core.Screening
import slick.driver.H2Driver.api._
import slick.sql.SqlProfile.ColumnOption.SqlType

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
  // def hall  = foreignKey("HALL_FK", hallID, TableQuery[Halls])(_.id)
  def movie = foreignKey("MOVIE_FK", movieID, TableQuery[Movies])(_.id)
}

