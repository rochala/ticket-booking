package http.routes

import java.sql.Timestamp

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import core.services.SeatService
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.Encoder
import io.circe.generic.auto._
import io.circe.syntax._

import scala.concurrent.ExecutionContext

class SeatRoute(seatService: SeatService)(implicit executionContext: ExecutionContext) extends FailFastCirceSupport {
  import StatusCodes._
  import seatService._

  val route: Route = pathPrefix("seats") {
    concat(
      pathEndOrSingleSlash {
        get {
          complete(getSeats.map(_.asJson))
        }
      },
      path(LongNumber) { id =>
        pathEndOrSingleSlash {
          get {
            complete(getSeat(id).map {
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
