package core.reservations

import utils.DatabaseConnector
import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import core.reservations.ReservationTable
import core.Reservation

sealed trait ReservationStorage {
  def getReservations(): Future[Seq[Reservation]]

  def getReservation(id: Long): Future[Option[Reservation]]

}

class H2ReservationStorage(val databaseConnector: DatabaseConnector)(implicit executionContext: ExecutionContext)
    extends ReservationStorage
    with ReservationTable {

  import databaseConnector._
  import databaseConnector.profile.api._


  def getReservations(): Future[Seq[Reservation]] = db.run(reservations.result)

  def getReservation(id: Long): Future[Option[Reservation]] = db.run(reservations.filter(_.id === id).result.headOption)
}
