package core.reservations

import java.sql.Timestamp
import java.util.Calendar

import core.{Reservation, Seat}
import core.screenings.ScreeningStorage
import core.seats.SeatStorage

import scala.annotation.tailrec
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration.Duration

class ReservationService(
                          reservationStorage: ReservationStorage,
                          seatStorage: SeatStorage,
                          screeningStorage: ScreeningStorage
                        )(implicit executionContext: ExecutionContext) {
  def getReservations: Future[Seq[Reservation]] = reservationStorage.getReservations

  def getReservation(id: Long): Future[Option[Reservation]] = reservationStorage.getReservation(id)

  def makeReservation(reservationForm: ReservationForm): Future[Option[ReservationSummary]] = {
    val reservationTime = new Timestamp(Calendar.getInstance().getTimeInMillis)
    if (
      reservationForm.seats.isEmpty ||
        !checkReservationTime(reservationTime, reservationForm.screeningID) ||
        !checkFullName(reservationForm.name, reservationForm.surname)
    ) return Future(None)

    val seatsWithoutID = reservationForm.seats.map(s => Seat(None, 0, s.row, s.index, ticketTypeMapper(s.ticketType)))

    if (!checkRowsValues(seatsWithoutID, reservationForm.screeningID)) return Future(None)

    val reservation = Await.result(
      reservationStorage.saveReservation(
        Reservation(
          None,
          reservationForm.screeningID,
          reservationForm.name,
          reservationForm.surname,
          reservationTime,
          "UNPAID"
        )
      ),
      Duration.Inf
    )

    val seatsToReserve = seatsWithoutID.map(s => Seat(None, reservation.id.get, s.row, s.index, s.price))

    seatStorage.saveSeats(seatsToReserve)

    val totalPrice = seatsWithoutID.foldLeft(0.0)(_ + _.price)

    // println(reservationForm)
    // println(checkRowsValues(reservationForm.seats.map(s => Seat(None, 0, s.row, s.index, 1.0))))
    // println(checkFullName(reservationForm.name, reservationForm.surname))
    // println(checkReservationTime(93, Timestamp.valueOf("2021-03-25 19:00:00.0")))
    // println(checkReservationTime(93, Timestamp.valueOf("2021-03-25 18:00:00.0")))
    // println(checkReservationTime(93, Timestamp.valueOf("2021-03-25 18:45:00.0")))
    // println(checkReservationTime(93, Timestamp.valueOf("2021-03-25 18:44:59.0")))
    // println(checkReservationTime(93, Timestamp.valueOf("2021-03-25 19:01:00.0")))
    Future(Some(ReservationSummary(reservation.id.get, totalPrice, reservationTime)))
  }

  def ticketTypeMapper(ticketType: String): Double =
    ticketType match {
      case "adult" => 25.0
      case "student" => 18.0
      case "child" => 12.5
      case _ => -1.0
    }

  def checkReservationTime(reservationTime: Timestamp, screeningID: Long): Boolean = {
    val screening = Await.result(screeningStorage.getScreening(screeningID), Duration.Inf)
    screening match {
      case Some(screening) => (reservationTime.getTime + (15 * 60 * 1000)) < screening.screeningTime.getTime
      case None => false
    }
  }

  def checkFullName(name: String, surname: String): Boolean = {
    name.matches("""^\p{Lu}[\p{L}&&[^\p{Lu}]]{2,}""") &&
      surname.matches("""^\p{Lu}[\p{L}&&[^\p{Lu}]]{2,}(-\p{Lu}[\p{L}&&[^\p{Lu}]]{2,})?""")
  }

  def checkRowsValues(seats: List[Seat], screeningID: Long): Boolean = {

    @tailrec
    def checkFreeSpace(seats: List[Seat], previous: Seat): Boolean = {
      seats match {
        case Nil => true
        case x :: tail =>
          if (x.row == previous.row && (x.index - previous.index) == 2) false else checkFreeSpace(tail, x)
      }
    }

    val screeningData = Await.result(screeningStorage.getScreeningDetails(screeningID), Duration.Inf)
    if (screeningData.isEmpty) return false

    //check indices
    if (
      !seats.forall(seat =>
        seat.row < screeningData.get._3.rows
          && seat.row >= 0
          && seat.index < screeningData.get._3.columns
          && seat.index >= 0
      )
    ) return false

    //check if prices were good
    if (!seats.forall(_.price >= 0)) return false

    val seatsQueryResult =
      (Await.result(seatStorage.takenSeats(screeningID), Duration.Inf) ++ seats)
        .sortBy(s => (s.row, s.index))
    //check if seat already taken
    if (seatsQueryResult.distinctBy(s => (s.row, s.index)) != seatsQueryResult) return false

    //check for 1 slot spacing in row
    checkFreeSpace(seatsQueryResult.toList.tail, seatsQueryResult.head)
  }

  case class ReservationForm(screeningID: Long, name: String, surname: String, seats: List[SeatForm])

  case class SeatForm(row: Int, index: Int, ticketType: String)

  case class ReservationSummary(
                                 reservationID: Long,
                                 totalPrice: Double,
                                 reservedUntil: Timestamp
                               )

}
