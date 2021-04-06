package core.screenings

import java.sql.Timestamp

import core.{Hall, Movie, Screening}
import utils.DatabaseConnector

import scala.concurrent.{ExecutionContext, Future}

sealed trait ScreeningStorage {
  def getScreenings: Future[Seq[Screening]]

  def getScreening(id: Long): Future[Option[Screening]]

  def getMoviesScreenings(startDate: Timestamp, endDate: Timestamp): Future[Seq[(Movie, Long, Timestamp)]]

  def getScreeningDetails(id: Long): Future[Option[(Screening, Movie, Hall)]]
}

class DBScreeningStorage(val databaseConnector: DatabaseConnector)(implicit executionContext: ExecutionContext)
  extends ScreeningTable
    with ScreeningStorage {

  import databaseConnector._
  import databaseConnector.profile.api._

  val joinQuery = for {
    (s, m) <- screenings join movies on (_.movieID === _.id)
  } yield (m, s.id, s.screeningTime)

  val fullJoinQuery = for {
    ((s, m), h) <- screenings join movies join halls
  } yield (s, m, h)

  def getScreenings: Future[Seq[Screening]] = db.run(screenings.result)

  def getScreening(id: Long): Future[Option[Screening]] = db.run(screenings.filter(_.id === id).result.headOption)

  def getMoviesScreenings(startDate: Timestamp, endDate: Timestamp): Future[Seq[(Movie, Long, Timestamp)]] =
    db.run(joinQuery.filter(_._3.between(startDate, endDate)).sortBy(_._3).result)

  def getScreeningDetails(id: Long): Future[Option[(Screening, Movie, Hall)]] =
    db.run(fullJoinQuery.filter(_._1.id === id).result.headOption)

}
