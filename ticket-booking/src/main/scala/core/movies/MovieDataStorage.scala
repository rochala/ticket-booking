package core.movies

import core.Movie
import utils.DatabaseConnector
import scala.concurrent.Future
import scala.concurrent.ExecutionContext

sealed trait MovieDataStorage {
  def getMovies(): Future[Seq[Movie]]

  def getMovie(id: Long): Future[Option[Movie]]

  def saveMovie(movie: Movie): Future[Movie]

}

class H2MovieDataStorage(val databaseConnector: DatabaseConnector)(implicit executionContext: ExecutionContext)
    extends MovieDataTable
    with MovieDataStorage {

  import databaseConnector._
  import databaseConnector.profile.api._

  def getMovies(): Future[Seq[Movie]] = db.run(movies.result)

  def getMovie(id: Long): Future[Option[Movie]] = db.run(movies.filter(_.id === id).result.headOption)

  def saveMovie(movie: Movie): Future[Movie] = db.run(movies.insertOrUpdate(movie)).map(_ => movie)


  private def populate() = {
    def minToMilis(minutes: Int) = minutes * 60 * 1000

    db.run(
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
      )
    )
  }

  db.run(movies.schema.create)
  populate()
}
