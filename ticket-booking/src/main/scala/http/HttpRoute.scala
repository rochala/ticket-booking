package http

import core.halls.HallDataService
import core.movies.MovieDataService
import core.screenings.ScreeningService
import scala.concurrent.ExecutionContext
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import http.routes._

class HttpRoute(
  hallService: HallDataService,
  movieService: MovieDataService,
  screeningService: ScreeningService
)(implicit executionContext: ExecutionContext) {

  private val hallsRouter = new HallRoute(hallService)
  private val moviesRouter = new MovieRoute(movieService)
  private val screeningsRouter = new ScreeningRoute(screeningService)

  val route: Route =
    pathPrefix("api") {
      concat(
        hallsRouter.route,
        moviesRouter.route,
        screeningsRouter.route
      )
    }
}

