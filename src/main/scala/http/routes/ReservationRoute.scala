package http.routes

import java.sql.Timestamp

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import core.reservations.ReservationService
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.Encoder
import io.circe.generic.auto._
import io.circe.syntax._

import scala.concurrent.ExecutionContext

class ReservationRoute(reservationService: ReservationService)(implicit
                                                               executionContext: ExecutionContext,
                                                               encoder: Encoder[Timestamp]
) extends FailFastCirceSupport {

  import StatusCodes._
  import reservationService._

  val route: Route = pathPrefix("reservations") {
    concat(
      pathEndOrSingleSlash {
        concat(
          get {
            complete(getReservations.map(_.asJson))
          },
          post {
            entity(as[ReservationForm]) { reservationForm =>
              complete(makeReservation(reservationForm).map {
                case (Some(reservation), None) => OK -> reservation.asJson
                case (None, Some(error)) => BadRequest -> error.asJson
                case _ => BadRequest -> None.asJson
              })
            }
          }
        )
      },
      path(LongNumber) { id =>
        pathEndOrSingleSlash {
          get {
            complete(getReservation(id).map {
              case Some(reservation) =>
                OK -> reservation.asJson
              case None =>
                BadRequest -> None.asJson
            })
          }
        }
      }
    )
  }
}
