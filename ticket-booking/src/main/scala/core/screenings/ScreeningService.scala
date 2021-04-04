package core.screenings

import core.Screening
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import java.sql.Date
import java.sql.Timestamp
import scala.concurrent.duration.Duration
import scala.concurrent.Await
import core.Movie

class ScreeningService(screeningStorage: ScreeningStorage)(implicit executionContext: ExecutionContext) {
  def allScreenings(): Future[Seq[Screening]] = screeningStorage.getScreenings()

  def getScreening(id: Long): Future[Option[Screening]] = screeningStorage.getScreening(id)


  case class ScreeningHeader(id: Long, screeningTime: Timestamp)
  case class MovieSchedule(movie: Movie, screenings: Seq[ScreeningHeader])
  def screeningSchedule(startDate: Timestamp, endDate: Timestamp): Seq[MovieSchedule] = {
    Await.result(screeningStorage
      .getMoviesScreenings(startDate, endDate)
      , Duration.Inf)
    .groupBy(_._1)
    .map{
      case (x,y) =>
        MovieSchedule(x, y.map(e => ScreeningHeader(e._2, e._3)))
    }.toSeq.sortBy(_.movie.title)
  }

  def screeningDetails(id: Long) = {
  }



  // def screeningsBetween(startDate: Date, endDate: Date): Future[Seq[Screening]]
}
