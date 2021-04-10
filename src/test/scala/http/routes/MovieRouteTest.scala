package http.routes

import akka.http.scaladsl.model.{HttpEntity, MediaTypes}
import akka.http.scaladsl.server.Route
import http.BaseServiceTest
import org.mockito.Mockito._

import scala.concurrent.Future
import core.services.MovieDataService
import core.Movie
import scala.util.Random
import java.time.LocalTime
import io.circe.generic.auto._
import io.circe.syntax._

class MovieRouteTest extends BaseServiceTest {
  "MovieRoute" when {
    "GET /api/movies" should {
      "return 200 and list of all movies JSON" in new Context {
        when(movieService.getMovies).thenReturn(Future.successful(Seq(testMovie1, testMovie2)))

        Get("/movies") ~> movieRoute ~> check {
          responseAs[String] shouldBe Seq(testMovie1, testMovie2).asJson.noSpaces
          status.intValue shouldBe 200
        }
      }
    }

    "GET /api/movies/:id" should {
      "return 200 and movie JSON" in new Context {
        when(movieService.getMovie(testMovie1.id.get)).thenReturn(Future.successful(Some(testMovie1)))

        Get("/movies/" + testMovie1.id.get) ~> movieRoute ~> check {
          responseAs[String] shouldBe testMovie1.asJson.noSpaces
          status.intValue shouldBe 200
        }
      }
      "return 400 if movie does not exists" in new Context {
        when(movieService.getMovie(testMovie1.id.get)).thenReturn(Future.successful(None))

        Get("/movies/" + testMovie1.id.get) ~> movieRoute ~> check {
          status.intValue shouldBe 400
        }
      }
    }
  }

  trait Context {
    val movieService: MovieDataService = mock[MovieDataService]
    val movieRoute: Route              = new MovieRoute(movieService).route

    val testMovie1: Movie = testMovie(1)
    val testMovie2: Movie = testMovie(2)

    private def testMovie(id: Long) = Movie(
      Some(id),
      Random.nextString(7),
      Random.nextString(10),
      LocalTime.ofSecondOfDay(Random.between(0, 86400))
    )
  }
}
