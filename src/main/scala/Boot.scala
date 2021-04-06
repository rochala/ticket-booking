
// import scala.concurrent.ExecutionContext.Implicits.global
// import core.definitions._

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import core.halls._
import core.movies.{DBMovieDataStorage, MovieDataService}
import core.reservations.{DBReservationStorage, ReservationService}
import core.screenings._
import core.seats.{DBSeatStorage, SeatService}
import http.HttpRoute
import utils._

import scala.concurrent.ExecutionContext
import scala.io.StdIn

object Boot extends App {

  def startApplication(): Unit = {
    implicit val actorSystem: ActorSystem = ActorSystem()
    implicit val executor: ExecutionContext = actorSystem.dispatcher


    val databaseConnector = new DatabaseConnector()

    val hallDataStorage = new DBHallDataStorage(databaseConnector)
    val hallsService = new HallDataService(hallDataStorage)

    val movieDataStorage = new DBMovieDataStorage(databaseConnector)
    val movieService = new MovieDataService(movieDataStorage)

    val seatStorage = new DBSeatStorage(databaseConnector)
    val seatService = new SeatService(seatStorage)

    val screeningDataStorage = new DBScreeningStorage(databaseConnector)
    val screeningService = new ScreeningService(screeningDataStorage, seatStorage)

    val reservationStorage = new DBReservationStorage(databaseConnector)
    val reservationService = new ReservationService(reservationStorage, seatStorage, screeningDataStorage)


    val httpRoute = new HttpRoute(hallsService, movieService, screeningService, reservationService, seatService)

    val bindingFuture = Http().newServerAt(Config.httpHost, Config.httpPort).bind(httpRoute.route)

    printf("Server online at http://%s:%d/\n", Config.httpHost, Config.httpPort)
    println("Press Enter to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => actorSystem.terminate()) // and shutdown when done
  }

  startApplication()
}
