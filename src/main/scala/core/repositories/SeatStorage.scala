package core.repositories

import core.{Seat, Reservation}

import core.tables.SeatTable
import utils.DatabaseConnector

import scala.concurrent.{ExecutionContext, Future}

sealed trait SeatStorage {
  def getSeats: Future[Seq[Seat]]

  def getSeat(id: Long): Future[Option[Seat]]

  def takenSeats(screeningID: Long): Future[Seq[Seat]]

  def saveSeats(newSeats: List[Seat]): Future[Seq[Seat]]

  def fullReservationSave(newReservation: Reservation, newSeats: List[Seat])
}

class DBSeatStorage(val databaseConnector: DatabaseConnector)(implicit executionContext: ExecutionContext)
    extends SeatTable
    with SeatStorage {

  import databaseConnector._
  import databaseConnector.profile.api._

  def insertTransaction(newReservation: Reservation, newSeats: List[Seat]) = (for {
    reservation <- reservations returning reservations.map(r => r.id) += newReservation
    seats       <- seats ++= newSeats.map(seat => Seat(None, reservation, seat.row, seat.index, seat.price))
  } yield (reservation, seats)).transactionally

  val joinQuery = for {
    (seat, reservation) <-
      seats join reservations on (_.reservationID === _.id)
  } yield (reservation.screeningID, seat)

  def getSeats: Future[Seq[Seat]] = db.run(seats.result)

  def getSeat(id: Long): Future[Option[Seat]] = db.run(seats.filter(_.id === id).result.headOption)

  def takenSeats(screeningID: Long): Future[Seq[Seat]] =
    db.run(joinQuery.filter(_._1 === screeningID).map(_._2).distinct.result)

  def saveSeats(newSeats: List[Seat]): Future[Seq[Seat]] = db.run(seats returning seats ++= newSeats)

  def fullReservationSave(newReservation: Reservation, newSeats: List[Seat]) =
    db.run(insertTransaction(newReservation, newSeats))
}
