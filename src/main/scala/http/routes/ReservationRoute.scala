package http.routes

import java.sql.Timestamp

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.Encoder
import io.circe.generic.auto._
import io.circe.syntax._

import scala.concurrent.ExecutionContext
import java.time.LocalDateTime

import core.Status
import io.circe.Decoder
import io.circe.Json
import io.circe.HCursor
import core.services.{ReservationForm, ReservationService}

class ReservationRoute(reservationService: ReservationService)(implicit executionContext: ExecutionContext)
    extends FailFastCirceSupport {

  import StatusCodes._
  import reservationService._

  implicit val StatusFormat: Encoder[Status.Status] with Decoder[Status.Status] = new Encoder[Status.Status]
    with Decoder[Status.Status] {
    override def apply(enum: Status.Status): Json = Encoder.encodeString.apply(enum.toString)

    override def apply(string: HCursor): Decoder.Result[Status.Status] =
      Decoder.decodeString.map(s => Status.withName(s)).apply(string)
  }

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
                case Right(reservation) => Created    -> reservation.asJson
                case Left(message)      => BadRequest -> message.asJson
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
