import slick.driver.H2Driver.api._
import java.sql.Time
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global
import java.sql.Timestamp

object Main extends App {
  def minToMilis(minutes: Int) = minutes * 60 * 1000;
  val db                       = Database.forConfig("h2mem")
  try {
    val halls        = TableQuery[Halls]
    val movies       = TableQuery[Movies]
    val screenings   = TableQuery[Screenings]
    val reservations = TableQuery[Reservations]
    val seats        = TableQuery[Seats]

    Await.result(
      db.run(
        DBIO.seq(
          (halls.schema ++ movies.schema ++ screenings.schema ++ reservations.schema ++ seats.schema).create,
          halls ++= Seq(
            Hall(None, "Main hall", 18, 30),
            Hall(None, "Studio hall", 12, 20),
            Hall(None, "Small hall", 10, 15)
          ),
          movies ++= Seq(
            Movie(None, "tt1375666", "Inception", minToMilis(148)),
            Movie(None, "tt0076759", "Star Wars: Episode IV - A New Hope", minToMilis(121)),
            Movie(None, "tt0083658", "Blade Runner", minToMilis(117)),
            Movie(None, "tt1856101", "Blade Runner 2049", minToMilis(164)),
            Movie(None, "tt0110912", "Pulp Fiction", minToMilis(154)),
            Movie(None, "tt0120737", "The Lord of the Rings: The Fellowship of the Ring", minToMilis(178)),
            Movie(None, "tt0060196", "The Good, the Bad and the Ugly", minToMilis(178)),
            Movie(None, "tt6751668", "Parasite", minToMilis(132)),
            Movie(None, "tt3748528", "Rouge One: A Star Wars Story", minToMilis(133))
          ),
          screenings ++= Seq(
            Screening(None, 1, 1, new Timestamp(1616695200000L)),
            Screening(None, 1, 1, new Timestamp(1616706000000L)),
            Screening(None, 2, 2, new Timestamp(1616695200000L)),
            Screening(None, 2, 2, new Timestamp(1616706000000L)),
            Screening(None, 3, 3, new Timestamp(1616695200000L)),
            Screening(None, 3, 3, new Timestamp(1616706000000L)),
            Screening(None, 1, 4, new Timestamp(1616785200000L)),
            Screening(None, 1, 4, new Timestamp(1616871600000L)),
            Screening(None, 2, 5, new Timestamp(1616774400000L)),
            Screening(None, 2, 5, new Timestamp(1616787000000L)),
            Screening(None, 3, 6, new Timestamp(1616774400000L)),
            Screening(None, 3, 6, new Timestamp(1616787000000L)),
            Screening(None, 2, 6, new Timestamp(1616868000000L)),
            Screening(None, 3, 6, new Timestamp(1616868000000L)),
            Screening(None, 1, 7, new Timestamp(1616936400000L)),
            Screening(None, 1, 8, new Timestamp(1616949000000L)),
            Screening(None, 1, 9, new Timestamp(1616961600000L)),
            Screening(None, 2, 8, new Timestamp(1616936400000L)),
            Screening(None, 2, 9, new Timestamp(1616949000000L)),
            Screening(None, 2, 7, new Timestamp(1616961600000L)),
            Screening(None, 3, 9, new Timestamp(1616936400000L)),
            Screening(None, 3, 7, new Timestamp(1616949000000L)),
            Screening(None, 3, 8, new Timestamp(1616961600000L)),
          ),
          // reservations ++= Seq(
          // ),
          // seats ++= Seq(
          // )
          // screenings.filter(_.screeningTime > new Timestamp(1616868000000L)).result.map(_.map(println)),
        )
      ),
      Duration.Inf
    )

  } finally db.close
}
