package http.routes

import akka.http.scaladsl.model.{HttpEntity, MediaTypes}
import akka.http.scaladsl.server.Route
import http.BaseServiceTest
import org.mockito.Mockito._

import scala.concurrent.Future
import scala.util.Random
import io.circe.generic.auto._
import io.circe.syntax._
import core.services.HallDataService
import core.Hall

class HallRouteTest extends BaseServiceTest {
  "HallRoute" when {
    "GET /api/halls" should {
      "return 200 and list of all halls JSON" in new Context {
        when(hallService.getHalls).thenReturn(Future.successful(Seq(testHall1, testHall2)))

        Get("/halls") ~> hallRoute ~> check {
          responseAs[String] shouldBe Seq(testHall1, testHall2).asJson.noSpaces
          status.intValue shouldBe 200
        }
      }
    }

    "GET /api/halls/:id" should {
      "return 200 and hall JSON" in new Context {
        when(hallService.getHall(testHall1.id.get)).thenReturn(Future.successful(Some(testHall1)))

        Get("/halls/" + testHall1.id.get) ~> hallRoute ~> check {
          responseAs[String] shouldBe testHall1.asJson.noSpaces
          status.intValue shouldBe 200
        }
      }
      "return 400 if hall does not exists" in new Context {
        when(hallService.getHall(testHall1.id.get)).thenReturn(Future.successful(None))

        Get("/halls/" + testHall1.id.get) ~> hallRoute ~> check {
          status.intValue shouldBe 400
        }
      }
    }
  }

  trait Context {
    val hallService: HallDataService = mock[HallDataService]
    val hallRoute: Route             = new HallRoute(hallService).route

    val testHall1: Hall = testHall(1)
    val testHall2: Hall = testHall(2)

    private def testHall(id: Long) = Hall(
      Some(id),
      Random.nextString(10),
      Random.nextInt(),
      Random.nextInt()
    )
  }
}
