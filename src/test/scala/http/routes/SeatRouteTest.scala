package http.routes

import akka.http.scaladsl.model.{HttpEntity, MediaTypes}
import akka.http.scaladsl.server.Route
import http.BaseServiceTest
import org.mockito.Mockito._

import scala.concurrent.Future
import scala.util.Random
import io.circe.generic.auto._
import io.circe.syntax._
import core.Seat
import core.services.SeatService

class SeatRouteTest extends BaseServiceTest {
  "SeatRoute" when {
    "GET /api/seats " should {
      "return 200 and list of all seats JSON" in new Context {
        when(seatService.getSeats).thenReturn(Future.successful(Seq(testSeat1, testSeat2)))

        Get("/seats") ~> seatRoute ~> check {
          responseAs[String] shouldBe Seq(testSeat1, testSeat2).asJson.noSpaces
          status.intValue shouldBe 200
        }
      }
    }

    "GET /api/seats/:id" should {
      "return 200 and seat JSON" in new Context {
        when(seatService.getSeat(testSeat1.id.get)).thenReturn(Future.successful(Some(testSeat1)))

        Get("/seats/" + testSeat1.id.get) ~> seatRoute ~> check {
          responseAs[String] shouldBe testSeat1.asJson.noSpaces
          status.intValue shouldBe 200
        }
      }
      "return 400 if seat does not exists" in new Context {
        when(seatService.getSeat(testSeat1.id.get)).thenReturn(Future.successful(None))

        Get("/seats/" + testSeat1.id.get) ~> seatRoute ~> check {
          status.intValue shouldBe 400
        }
      }
    }
  }

  trait Context {
    val seatService: SeatService = mock[SeatService]
    val seatRoute: Route         = new SeatRoute(seatService).route

    val testSeat1: Seat = testSeat(1)
    val testSeat2: Seat = testSeat(2)

    private def testSeat(id: Long) = Seat(
      Some(id),
      Random.nextInt(),
      Random.nextInt(),
      Random.nextInt(),
      BigDecimal(Random.nextDouble())
    )
  }
}
