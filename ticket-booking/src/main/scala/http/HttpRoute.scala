package http

import core.halls.HallDataService
import core.movies.MovieDataService
import core.screenings.ScreeningService
import scala.concurrent.ExecutionContext
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import http.routes._
import core.seats.SeatService
import core.reservations.ReservationService
import io.circe.Encoder
import io.circe.Decoder
import io.circe.Json
import io.circe.HCursor
import java.sql.Timestamp

class HttpRoute(
  hallService: HallDataService,
  movieService: MovieDataService,
  screeningService: ScreeningService,
  reservationService: ReservationService,
  seatService: SeatService
)(implicit executionContext: ExecutionContext) {

  implicit val TimestampFormat: Encoder[Timestamp] with Decoder[Timestamp] = new Encoder[Timestamp]
    with Decoder[Timestamp] {
    override def apply(a: Timestamp): Json = Encoder.encodeLong.apply(a.getTime)

    override def apply(c: HCursor): Decoder.Result[Timestamp] = Decoder.decodeLong.map(s => new Timestamp(s)).apply(c)
  }


  private val hallsRouter = new HallRoute(hallService)
  private val moviesRouter = new MovieRoute(movieService)
  private val screeningsRouter = new ScreeningRoute(screeningService)
  private val reservationsRouter = new ReservationRoute(reservationService)
  private val seatsRouter = new SeatRoute(seatService)

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

