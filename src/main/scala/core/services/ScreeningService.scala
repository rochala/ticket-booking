package core.services

import java.time.LocalDateTime

import core.repositories.{ScreeningStorage, SeatStorage}
import core.{Hall, Movie, Screening}

import scala.concurrent.{ExecutionContext, Future}

class ScreeningService(screeningStorage: ScreeningStorage, seatStorage: SeatStorage)(implicit
    executionContext: ExecutionContext
) {
  def allScreenings: Future[Seq[Screening]] = screeningStorage.getScreenings

  def getScreening(id: Long): Future[Option[Screening]] = screeningStorage.getScreening(id)

  def screeningSchedule(startDate: LocalDateTime, endDate: LocalDateTime): Future[Seq[MovieSchedule]] =
    screeningStorage
      .getMoviesScreenings(startDate, endDate)
      .map {
        _.groupBy(_._1)
          .map { case (x, y) =>
            MovieSchedule(x, y.map(e => ScreeningHeader(e._2, e._3)))
          }
          .toSeq
          .sortBy(_.movie.title)
      }

  def screeningDetails(id: Long): Future[Option[ScreeningDetails]] = {
    val data = for {
      screeningData <- screeningStorage.getScreeningDetails(id)
      takenSeats    <- seatStorage.takenSeats(id)
    } yield (screeningData, takenSeats)

    data.map { data =>
      if (data._1.isEmpty) None

      val screeningData = data._1.get

      val seats = Array
        .ofDim[Boolean](screeningData._3.rows, screeningData._3.columns)
        .map(_.map(!_))

      data._2.foreach(seat => seats(seat.row)(seat.index) = false)

      Some(
        ScreeningDetails(
          screeningData._1.id.get,
          screeningData._1.screeningTime,
          screeningData._2,
          screeningData._3,
          seats
        )
      )
    }
  }
}

case class ScreeningHeader(id: Long, screeningTime: LocalDateTime)

case class MovieSchedule(movie: Movie, screenings: Seq[ScreeningHeader])

case class ScreeningDetails(
    id: Long,
    screeningTime: LocalDateTime,
    movie: Movie,
    hall: Hall,
    availableSeats: Array[Array[Boolean]]
)
