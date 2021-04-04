package core.reservations

import utils.DatabaseConnector
import java.sql.Timestamp
import _root_.core.Reservation
import _root_.core.screenings.ScreeningTable
import slick.sql.SqlProfile

private[core] trait ReservationTable extends ScreeningTable {

  protected val databaseConnector: DatabaseConnector
  import databaseConnector.profile.api._

  class Reservations(tag: Tag) extends Table[Reservation](tag, "reservations") {
    def id          = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def screeningID = column[Long]("screeningid")
    def name        = column[String]("name")
    def surname     = column[String]("surname")
    def status      = column[String]("status")
    def reservatonTime = column[Timestamp](
      "reservation_time",
      SqlProfile.ColumnOption.SqlType("TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    )
    override def * =
      (id.?, screeningID, name, surname, reservatonTime, status).<>(Reservation.tupled, Reservation.unapply)
    def screening = foreignKey("screening_fk", screeningID, TableQuery[Screenings])(_.id)
  }

  protected val reservations = TableQuery[Reservations]
}
