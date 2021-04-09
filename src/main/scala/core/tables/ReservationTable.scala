package core.tables

import java.time.LocalDateTime

import core.{Reservation, Status}
import utils.DatabaseConnector

private[core] trait ReservationTable extends ScreeningTable {

  protected val databaseConnector: DatabaseConnector

  import databaseConnector.profile.api._

  protected val reservations = TableQuery[Reservations]

  implicit val statusColumnType = MappedColumnType.base[Status.Status, String](
    enum => enum.toString,
    string => Status.withName(string)
  )

  class Reservations(tag: Tag) extends Table[Reservation](tag, "reservations") {
    override def * =
      (id.?, screeningID, name, surname, reservatonTime, status).<>(Reservation.tupled, Reservation.unapply)

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")

    def surname = column[String]("surname")

    def status = column[Status.Status]("status")

    def reservatonTime = column[LocalDateTime](
      "reservation_time"
    )

    def screening = foreignKey("screening_fk", screeningID, TableQuery[Screenings])(_.id)

    def screeningID = column[Long]("screening_id")
  }

}
