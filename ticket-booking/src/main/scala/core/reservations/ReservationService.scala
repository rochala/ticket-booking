package core.reservations

import core.Reservation
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import core.seats.SeatStorage

class ReservationService(reservationStorage: ReservationStorage, seatStorage: SeatStorage)(implicit executionContext: ExecutionContext) {
  def getReservations(): Future[Seq[Reservation]] = reservationStorage.getReservations()

  def getReservation(id: Long): Future[Option[Reservation]] = reservationStorage.getReservation(id)

  case class ReservationForm(screeningID: Long, name: String, surname: String, seats: List[Boolean])
}
