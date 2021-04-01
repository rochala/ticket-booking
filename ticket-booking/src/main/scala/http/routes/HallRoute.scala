package http.routes

import core.halls.HallDataService
import scala.concurrent.ExecutionContext
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._

import io.circe.generic.auto._
import io.circe.syntax._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport

class HallRoute(hallService: HallDataService)(implicit executionContext: ExecutionContext) extends FailFastCirceSupport {
  import StatusCodes._
  import hallService._

  val route = pathPrefix("halls") {
    concat(
      pathEndOrSingleSlash {
          get {
            complete(getHalls().map(_.asJson))
          }
      },
      path(LongNumber) { id =>
        pathEndOrSingleSlash {
          get {
            complete(getHall(id).map {
              case Some(hall) =>
                OK -> hall.asJson
              case None =>
                BadRequest -> None.asJson
            }
            )
          }
        }
      }
      )
  }
}
