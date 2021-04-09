package core.repositories

import core.Reservation
import core.tables.ReservationTable
import utils.DatabaseConnector

import scala.concurrent.{ExecutionContext, Future}

sealed trait ReservationStorage {
  def getReservations: Future[Seq[Reservation]]

  def getReservation(id: Long): Future[Option[Reservation]]

  def saveReservation(reservation: Reservation): Future[Reservation]

}

class DBReservationStorage(val databaseConnector: DatabaseConnector)(implicit executionContext: ExecutionContext)
    extends ReservationStorage
    with ReservationTable {

  import databaseConnector._
  import databaseConnector.profile.api._

  def getReservations: Future[Seq[Reservation]] = db.run(reservations.result)

  def getReservation(id: Long): Future[Option[Reservation]] = db.run(reservations.filter(_.id === id).result.headOption)

  def saveReservation(reservation: Reservation): Future[Reservation] =
    db.run(reservations returning reservations.map(r => r) += reservation)
}
