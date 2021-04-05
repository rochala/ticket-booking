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

class ScreeningRoute(screeningService: ScreeningService)(implicit executionContext: ExecutionContext, encoder: Encoder[Timestamp])
    extends FailFastCirceSupport {
  import StatusCodes._
  import screeningService._

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
            complete(screeningSchedule(new Timestamp(start), new Timestamp(end)).map(_.asJson))
          }
        }
      },
      pathPrefix("details") {
        path(LongNumber) { id =>
          pathEndOrSingleSlash {
            get {
              complete(screeningDetails(id).map {
                case Some(screening) =>
                  OK -> screening.asJson
                case None =>
                  BadRequest -> None.asJson
              })
            }
          }
        }
      }
    )
  }
}
