package http

import core.halls.HallDataService
import scala.concurrent.ExecutionContext
import http.routes.HallRoute
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._

class HttpRoute(
  hallService: HallDataService
)(implicit executionContext: ExecutionContext) {

  private val hallsRouter = new HallRoute(hallService)

  val route: Route =
    pathPrefix("api") {
      hallsRouter.route
    }
}

