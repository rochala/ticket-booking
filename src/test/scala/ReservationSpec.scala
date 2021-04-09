import org.scalatest.flatspec.AnyFlatSpec
import core.services.ReservationValidationService._
import core.services.ReservationValidationService.ReservationValidatorNec._
import java.sql.Timestamp
import core.Seat
import core.Hall
import java.time.LocalDateTime
import core.services.ReservationValidationService
import core.services.ReservationService.mapTicketType

class ReservationSpec extends AnyFlatSpec {
  "Name" should "start with capital letter" in {
    assert(validateFirstName("Jedrzej").isValid)
    assert(validateFirstName("jedrzej").isInvalid)
  }
  it should "allow polish letters" in {
    assert(validateFirstName("Jędrzej").isValid)
    assert(validateFirstName("Ądrzej").isValid)
  }
  it should "has at least 3 letters" in {
    assert(validateFirstName("Ej").isInvalid)
    assert(validateFirstName("Sss").isValid)
    assert(validateFirstName("").isInvalid)
  }

  "Surname" should "start with capital letter" in {
    assert(validateLastName("Rochala").isValid)
    assert(validateLastName("rochala").isInvalid)
  }
  it should "allow to have double surname" in {
    assert(validateLastName("Rochala-Sostrs").isValid)
    assert(validateLastName("Rochąla-Ostrs-Strs").isInvalid)
  }
  it should "both parts have to start with capital letter" in {
    assert(validateLastName("Rochala-Sostrs").isValid)
    assert(validateLastName("Rochala-ostrs").isInvalid)
    assert(validateLastName("rochala-Sostrs").isInvalid)
    assert(validateLastName("rochala-ostrs").isInvalid)
  }
  it should "allow polish letters" in {
    assert(validateLastName("Rochąla").isValid)
    assert(validateLastName("Ąochxla").isValid)
    assert(validateLastName("Rochala-Sóstrs").isValid)
    assert(validateLastName("Rochala-Óstrs").isValid)
    assert(validateLastName("Rochąla-Óstrs").isValid)
    assert(validateLastName("Rochąla-Óstrs-Strs").isInvalid)
  }

  //screeningID = 1 Timestamp is 2021-04-29 15:00:00
  "Reservation time" should "be possible at least 15:00 minutes before screening" in {
    val screeningTime = LocalDateTime.parse("2021-04-29T15:00:00")
    assert(!validateReservationTime(LocalDateTime.parse("2021-04-29T15:00:00"), screeningTime))
    assert(!validateReservationTime(LocalDateTime.parse("2021-04-29T14:59:59"), screeningTime))
    assert(!validateReservationTime(LocalDateTime.parse("2021-04-29T15:00:01"), screeningTime))
    assert(validateReservationTime(LocalDateTime.parse("2021-04-29T14:44:59"), screeningTime))
    assert(!validateReservationTime(LocalDateTime.parse("2021-04-29T14:45:00"), screeningTime))
    assert(validateReservationTime(LocalDateTime.parse("2021-04-20T14:45:00"), screeningTime))
    assert(!validateReservationTime(LocalDateTime.parse("2021-08-21T14:45:00"), screeningTime))
  }

  "Rows" should "not allow to have single space between 2 reserved seats" in {
    val takenSeats = Seq(
      Seat(None, 0, 0, 0, 18.0),
      Seat(None, 0, 0, 3, 18.0)
    )
    val hall = Hall(None, "TestHall", 18, 30)
    assert(validateRowsSpacing(List(Seat(None, 0, 0, 10, 0.0)), takenSeats).isValid)
    assert(validateRowsSpacing(List(Seat(None, 0, 0, 4, 0.0)), takenSeats).isValid)
    assert(validateRowsSpacing(List(Seat(None, 0, 0, 8, 0.0), Seat(None, 0, 0, 9, 0.0)), takenSeats).isValid)
    assert(validateRowsSpacing(List(Seat(None, 0, 0, 1, 0.0), Seat(None, 0, 0, 2, 0.0)), takenSeats).isValid)

    assert(validateRowsSpacing(List(Seat(None, 0, 0, 8, 0.0), Seat(None, 0, 0, 10, 0.0)), takenSeats).isInvalid)
    assert(validateRowsSpacing(List(Seat(None, 0, 0, 1, 0.0)), takenSeats).isInvalid)
    assert(validateRowsSpacing(List(Seat(None, 0, 0, 2, 0.0)), takenSeats).isInvalid)
  }

  "Tickets" should "have type in (adult, student, child)" in {
    assert(mapTicketType("adult") == 25.0)
    assert(mapTicketType("student") == 18.0)
    assert(mapTicketType("child") == 12.5)
    assert(mapTicketType("other value") == -1.0)
  }
}
