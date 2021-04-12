package core.services

import java.time.LocalDateTime

import cats.data.ValidatedNec
import core.{Hall, Seat}
import utils.Config.reservationAdvanceMinutes
import cats.implicits._

import scala.annotation.tailrec
import scala.util.matching.Regex

object ReservationValidationService {
  def validateReservationTime(reservationTime: LocalDateTime, screeningTime: LocalDateTime): Boolean =
    reservationTime.plusMinutes(reservationAdvanceMinutes).isBefore(screeningTime)

  val nameRegex: Regex = """^\p{Lu}[\p{L}&&[^\p{Lu}]]{2,}(-\p{Lu}[\p{L}&&[^\p{Lu}]]{2,})?""".r

  sealed abstract class Validated[+E, +A] extends Product with Serializable {}

  final case class Valid[+A](a: A)   extends Validated[Nothing, A]
  final case class Invalid[+E](e: E) extends Validated[E, Nothing]

  sealed trait ReservationValidation {
    def errorMessage: String
  }

  case object FirstNameDoesNotMeetCriteria extends ReservationValidation {
    def errorMessage: String = "First name must start with capital letter and have at least 3 letters"
  }

  case object LastNameDoesNotMeetCriteria extends ReservationValidation {
    def errorMessage: String =
      "Last name must start with capital letter and have at least 3 letters. Applies for both parts in case of two part last name"
  }

  case object IllegalTicketTypeValue extends ReservationValidation {
    def errorMessage: String =
      "Ticket type must contain in (adult, student, child)"
  }

  case object TooLateToMakeReservation extends ReservationValidation {
    def errorMessage: String =
      s"Reservation must be made at least $reservationAdvanceMinutes minutes before screening"
  }

  case object SeatsAlreadyTaken extends ReservationValidation {
    def errorMessage: String =
      "Seats are already reserved"
  }

  case object SeatsDoesNotExist extends ReservationValidation {
    def errorMessage: String =
      "Seats does not exist"
  }

  case object SeatsDoesNotMeetCriteria extends ReservationValidation {
    def errorMessage: String =
      "Seats leaves single slot in a row"
  }

  case object NoSeatsSelected extends ReservationValidation {
    def errorMessage: String =
      "Reservation must reserve at least 1 seat"
  }

  sealed trait ReservationValidatorNec {
    type ValidationResult[A] = ValidatedNec[ReservationValidation, A]

    def validateFirstName(name: String): ValidationResult[String] = {
      if (nameRegex.matches(name)) name.validNec else FirstNameDoesNotMeetCriteria.invalidNec
    }

    def validateLastName(surname: String): ValidationResult[String] = {
      if (nameRegex.matches(surname)) surname.validNec else LastNameDoesNotMeetCriteria.invalidNec
    }

    def validateTicketTypes(seats: List[Seat]): ValidationResult[List[Seat]] =
      if (seats.forall(_.price >= 0)) seats.validNec else IllegalTicketTypeValue.invalidNec

    def validateSeatsIndices(seats: List[Seat], hall: Hall): ValidationResult[List[Seat]] =
      if (
        seats.forall(seat =>
          seat.row < hall.rows
            && seat.row >= 0
            && seat.index < hall.columns
            && seat.index >= 0
        )
      ) seats.validNec
      else SeatsDoesNotExist.invalidNec

    def validateSeatAvailability(seats: List[Seat], takenSeats: Seq[Seat]): ValidationResult[List[Seat]] = {
      val mergedSeats = takenSeats ++ seats
      if (mergedSeats.distinctBy(s => (s.row, s.index)) == mergedSeats) seats.validNec
      else SeatsAlreadyTaken.invalidNec
    }

    def validateRowsSpacing(seats: List[Seat], takenSeats: Seq[Seat]): ValidationResult[List[Seat]] = {
      @tailrec
      def checkFreeSpace(seats: List[Seat], previous: Seat): Boolean = {
        seats match {
          case Nil => true
          case x :: tail =>
            if (x.row == previous.row && (x.index - previous.index) == 2) false else checkFreeSpace(tail, x)
        }
      }
      val seatsQueryResult = (takenSeats ++ seats).sortBy(s => (s.row, s.index))

      if (checkFreeSpace(seatsQueryResult.toList.tail, seatsQueryResult.head)) seats.validNec
      else SeatsDoesNotMeetCriteria.invalidNec
    }

    def validateSeats(
        seats: List[Seat],
        takenSeats: Seq[Seat],
        hall: Hall
    ): ValidationResult[List[SeatForm]] = {
      validateRowsSpacing(seats, takenSeats)
        .andThen(validateSeatsNumber)
        .andThen(validateSeatAvailability(_, takenSeats))
        .andThen(validateSeatsIndices(_, hall))
        .andThen(validateTicketTypes)
        .map(_.map(seat => SeatForm(seat.row, seat.index, ReservationService.mapTicketType(seat.price))))
    }

    def validateSeatsNumber(seats: List[Seat]): ValidationResult[List[Seat]] =
      if (seats.nonEmpty) seats.validNec else NoSeatsSelected.invalidNec

    def validateReservation(
        reservationForm: ReservationForm,
        seats: List[Seat],
        takenSeats: Seq[Seat],
        hall: Hall
    ): ValidationResult[ReservationForm] = {
      (
        reservationForm.screeningID.validNec,
        validateFirstName(reservationForm.name),
        validateLastName(reservationForm.surname),
        validateSeats(seats, takenSeats, hall)
      ).mapN(ReservationForm)
    }
  }

  object ReservationValidatorNec extends ReservationValidatorNec
}
