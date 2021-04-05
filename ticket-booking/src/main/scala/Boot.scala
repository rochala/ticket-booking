
// import scala.concurrent.ExecutionContext.Implicits.global
// import core.definitions._
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import core.halls._
import core.movies.{H2MovieDataStorage, MovieDataService}
import core.reservations.{H2ReservationStorage, ReservationService}
import core.screenings._
import core.seats.{H2SeatStorage, SeatService}
import http.HttpRoute
import utils._

import scala.concurrent.ExecutionContext
import scala.io.StdIn

object Boot extends App {

  def startApplication(): Unit = {
    implicit val actorSystem: ActorSystem = ActorSystem()
    implicit val executor: ExecutionContext = actorSystem.dispatcher


    val databaseConnector = new DatabaseConnector()

    val hallDataStorage = new H2HallDataStorage(databaseConnector)
    val hallsService = new HallDataService(hallDataStorage)

    val movieDataStorage = new H2MovieDataStorage(databaseConnector)
    val movieService = new MovieDataService(movieDataStorage)

    val seatStorage = new H2SeatStorage(databaseConnector)
    val seatService = new SeatService(seatStorage)

    val screeningDataStorage = new H2ScreeningStorage(databaseConnector)
    val screeningService = new ScreeningService(screeningDataStorage, seatStorage)

    val reservationStorage = new H2ReservationStorage(databaseConnector)
    val reservationService = new ReservationService(reservationStorage, seatStorage, screeningDataStorage)


    val httpRoute = new HttpRoute(hallsService, movieService, screeningService, reservationService, seatService)

    val bindingFuture = Http().newServerAt(Config.httpHost, Config.httpPort).bind(httpRoute.route)

    printf("Server online at http://%s:%d/\n", Config.httpHost, Config.httpPort)
    println("Press RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ ⇒ actorSystem.terminate()) // and shutdown when done
  }

  startApplication()
}
