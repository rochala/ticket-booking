package core.services

import java.time.LocalDateTime

import cats.data.Validated.{Invalid, Valid}
import core.repositories.{ReservationStorage, ScreeningStorage, SeatStorage}
import core.services.ReservationValidationService._
import core.{Reservation, Seat, Status}
import utils.Config._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

case class ReservationSummary(
    name: String,
    surname: String,
    totalPrice: BigDecimal,
    reservedUntill: LocalDateTime,
    seats: List[SeatForm]
)

case class SeatForm(row: Int, index: Int, ticketType: String)
case class ReservationForm(screeningID: Long, name: String, surname: String, seats: List[SeatForm])

class ReservationService(
    reservationStorage: ReservationStorage,
    seatStorage: SeatStorage,
    screeningStorage: ScreeningStorage
)(implicit executionContext: ExecutionContext) {
  def getReservations: Future[Seq[Reservation]] = reservationStorage.getReservations

  def getReservation(id: Long): Future[Option[Reservation]] = reservationStorage.getReservation(id)

  def makeReservation(reservationForm: ReservationForm): Future[Either[String, ReservationSummary]] = {

    val data = for {
      screeningData <- screeningStorage.getScreeningDetails(reservationForm.screeningID)
      takenSeats    <- seatStorage.takenSeats(reservationForm.screeningID)
    } yield (screeningData, takenSeats)

    data.map { data =>
      if (data._1.isEmpty) None

      val screeningData = data._1.get
      val takenSeats    = data._2

      val reservationTime = LocalDateTime.now()
      val paymentTime =
        if (reservationTime.plusDays(reservationDays).isBefore(screeningData._1.screeningTime))
          reservationTime.plusDays(reservationDays)
        else screeningData._1.screeningTime

      val seatsWithoutID =
        reservationForm.seats.map(s => Seat(None, 0, s.row, s.index, ReservationService.mapTicketType(s.ticketType)))

      ReservationValidatorNec
        .validateReservation(
          reservationForm,
          seatsWithoutID,
          takenSeats,
          screeningData._3
        ) match {
        case Valid(reservation) => {
          val newReservation = Reservation(
            None,
            reservation.screeningID,
            reservation.name,
            reservation.surname,
            reservationTime,
            Status.Unpaid
          )
          seatStorage.fullReservationSave(newReservation, seatsWithoutID)

          Right(
            ReservationSummary(
              reservation.name,
              reservation.surname,
              seatsWithoutID.foldLeft(BigDecimal(0, 2))(_ + _.price),
              paymentTime,
              reservation.seats
            )
          )

        }
        case Invalid(failures) => Left(failures.iterator.map(_.errorMessage).mkString(",\n "))
      }
    }
  }
}

object ReservationService {
  val ticketTypeMap = Map(adultPrice -> "adult", studentPrice -> "student", childPrice -> "child")

  def mapTicketType(ticketType: String): BigDecimal =
    ticketTypeMap.find(_._2 == ticketType.toLowerCase) match {
      case Some(key) => BigDecimal(key._1)
      case None      => BigDecimal(-1, 0)
    }

  def mapTicketType(ticketType: BigDecimal): String =
    ticketTypeMap.get(ticketType.toDouble) match {
      case Some(value) => value
      case None        => throw new Exception("There is no such value in ticket types")
    }
}
