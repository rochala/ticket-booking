package core.screenings

import core.Screening
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import java.sql.Date
import java.sql.Timestamp
import scala.concurrent.duration.Duration
import scala.concurrent.Await
import core.Movie
import core.seats.SeatStorage
import core.Hall

class ScreeningService(screeningStorage: ScreeningStorage, seatStorage: SeatStorage)(implicit
    executionContext: ExecutionContext
) {
  def allScreenings(): Future[Seq[Screening]] = screeningStorage.getScreenings()

  def getScreening(id: Long): Future[Option[Screening]] = screeningStorage.getScreening(id)

  case class ScreeningHeader(id: Long, screeningTime: Timestamp)
  case class MovieSchedule(movie: Movie, screenings: Seq[ScreeningHeader])
  def screeningSchedule(startDate: Timestamp, endDate: Timestamp): Seq[MovieSchedule] = {
    Await
      .result(
        screeningStorage
          .getMoviesScreenings(startDate, endDate),
        Duration.Inf
      )
      .groupBy(_._1)
      .map { case (x, y) =>
        MovieSchedule(x, y.map(e => ScreeningHeader(e._2, e._3)))
      }
      .toSeq
      .sortBy(_.movie.title)
  }

  case class ScreeningDetails(
      id: Long,
      screeningTime: Timestamp,
      movie: Movie,
      hall: Hall,
      avaliableSeats: Array[Array[Boolean]]
  )
  def screeningDetails(id: Long): Future[Option[ScreeningDetails]] = {
    val screeningData = Await.result(screeningStorage.getScreeningDetails(id), Duration.Inf)
    if (screeningData == None) return Future(None)

    val takenSeats     = Await.result(seatStorage.takenSeats(id), Duration.Inf)
    val avaliableSeats = Array.ofDim[Boolean](screeningData.get._3.rows, screeningData.get._3.columns).map(_.map(!_))

    takenSeats.foreach(seat => avaliableSeats(seat.row)(seat.index) = false)

    Future(
      Some(
        ScreeningDetails(
          screeningData.get._1.id.get,
          screeningData.get._1.screeningTime,
          screeningData.get._2,
          screeningData.get._3,
          avaliableSeats
        )
      )
    )
  }
}
