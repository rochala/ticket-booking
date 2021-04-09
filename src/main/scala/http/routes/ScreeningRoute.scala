package http.routes

import java.sql.{Time, Timestamp}

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.Encoder
import io.circe.generic.auto._
import io.circe.syntax._

import scala.concurrent.ExecutionContext
import java.time.LocalDateTime

import core.services.ScreeningService

class ScreeningRoute(screeningService: ScreeningService)(implicit
    executionContext: ExecutionContext,
    timeEncoder: Encoder[Time]
) extends FailFastCirceSupport {

  import StatusCodes._
  import screeningService._

  val route: Route = pathPrefix("screenings") {
    concat(
      pathEndOrSingleSlash {
        get {
          complete(allScreenings.map(_.asJson))
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
      },
      path(Segment / Segment) { (start, end) =>
        pathEndOrSingleSlash {
          get {
            complete(screeningSchedule(LocalDateTime.parse(start), LocalDateTime.parse(end)).map(_.asJson))
          }
        }
      }
    )
  }
}
