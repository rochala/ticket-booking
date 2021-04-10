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
import core.services.HallDataService
import core.Hall
import core.services.ScreeningService
import java.time.LocalDateTime
import core.Screening
import core.services.ScreeningDetails
import core.services.ScreeningHeader
import core.services.MovieSchedule

class ScreeningRouteTest extends BaseServiceTest {
  "ScreeningRoute" when {
    "GET /api/screenings" should {
      "return 200 and list of all screenings JSON" in new Context {
        when(screeningService.allScreenings).thenReturn(
          Future.successful(Seq(testScreening1, testScreening2, testScreening3, testScreening4))
        )

        Get("/screenings") ~> screeningRoute ~> check {
          responseAs[String] shouldBe Seq(
            testScreening1,
            testScreening2,
            testScreening3,
            testScreening4
          ).asJson.noSpaces
          status.intValue shouldBe 200
        }
      }
    }

    "GET /api/screenings/:id" should {
      "return 200 and screening JSON" in new Context {
        when(screeningService.getScreening(testScreening1.id.get)).thenReturn(Future.successful(Some(testScreening1)))

        Get("/screenings/" + testScreening1.id.get) ~> screeningRoute ~> check {
          responseAs[String] shouldBe testScreening1.asJson.noSpaces
          status.intValue shouldBe 200
        }
      }
      "return 400 if screening does not exists" in new Context {
        when(screeningService.getScreening(testScreening1.id.get)).thenReturn(Future.successful(None))

        Get("/screenings/" + testScreening1.id.get) ~> screeningRoute ~> check {
          status.intValue shouldBe 400
        }
      }
    }

    "GET /api/screenings/details/:id" should {
      "return 200 and screening details JSON" in new Context {
        when(screeningService.screeningDetails(testScreening1.id.get))
          .thenReturn(Future.successful(Some(testScreeningDetails)))

        Get("/screenings/details/" + testScreening1.id.get) ~> screeningRoute ~> check {
          responseAs[String] shouldBe testScreeningDetails.asJson.noSpaces
          status.intValue shouldBe 200
        }
      }
      "return 400 if screening does not exists" in new Context {
        when(screeningService.screeningDetails(testScreening1.id.get)).thenReturn(Future.successful(None))

        Get("/screenings/details/" + testScreening1.id.get) ~> screeningRoute ~> check {
          status.intValue shouldBe 400
        }
      }
    }

    "GET /api/screenings/:startdate/:enddate" should {
      "return 200 and screenings in given range JSON" in new Context {
        when(screeningService.screeningSchedule(startTime.toString, endTime.toString))
          .thenReturn(Future.successful(Some(Seq(testMovieSchedule1, testMovieSchedule2))))

        Get("/screenings/" + startTime + "/" + endTime) ~> screeningRoute ~> check {
          responseAs[String] shouldBe Seq(testMovieSchedule1, testMovieSchedule2).asJson.noSpaces
          status.intValue shouldBe 200
        }
      }
      "return 200 and empty seq if there are no screenings in given range" in new Context {
        when(screeningService.screeningSchedule(startTime.toString, startTime.toString))
          .thenReturn(Future.successful(Some(Seq())))

        Get("/screenings/" + startTime.toString + "/" + startTime.toString) ~> screeningRoute ~> check {
          responseAs[String] shouldBe emptySequence.asJson.noSpaces
          status.intValue shouldBe 200
        }
      }
      "return 400 if date values are in wrong format" in new Context {
        when(screeningService.screeningSchedule("IllegalDate", startTime.toString))
          .thenReturn(Future.successful(None))

        Get("/screenings/" + "IllegalDate" + "/" + startTime.toString) ~> screeningRoute ~> check {
          responseAs[String] shouldBe "Illegal date values".asJson.noSpaces
          status.intValue shouldBe 400
        }
      }
    }
  }

  trait Context {
    val screeningService: ScreeningService = mock[ScreeningService]
    val screeningRoute: Route              = new ScreeningRoute(screeningService).route

    val emptySequence: Seq[MovieSchedule] = Seq()
    val testMovie1: Movie                 = testMovie(1)
    val testMovie2: Movie                 = testMovie(2)
    val testHall1: Hall                   = testHall(1)
    val testHall2: Hall                   = testHall(2)

    val testScreening1: Screening = testScreening(1, testHall1, testMovie1)
    val testScreening2: Screening = testScreening(2, testHall1, testMovie2)
    val testScreening3: Screening = testScreening(3, testHall2, testMovie1)
    val testScreening4: Screening = testScreening(4, testHall2, testMovie2)

    val startTime = LocalDateTime.now
    val endTime   = LocalDateTime.now.plusDays(100)

    val testScreeningDetails: ScreeningDetails = ScreeningDetails(
      1,
      testScreening1.screeningTime,
      testMovie1,
      testHall1,
      Array.ofDim[Boolean](testHall1.rows, testHall1.columns).map(_.map(!_))
    )

    val screeningHeader1: ScreeningHeader = testScreeningHeader(testScreening1)
    val screeningHeader2: ScreeningHeader = testScreeningHeader(testScreening2)
    val screeningHeader3: ScreeningHeader = testScreeningHeader(testScreening3)
    val screeningHeader4: ScreeningHeader = testScreeningHeader(testScreening4)

    val testMovieSchedule1: MovieSchedule = MovieSchedule(testMovie1, Seq(screeningHeader1, screeningHeader3))
    val testMovieSchedule2: MovieSchedule = MovieSchedule(testMovie2, Seq(screeningHeader2, screeningHeader4))

    private def testMovie(id: Long) = Movie(
      Some(id),
      Random.nextString(7),
      Random.nextString(10),
      LocalTime.ofSecondOfDay(Random.nextInt(86400) + 1)
    )

    private def testScreeningHeader(screening: Screening) = ScreeningHeader(screening.id.get, screening.screeningTime)

    private def testHall(id: Long) = Hall(
      Some(id),
      Random.nextString(10),
      Random.nextInt(20) + 1,
      Random.nextInt(20) + 1
    )

    private def testScreening(id: Long, hall: Hall, movie: Movie) = Screening(
      Some(id),
      hall.id.get,
      movie.id.get,
      LocalDateTime.now().plusDays(Random.nextInt(100) + 1)
    )
  }
}
