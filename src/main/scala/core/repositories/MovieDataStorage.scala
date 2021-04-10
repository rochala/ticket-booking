package core.repositories

import core.Movie
import core.tables.MovieDataTable
import utils.DatabaseConnector

import scala.concurrent.{ExecutionContext, Future}

class MovieDataStorage(val databaseConnector: DatabaseConnector)(implicit executionContext: ExecutionContext)
    extends MovieDataTable {

  import databaseConnector._
  import databaseConnector.profile.api._

  def getMovies: Future[Seq[Movie]] = db.run(movies.result)

  def getMovie(id: Long): Future[Option[Movie]] = db.run(movies.filter(_.id === id).result.headOption)

  def saveMovie(movie: Movie): Future[Movie] = db.run(movies.insertOrUpdate(movie)).map(_ => movie)
}
