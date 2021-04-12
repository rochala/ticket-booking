package core.repositories

import core.Reservation
import core.tables.ReservationTable
import utils.DatabaseConnector

import scala.concurrent.{ExecutionContext, Future}

class ReservationStorage(val databaseConnector: DatabaseConnector)(implicit executionContext: ExecutionContext)
    extends ReservationTable {

  import databaseConnector._
  import databaseConnector.profile.api._

  def getReservations: Future[Seq[Reservation]] = db.run(reservations.result)

  def getReservation(id: Long): Future[Option[Reservation]] = db.run(reservations.filter(_.id === id).result.headOption)

  def saveReservation(reservation: Reservation): Future[Reservation] =
    db.run(reservations returning reservations.map(r => r) += reservation)
}
