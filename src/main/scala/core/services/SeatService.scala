package core.services

import core.Seat
import core.repositories.SeatStorage

import scala.concurrent.{ExecutionContext, Future}

class SeatService(seatStorage: SeatStorage)(implicit executionContext: ExecutionContext) {
  def getSeats: Future[Seq[Seat]] = seatStorage.getSeats

  def getSeat(id: Long): Future[Option[Seat]] = seatStorage.getSeat(id)

}
