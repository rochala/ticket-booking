package core.repositories

import java.time.LocalDateTime

import core.tables.ScreeningTable
import core.{Hall, Movie, Screening}
import utils.DatabaseConnector

import scala.concurrent.{ExecutionContext, Future}

class ScreeningStorage(val databaseConnector: DatabaseConnector)(implicit executionContext: ExecutionContext)
    extends ScreeningTable {

  import databaseConnector._
  import databaseConnector.profile.api._

  val screeningMovieJoinQuery = for {
    (screening, movie) <- screenings join movies on (_.movieID === _.id)
  } yield (movie, screening.id, screening.screeningTime)

  val fullJoinQuery = for {
    ((screening, movie), hall) <- screenings join movies on (_.movieID === _.id) join halls on (_._1.hallID === _.id)
  } yield (screening, movie, hall)

  def getScreenings: Future[Seq[Screening]] = db.run(screenings.result)

  def getScreening(id: Long): Future[Option[Screening]] = db.run(screenings.filter(_.id === id).result.headOption)

  def getMoviesScreenings(startDate: LocalDateTime, endDate: LocalDateTime): Future[Seq[(Movie, Long, LocalDateTime)]] =
    db.run(screeningMovieJoinQuery.filter(_._3.between(startDate, endDate)).sortBy(_._3).result)

  def getScreeningDetails(id: Long): Future[Option[(Screening, Movie, Hall)]] =
    db.run(fullJoinQuery.filter(_._1.id === id).result.headOption)

}
