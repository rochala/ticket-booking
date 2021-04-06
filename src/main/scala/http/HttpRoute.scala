package http

import java.sql.{Time, Timestamp}

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import core.halls.HallDataService
import core.movies.MovieDataService
import core.reservations.ReservationService
import core.screenings.ScreeningService
import core.seats.SeatService
import http.routes._
import io.circe.{Decoder, Encoder, HCursor, Json}

import scala.concurrent.ExecutionContext

class HttpRoute(
    hallService: HallDataService,
    movieService: MovieDataService,
    screeningService: ScreeningService,
    reservationService: ReservationService,
    seatService: SeatService
)(implicit executionContext: ExecutionContext) {

  implicit val TimestampFormat: Encoder[Timestamp] with Decoder[Timestamp] = new Encoder[Timestamp]
    with Decoder[Timestamp] {
    override def apply(a: Timestamp): Json = Encoder.encodeString.apply(a.toString)

    override def apply(c: HCursor): Decoder.Result[Timestamp] =
      Decoder.decodeString.map(s => Timestamp.valueOf(s)).apply(c)
  }

  implicit val TimeFormat: Encoder[Time] with Decoder[Time] = new Encoder[Time] with Decoder[Time] {
    override def apply(a: Time): Json = Encoder.encodeString.apply(a.toString)

    override def apply(c: HCursor): Decoder.Result[Time] = Decoder.decodeString.map(s => Time.valueOf(s)).apply(c)
  }

  private val hallsRouter        = new HallRoute(hallService)
  private val moviesRouter       = new MovieRoute(movieService)
  private val screeningsRouter   = new ScreeningRoute(screeningService)
  private val reservationsRouter = new ReservationRoute(reservationService)
  private val seatsRouter        = new SeatRoute(seatService)

  val route: Route =
    pathPrefix("api") {
      concat(
        hallsRouter.route,
        moviesRouter.route,
        screeningsRouter.route,
        reservationsRouter.route,
        seatsRouter.route
      )
    }
}
