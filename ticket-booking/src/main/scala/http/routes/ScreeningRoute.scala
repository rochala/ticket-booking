package http.routes

import core.screenings.ScreeningService
import scala.concurrent.ExecutionContext
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._

import io.circe.generic.auto._
import io.circe.syntax._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.Encoder
import java.sql.Timestamp
import io.circe.Decoder
import io.circe.Json
import io.circe.HCursor
import java.sql.Date
import akka.http.scaladsl.model.DateTime


class ScreeningRoute(screeningService: ScreeningService)(implicit executionContext: ExecutionContext)
    extends FailFastCirceSupport {
  import StatusCodes._
  import screeningService._

  implicit val TimestampFormat: Encoder[Timestamp] with Decoder[Timestamp] = new Encoder[Timestamp]
    with Decoder[Timestamp] {
    override def apply(a: Timestamp): Json = Encoder.encodeLong.apply(a.getTime)

    override def apply(c: HCursor): Decoder.Result[Timestamp] = Decoder.decodeLong.map(s => new Timestamp(s)).apply(c)
  }

  implicit val DateFormat: Encoder[Date] with Decoder[Date] = new Encoder[Date]
    with Decoder[Date] {
    override def apply(a: Date): Json = Encoder.encodeLong.apply(a.getTime)

    override def apply(c: HCursor): Decoder.Result[Date] = Decoder.decodeLong.map(s => new Date(s)).apply(c)
  }


  val route = pathPrefix("screenings") {
    concat(
      pathEndOrSingleSlash {
        get {
          complete(allScreenings().map(_.asJson))
        }
      },
      path(LongNumber) { id =>
        pathEndOrSingleSlash {
          get {
            complete(getScreening(id).map {
              case Some(screening) =>
                OK -> screening.asJson
              case None =>
                BadRequest -> None.asJson
            })
          }
        }
      },
      path(LongNumber / LongNumber) { (start, end) =>
        pathEndOrSingleSlash {
          get {
            complete(screeningSchedule(new Timestamp(start),new Timestamp(end)).map(_.asJson))
          }
        }
      }
    )
  }
}
