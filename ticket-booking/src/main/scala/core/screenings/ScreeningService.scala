package core.screenings

import core.Screening
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import java.sql.Date
import java.sql.Timestamp
import core.Hall
import core.Movie

class ScreeningService(screeningStorage: ScreeningStorage)(implicit executionContext: ExecutionContext) {
  def allScreenings(): Future[Seq[(Long, Timestamp, Movie, Hall)]] = screeningStorage.getScreenings()

  def getScreening(id: Long): Future[Option[Screening]] = screeningStorage.getScreening(id)

  // def screeningSchedule(day: Date): Future[Seq[Screening]] =
  //   screeningStorage
  //     .getScreenings()
  //     .map(_.filter(_.screeningTime == 0L))



  // def screeningsBetween(startDate: Date, endDate: Date): Future[Seq[Screening]]
}
