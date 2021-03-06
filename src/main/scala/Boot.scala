// import scala.concurrent.ExecutionContext.Implicits.global
// import core.definitions._

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import core.repositories.{HallDataStorage, MovieDataStorage, ReservationStorage, ScreeningStorage, SeatStorage}
import http.HttpRoute
import utils._

import scala.concurrent.ExecutionContext
import scala.io.StdIn
import Config._
import core.services.{HallDataService, MovieDataService, ReservationService, ScreeningService, SeatService}

object Boot extends App {

  def startApplication(): Unit = {
    implicit val actorSystem: ActorSystem   = ActorSystem()
    implicit val executor: ExecutionContext = actorSystem.dispatcher

    val databaseConnector = new DatabaseConnector()

    val hallDataStorage = new HallDataStorage(databaseConnector)
    val hallsService    = new HallDataService(hallDataStorage)

    val movieDataStorage = new MovieDataStorage(databaseConnector)
    val movieService     = new MovieDataService(movieDataStorage)

    val seatStorage = new SeatStorage(databaseConnector)
    val seatService = new SeatService(seatStorage)

    val screeningDataStorage = new ScreeningStorage(databaseConnector)
    val screeningService     = new ScreeningService(screeningDataStorage, seatStorage)

    val reservationStorage = new ReservationStorage(databaseConnector)
    val reservationService = new ReservationService(reservationStorage, seatStorage, screeningDataStorage)

    val httpRoute = new HttpRoute(hallsService, movieService, screeningService, reservationService, seatService)

    val bindingFuture = Http().newServerAt(httpHost, httpPort).bind(httpRoute.route)

    println(s"Server online at http://$httpHost:$httpPort/")
    println("Press Enter to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind())                      // trigger unbinding from the port
      .onComplete(_ => actorSystem.terminate()) // and shutdown when done
  }

  startApplication()
}
