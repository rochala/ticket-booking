package core.movies

import core.Movie
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

class MovieDataService(movieDataStorage: MovieDataStorage)(implicit executionContext: ExecutionContext) {
  def getMovies(): Future[Seq[Movie]] = movieDataStorage.getMovies()

  def getMovie(id: Long): Future[Option[Movie]] = movieDataStorage.getMovie(id)

  def addMovie(movie: Movie): Future[Movie] = movieDataStorage.saveMovie(movie)
}
