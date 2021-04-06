import org.scalatest.flatspec.AnyFlatSpec
import core.reservations.ReservationService
import core.reservations.ReservationStorage
import utils.DatabaseConnector
import core.reservations.DBReservationStorage
import akka.actor.ActorSystem
import scala.concurrent.ExecutionContext
import core.seats.DBSeatStorage
import core.screenings.DBScreeningStorage
import java.sql.Timestamp
import core.Seat
import core.Hall

class ReservationSpec extends AnyFlatSpec {
  val databaseConnector = new DatabaseConnector()
  implicit val actorSystem: ActorSystem = ActorSystem()
  implicit val executor: ExecutionContext = actorSystem.dispatcher

  val reservationStorage = new DBReservationStorage(databaseConnector)
  val seatStorage = new DBSeatStorage(databaseConnector)
  val screeningStorage = new DBScreeningStorage(databaseConnector)

  val reservation = new ReservationService(reservationStorage, seatStorage, screeningStorage)

  "Name" should "start with capital letter" in {
    assert(reservation.checkFullName("Jedrzej", "Rochala"))
    assert(!reservation.checkFullName("jedrzej", "Rochala"))
  }
  it should "allow polish letters" in {
    assert(reservation.checkFullName("Jędrzej", "Rochala"))
    assert(reservation.checkFullName("Ądrzej", "Rochala"))
  }
  it should "has at least 3 letters" in {
    assert(!reservation.checkFullName("Ej", "Rochala"))
    assert(reservation.checkFullName("Sss", "Rochala"))
    assert(!reservation.checkFullName("", "Rochala"))
  }

  "Surname" should "start with capital letter" in {
    assert(reservation.checkFullName("Jedrzej", "Rochala"))
    assert(!reservation.checkFullName("jedrzej", "rochala"))
  }
  it should "allow to have double surname" in {
    assert(reservation.checkFullName("Jedrzej", "Rochala-Sostrs"))
    assert(reservation.checkFullName("Jedrzej", "Rochala-Sostrs"))
  }
  it should "second part to have start with capital letter" in {
    assert(reservation.checkFullName("Jedrzej", "Rochala-Sostrs"))
    assert(!reservation.checkFullName("Jedrzej", "Rochala-ostrs"))
  }
  it should "allow polish letters" in {
    assert(reservation.checkFullName("Jedrzej", "Rochąla"))
    assert(reservation.checkFullName("Jedrzej", "Ąochxla"))
    assert(reservation.checkFullName("Jedrzej", "Rochala-Sóstrs"))
    assert(reservation.checkFullName("Jedrzej", "Rochala-Óstrs"))
    assert(reservation.checkFullName("Jedrzej", "Rochąla-Óstrs"))
    assert(!reservation.checkFullName("Jedrzej", "Rochąla-Óstrs-Strs"))
  }

  //screeningID = 1 Timestamp is 2021-04-29 15:00:00
  "Reservation time" should "be possible at least 15:00 minutes before screening" in {
    val screeningTime = Timestamp.valueOf("2021-04-29 15:00:00")
    assert(!reservation.checkReservationTime(Timestamp.valueOf("2021-04-29 15:00:00"), screeningTime))
    assert(!reservation.checkReservationTime(Timestamp.valueOf("2021-04-29 14:59:99"), screeningTime))
    assert(!reservation.checkReservationTime(Timestamp.valueOf("2021-04-29 15:00:01"), screeningTime))
    assert(reservation.checkReservationTime(Timestamp.valueOf("2021-04-29 14:44:99"), screeningTime))
    assert(reservation.checkReservationTime(Timestamp.valueOf("2021-04-29 14:45:00"), screeningTime))
    assert(reservation.checkReservationTime(Timestamp.valueOf("2021-04-20 14:45:00"), screeningTime))
    assert(!reservation.checkReservationTime(Timestamp.valueOf("2021-08-21 14:45:00"), screeningTime))
  }


  "Rows" should "not allow to have single space between 2 reserved seats" in {
    val takenSeats = Seq(
      Seat(None,0,0,0,18.0), Seat(None,0,0,3,18.0),
    )
    val hall = Hall(None, "TestHall", 18, 30)
    assert(reservation.checkRowsValues(List(Seat(None, 0, 0, 10, 0.0)), takenSeats, hall))
    assert(reservation.checkRowsValues(List(Seat(None, 0, 0, 4, 0.0)), takenSeats, hall))
    assert(reservation.checkRowsValues(List(Seat(None, 0, 0, 8, 0.0), Seat(None, 0, 0, 9, 0.0)), takenSeats, hall))
    assert(!reservation.checkRowsValues(List(Seat(None, 0, 0, 8, 0.0), Seat(None, 0, 0, 10, 0.0)), takenSeats, hall))

    assert(!reservation.checkRowsValues(List(Seat(None, 0, 0, 1, 0.0)), takenSeats, hall))
    assert(!reservation.checkRowsValues(List(Seat(None, 0, 0, 2, 0.0)), takenSeats, hall))
    assert(reservation.checkRowsValues(List(Seat(None, 0, 0, 1, 0.0),Seat(None, 0, 0, 2, 0.0)), takenSeats, hall))
  }

  "Tickets" should "have type in (adult, student, child)" in {
    assert(reservation.ticketTypeMapper("adult") == 25.0)
    assert(reservation.ticketTypeMapper("student") == 18.0)
    assert(reservation.ticketTypeMapper("child") == 12.5)
    assert(reservation.ticketTypeMapper("other value") == -1.0)
  }
}
