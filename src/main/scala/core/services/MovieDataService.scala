package core.services

import core.Movie
import core.repositories.MovieDataStorage

import scala.concurrent.{ExecutionContext, Future}

class MovieDataService(movieDataStorage: MovieDataStorage)(implicit executionContext: ExecutionContext) {
  def getMovies: Future[Seq[Movie]] = movieDataStorage.getMovies

  def getMovie(id: Long): Future[Option[Movie]] = movieDataStorage.getMovie(id)

  def addMovie(movie: Movie): Future[Movie] = movieDataStorage.saveMovie(movie)
}
