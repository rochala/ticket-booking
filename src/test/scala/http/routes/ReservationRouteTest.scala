package http.routes

import akka.http.scaladsl.model.{HttpEntity, MediaTypes}
import akka.http.scaladsl.server.Route
import http.BaseServiceTest
import org.mockito.Mockito._

import scala.concurrent.Future
import scala.util.Random
import io.circe.generic.auto._
import io.circe.syntax._
import core.{Reservation, Seat, Status}
import core.services.SeatService
import core.services.ReservationService
import java.time.LocalDateTime

import io.circe.Encoder
import io.circe.Decoder
import io.circe.HCursor
import io.circe.Json
import core.services.ReservationSummary
import core.services.SeatForm
import core.services.ReservationForm
import core.services.ReservationValidationService._

class ReservationRouteTest extends BaseServiceTest {
  implicit val StatusFormat: Encoder[Status.Status] with Decoder[Status.Status] = new Encoder[Status.Status]
    with Decoder[Status.Status] {
    override def apply(enum: Status.Status): Json = Encoder.encodeString.apply(enum.toString)

    override def apply(string: HCursor): Decoder.Result[Status.Status] =
      Decoder.decodeString.map(s => Status.withName(s)).apply(string)
  }

  "ReservationRoute" when {
    "GET /api/reservations" should {
      "return 200 and list of all reservations JSON" in new Context {
        when(reservationService.getReservations).thenReturn(Future.successful(Seq(testReservation1, testReservation2)))

        Get("/reservations") ~> reservationRoute ~> check {
          responseAs[String] shouldBe Seq(testReservation1, testReservation2).asJson.noSpaces
          status.intValue shouldBe 200
        }
      }
    }

    "GET /api/reservations/:id" should {
      "return 200 and reservation JSON" in new Context {
        when(reservationService.getReservation(testReservation1.id.get))
          .thenReturn(Future.successful(Some(testReservation1)))

        Get("/reservations/" + testReservation1.id.get) ~> reservationRoute ~> check {
          responseAs[String] shouldBe testReservation1.asJson.noSpaces
          status.intValue shouldBe 200
        }
      }
      "return 400 if reservation does not exists" in new Context {
        when(reservationService.getReservation(testReservation1.id.get)).thenReturn(Future.successful(None))

        Get("/reservations/" + testReservation1.id.get) ~> reservationRoute ~> check {
          status.intValue shouldBe 400
        }
      }
    }

    "POST /api/reservations/" should {
      "create new reservation and return reserver name and surname," +
        "days to pay for reservation, total price and reserved seats" in new Context {
          when(reservationService.makeReservation(testReservationForm))
            .thenReturn(Future.successful(Right(testReservationSummary)))
          private val requestEntity = HttpEntity(MediaTypes.`application/json`, testReservationForm.asJson.noSpaces)

          Post("/reservations", requestEntity) ~> reservationRoute ~> check {
            responseAs[String] shouldBe testReservationSummary.asJson.noSpaces
            status.intValue shouldBe 201
          }
        }

      "return 400 when screening does not exist" in new Context {
        when(reservationService.makeReservation(testReservationForm))
          .thenReturn(Future.successful(Left("Screening does not exist")))
        private val requestEntity = HttpEntity(MediaTypes.`application/json`, testReservationForm.asJson.noSpaces)

        Post("/reservations", requestEntity) ~> reservationRoute ~> check {
          responseAs[String] shouldBe "Screening does not exist".asJson.noSpaces
          status.intValue shouldBe 400
        }
      }
      "return 400 and all errors when reservation form is incorrect" in new Context {
        private val selectedErrors = errors.slice(0, Random.between(1, errors.size))
        when(reservationService.makeReservation(testReservationForm))
          .thenReturn(Future.successful(Left(selectedErrors.mkString(". "))))
        private val requestEntity = HttpEntity(MediaTypes.`application/json`, testReservationForm.asJson.noSpaces)

        Post("/reservations", requestEntity) ~> reservationRoute ~> check {
          responseAs[String] shouldBe selectedErrors.mkString(". ").asJson.noSpaces
          status.intValue shouldBe 400
        }
      }
    }
  }

  trait Context {
    val testSeat1: SeatForm = testSeatForm()
    val testSeat2: SeatForm = testSeatForm()

    val reservationService: ReservationService = mock[ReservationService]
    val reservationRoute: Route                = new ReservationRoute(reservationService).route

    val testReservation1: Reservation = Reservation(Some(1), 1, "Test", "Test", LocalDateTime.now, Status.Unpaid)
    val testReservation2: Reservation =
      Reservation(Some(2), 1, "Name", "Test-Surname", LocalDateTime.now, Status.Unpaid)

    val testReservationForm: ReservationForm = ReservationForm(
      Random.nextInt(),
      "Name",
      "Test-Surname",
      List(testSeat1, testSeat2)
    )

    val errors: List[ReservationValidation] = Random.shuffle(
      List(
        FirstNameDoesNotMeetCriteria,
        LastNameDoesNotMeetCriteria,
        IllegalTicketTypeValue,
        TooLateToMakeReservation,
        SeatsAlreadyTaken,
        SeatsDoesNotExist,
        SeatsDoesNotMeetCriteria,
        NoSeatsSelected
      )
    )

    val testReservationSummary: ReservationSummary = ReservationSummary(
      testReservationForm.name,
      testReservationForm.surname,
      BigDecimal(50),
      testReservation1.reservationTime.plusDays(3),
      List(testSeat1, testSeat2)
    )

    private def testSeatForm() = SeatForm(
      Random.nextInt(),
      Random.nextInt(),
      "adult"
    )
  }
}
