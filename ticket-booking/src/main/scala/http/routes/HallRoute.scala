package http.routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import core.halls.HallDataService
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.generic.auto._
import io.circe.syntax._

import scala.concurrent.ExecutionContext

class HallRoute(hallService: HallDataService)(implicit executionContext: ExecutionContext)
    extends FailFastCirceSupport {

  import StatusCodes._
  import hallService._

  val route: Route = pathPrefix("halls") {
    concat(
      pathEndOrSingleSlash {
        get {
          complete(getHalls.map(_.asJson))
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
            })
          }
        }
      }
    )
  }
}
