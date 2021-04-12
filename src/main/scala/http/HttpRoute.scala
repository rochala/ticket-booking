package http

import java.sql.{Time, Timestamp}

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import http.routes._
import io.circe.{Decoder, Encoder, HCursor, Json}

import scala.concurrent.ExecutionContext
import java.time.LocalDateTime

import core.services.{HallDataService, MovieDataService, ReservationService, ScreeningService, SeatService}

class HttpRoute(
    hallService: HallDataService,
    movieService: MovieDataService,
    screeningService: ScreeningService,
    reservationService: ReservationService,
    seatService: SeatService
)(implicit executionContext: ExecutionContext) {
  private val hallsRouter        = new HallRoute(hallService)
  private val moviesRouter       = new MovieRoute(movieService)
  private val screeningsRouter   = new ScreeningRoute(screeningService)
  private val reservationsRouter = new ReservationRoute(reservationService)
  private val seatsRouter        = new SeatRoute(seatService)

  val route: Route =
    concat(
      pathPrefix("api") {
        concat(
          hallsRouter.route,
          moviesRouter.route,
          screeningsRouter.route,
          reservationsRouter.route,
          seatsRouter.route
        )
      },
      pathPrefix("healthcheck") {
        get {
          complete("OK")
        }
      }
    )
}
