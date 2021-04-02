
package http.routes

import core.movies.MovieDataService
import scala.concurrent.ExecutionContext
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._

import io.circe.generic.auto._
import io.circe.syntax._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport

class MovieRoute(movieService: MovieDataService)(implicit executionContext: ExecutionContext) extends FailFastCirceSupport {
  import StatusCodes._
  import movieService._

  val route = pathPrefix("movies") {
    concat(
      pathEndOrSingleSlash {
          get {
            complete(getMovies().map(_.asJson))
          }
      },
      path(LongNumber) { id =>
        pathEndOrSingleSlash {
          get {
            complete(getMovie(id).map {
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
