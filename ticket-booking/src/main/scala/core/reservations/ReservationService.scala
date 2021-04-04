package core.reservations

import core.Reservation
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

class ReservationService(reservationStorage: ReservationStorage)(implicit executionContext: ExecutionContext) {
  def getReservations(): Future[Seq[Reservation]] = reservationStorage.getReservations()

  def getReservation(id: Long): Future[Option[Reservation]] = reservationStorage.getReservation(id)
}
