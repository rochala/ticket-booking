package http

import akka.http.scaladsl.server.Route
import http.BaseServiceTest
import core.services.HallDataService
import core.services.MovieDataService
import core.services.ScreeningService
import core.services.ReservationService
import core.services.SeatService

class HttpRouteTest extends BaseServiceTest {
  "HttpRoute" when {
    "GET /healthcheck" should {
      "return 200 OK" in new Context {
        Get("/healthcheck") ~> httpRoute ~> check {
          responseAs[String] shouldBe "OK"
          status.intValue() shouldBe 200
        }
      }
    }
  }

  trait Context {
    val movieService: MovieDataService = mock[MovieDataService]
    val hallService: HallDataService = mock[HallDataService]
    val screeningService: ScreeningService = mock[ScreeningService]
    val reservationService: ReservationService = mock[ReservationService]
    val seatService: SeatService = mock[SeatService]

    val httpRoute: Route =
      new HttpRoute(hallService, movieService, screeningService, reservationService, seatService).route
  }
}
