package core.screenings

import core.Screening
import utils.DatabaseConnector
import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import java.sql.Timestamp
import java.sql.Date
import scala.concurrent.duration.Duration
import scala.concurrent.Await
import core.Movie
import core.Hall

sealed trait ScreeningStorage {
  def getScreenings(): Future[Seq[Screening]]

  def getScreening(id: Long): Future[Option[Screening]]

  def getMoviesScreenings(startDate: Timestamp, endDate: Timestamp): Future[Seq[(Movie, Long, Timestamp)]]
  // def screeningSchedule(day: Date): Future[Seq[Screening]]

  // def screeningsBetween(startDate: Date, endDate: Date): Future[Seq[Screening]]
}

class H2ScreeningStorage(val databaseConnector: DatabaseConnector)(implicit executionContext: ExecutionContext)
    extends ScreeningTable
    with ScreeningStorage {

  import databaseConnector._
  import databaseConnector.profile.api._

  val joinQuery = for {
    (s, m) <- screenings join movies on (_.movieID === _.id)
  } yield (m, s.id, s.screeningTime)

  def getScreenings(): Future[Seq[Screening]] = db.run(screenings.result)

  def getScreening(id: Long): Future[Option[Screening]] = db.run(screenings.filter(_.id === id).result.headOption)

  def getMoviesScreenings(startDate: Timestamp, endDate: Timestamp): Future[Seq[(Movie, Long, Timestamp)]] =
    db.run(joinQuery.filter(_._3.between(startDate, endDate)).sortBy(_._1.id).result)

  private def init() = db.run(
    DBIO.seq(
      screenings.schema.create,
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
        Screening(None, 3, 8, new Timestamp(1616961600000L))
      )
    )
  )

  // Await.result(populate(), Duration.Inf)
  init()
  db.run(joinQuery.result).map(println)
}
