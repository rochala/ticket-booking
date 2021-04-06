package core.reservations

import java.sql.Timestamp

import _root_.core.Reservation
import _root_.core.screenings.ScreeningTable
import slick.sql.SqlProfile
import utils.DatabaseConnector

private[core] trait ReservationTable extends ScreeningTable {

  protected val databaseConnector: DatabaseConnector

  import databaseConnector.profile.api._

  protected val reservations = TableQuery[Reservations]

  class Reservations(tag: Tag) extends Table[Reservation](tag, "reservations") {
    override def * =
      (id.?, screeningID, name, surname, reservatonTime, status).<>(Reservation.tupled, Reservation.unapply)

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")

    def surname = column[String]("surname")

    def status = column[String]("status")

    def reservatonTime = column[Timestamp](
      "reservation_time",
      SqlProfile.ColumnOption.SqlType("TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    )

    def screening = foreignKey("screening_fk", screeningID, TableQuery[Screenings])(_.id)

    def screeningID = column[Long]("screeningid")
  }

}
