import slick.driver.H2Driver.api._
import java.sql.Time
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global

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
            Hall(None, "Main hall"),
            Hall(None, "Studio hall"),
            Hall(None, "Small hall")
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
          movies.result.map(println)
          // screenings ++= Seq(
          // ),
          // reservations ++= Seq(
          // ),
          // seats ++= Seq(
          // )
        )
      ),
      Duration.Inf
    )
  } finally db.close
}
