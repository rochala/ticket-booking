package core.screenings

import java.sql.Timestamp

import core.{Hall, Movie, Screening}
import core.seats.SeatStorage

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration.Duration

class ScreeningService(screeningStorage: ScreeningStorage, seatStorage: SeatStorage)(implicit
                                                                                     executionContext: ExecutionContext
) {
  def allScreenings: Future[Seq[Screening]] = screeningStorage.getScreenings

  def getScreening(id: Long): Future[Option[Screening]] = screeningStorage.getScreening(id)

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

  def screeningDetails(id: Long): Future[Option[ScreeningDetails]] = {
    val screeningData = Await.result(screeningStorage.getScreeningDetails(id), Duration.Inf)
    if (screeningData.isEmpty) return Future(None)

    val takenSeats = Await.result(seatStorage.takenSeats(id), Duration.Inf)
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

  case class ScreeningHeader(id: Long, screeningTime: Timestamp)

  case class MovieSchedule(movie: Movie, screenings: Seq[ScreeningHeader])

  case class ScreeningDetails(
                               id: Long,
                               screeningTime: Timestamp,
                               movie: Movie,
                               hall: Hall,
                               availableSeats: Array[Array[Boolean]]
                             )
}
