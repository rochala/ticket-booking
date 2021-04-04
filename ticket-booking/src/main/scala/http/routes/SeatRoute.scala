package http.routes

import scala.concurrent.ExecutionContext
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._

import io.circe.generic.auto._
import io.circe.syntax._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.Encoder
import java.sql.Timestamp
import core.seats.SeatService

class SeatRoute(seatService: SeatService)(implicit
    executionContext: ExecutionContext,
    encoder: Encoder[Timestamp]
) extends FailFastCirceSupport {
  import StatusCodes._
  import seatService._

  val route = pathPrefix("seats") {
    concat(
      pathEndOrSingleSlash {
        get {
          complete(getSeats().map(_.asJson))
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
      },
      pathPrefix("avaliable") {
        path(LongNumber) { id =>
          pathEndOrSingleSlash {
            get {
              complete(avaliableSeats(id).map(_.asJson))
            }
          }

        }

      }

    )
  }
}
