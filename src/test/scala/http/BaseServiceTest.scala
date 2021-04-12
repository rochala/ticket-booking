package http

import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest._
import org.scalatestplus.mockito.MockitoSugar

import matchers.should._
import wordspec._
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import org.scalatest.matchers._

trait BaseServiceTest extends AnyWordSpec with Matchers with ScalatestRouteTest with MockitoSugar {}
