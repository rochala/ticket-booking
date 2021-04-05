package http.routes

import scala.concurrent.ExecutionContext
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._

import io.circe.generic.auto._
import io.circe.syntax._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import core.reservations.ReservationService
import io.circe.Encoder
import java.sql.Timestamp

class ReservationRoute(reservationService: ReservationService)(implicit
    executionContext: ExecutionContext,
    encoder: Encoder[Timestamp]
) extends FailFastCirceSupport {
  import StatusCodes._
  import reservationService._

  val route = pathPrefix("reservations") {
    concat(
      pathEndOrSingleSlash {
        concat(
          get {
            complete(getReservations().map(_.asJson))
          },
          post {
            entity(as[ReservationForm]) { reservationForm =>
              complete(makeReservation(reservationForm).map {
                case Some(reservation) => OK         -> reservation.asJson
                case None              => BadRequest -> None.asJson
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
