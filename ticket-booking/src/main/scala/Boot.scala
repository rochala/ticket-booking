import slick.driver.H2Driver.api._
import java.sql.Time
import scala.concurrent.Await
import scala.concurrent.duration.Duration
// import scala.concurrent.ExecutionContext.Implicits.global
// import core.definitions._
import core.halls._
import java.sql.Timestamp
import utils._
import core.screenings._
import scala.concurrent.ExecutionContext
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import http.HttpRoute
import scala.util.Success
import scala.util.Failure
import scala.io.StdIn
import akka.stream.ActorMaterializer
import akka.util.Timeout
import scala.concurrent.duration._
import core.movies.MovieDataService
import core.movies.H2MovieDataStorage
import core.reservations.H2ReservationStorage
import core.reservations.ReservationService
import core.seats.H2SeatStorage
import core.seats.SeatService

object Boot extends App {

  def startApplication() = {
    implicit val actorSystem                = ActorSystem()
    implicit val executor: ExecutionContext = actorSystem.dispatcher


    val databaseConnector = new DatabaseConnector()

    val hallDataStorage = new H2HallDataStorage(databaseConnector)
    val hallsService    = new HallDataService(hallDataStorage)

    val movieDataStorage = new H2MovieDataStorage(databaseConnector)
    val movieService = new MovieDataService(movieDataStorage)

    val seatStorage = new H2SeatStorage(databaseConnector)
    val seatService = new SeatService(seatStorage)

    val screeningDataStorage = new H2ScreeningStorage(databaseConnector)
    val screeningService = new ScreeningService(screeningDataStorage, seatStorage)

    val reservationStorage = new H2ReservationStorage(databaseConnector)
    val reservationService = new ReservationService(reservationStorage, seatStorage, screeningDataStorage)


    val httpRoute = new HttpRoute(hallsService, movieService, screeningService, reservationService, seatService)

    val bindingFuture = Http().bindAndHandle(httpRoute.route, Config.httpHost, Config.httpPort)

    printf("Server online at http://%s:%d/\n", Config.httpHost, Config.httpPort)
    println("Press RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind())                     // trigger unbinding from the port
      .onComplete(_ ⇒ actorSystem.terminate()) // and shutdown when done
  }

  startApplication()

  // def minToMilis(minutes: Int) = minutes * 60 * 1000;

  // val db                       = Database.forConfig("h2mem")
  // // db.createSession()

  // try {
  //   val halls        = TableQuery[Halls]
  //   val movies       = TableQuery[Movies]
  //   val screenings   = TableQuery[Screenings]
  //   val reservations = TableQuery[Reservations]
  //   val seats        = TableQuery[Seats]

  //   Await.result(
  //     db.run(
  //       DBIO.seq(
  //         (halls.schema ++ movies.schema ++ screenings.schema ++ reservations.schema ++ seats.schema).create,
  //         halls ++= Seq(
  //           Hall(None, "Main hall", 18, 30),
  //           Hall(None, "Studio hall", 12, 20),
  //           Hall(None, "Small hall", 10, 15)
  //         ),
  //         movies ++= Seq(
  //           Movie(None, "tt1375666", "Inception", minToMilis(148)),
  //           Movie(None, "tt0076759", "Star Wars: Episode IV - A New Hope", minToMilis(121)),
  //           Movie(None, "tt0083658", "Blade Runner", minToMilis(117)),
  //           Movie(None, "tt1856101", "Blade Runner 2049", minToMilis(164)),
  //           Movie(None, "tt0110912", "Pulp Fiction", minToMilis(154)),
  //           Movie(None, "tt0120737", "The Lord of the Rings: The Fellowship of the Ring", minToMilis(178)),
  //           Movie(None, "tt0060196", "The Good, the Bad and the Ugly", minToMilis(178)),
  //           Movie(None, "tt6751668", "Parasite", minToMilis(132)),
  //           Movie(None, "tt3748528", "Rouge One: A Star Wars Story", minToMilis(133))
  //         ),
  //         screenings ++= Seq(
  //           Screening(None, 1, 1, new Timestamp(1616695200000L)),
  //           Screening(None, 1, 1, new Timestamp(1616706000000L)),
  //           Screening(None, 2, 2, new Timestamp(1616695200000L)),
  //           Screening(None, 2, 2, new Timestamp(1616706000000L)),
  //           Screening(None, 3, 3, new Timestamp(1616695200000L)),
  //           Screening(None, 3, 3, new Timestamp(1616706000000L)),
  //           Screening(None, 1, 4, new Timestamp(1616785200000L)),
  //           Screening(None, 1, 4, new Timestamp(1616871600000L)),
  //           Screening(None, 2, 5, new Timestamp(1616774400000L)),
  //           Screening(None, 2, 5, new Timestamp(1616787000000L)),
  //           Screening(None, 3, 6, new Timestamp(1616774400000L)),
  //           Screening(None, 3, 6, new Timestamp(1616787000000L)),
  //           Screening(None, 2, 6, new Timestamp(1616868000000L)),
  //           Screening(None, 3, 6, new Timestamp(1616868000000L)),
  //           Screening(None, 1, 7, new Timestamp(1616936400000L)),
  //           Screening(None, 1, 8, new Timestamp(1616949000000L)),
  //           Screening(None, 1, 9, new Timestamp(1616961600000L)),
  //           Screening(None, 2, 8, new Timestamp(1616936400000L)),
  //           Screening(None, 2, 9, new Timestamp(1616949000000L)),
  //           Screening(None, 2, 7, new Timestamp(1616961600000L)),
  //           Screening(None, 3, 9, new Timestamp(1616936400000L)),
  //           Screening(None, 3, 7, new Timestamp(1616949000000L)),
  //           Screening(None, 3, 8, new Timestamp(1616961600000L)),
  //         ),
  //         // reservations ++= Seq(
  //         // ),
  //         // seats ++= Seq(
  //         // )
  //         screenings.filter(_.screeningTime > new Timestamp(1616868000000L)).result.map(_.map(println)),
  //       )
  //     ),
  //     Duration.Inf
  //   )

  // } finally db.close
}
