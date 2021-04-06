package core.movies

import core.Movie
import utils.DatabaseConnector

import scala.concurrent.{ExecutionContext, Future}

sealed trait MovieDataStorage {
  def getMovies: Future[Seq[Movie]]

  def getMovie(id: Long): Future[Option[Movie]]

  def saveMovie(movie: Movie): Future[Movie]

}

class DBMovieDataStorage(val databaseConnector: DatabaseConnector)(implicit executionContext: ExecutionContext)
  extends MovieDataTable
    with MovieDataStorage {

  import databaseConnector._
  import databaseConnector.profile.api._

  def getMovies: Future[Seq[Movie]] = db.run(movies.result)

  def getMovie(id: Long): Future[Option[Movie]] = db.run(movies.filter(_.id === id).result.headOption)

  def saveMovie(movie: Movie): Future[Movie] = db.run(movies.insertOrUpdate(movie)).map(_ => movie)
}
