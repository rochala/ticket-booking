package core.seats

import core.Seat
import utils.DatabaseConnector
import scala.concurrent.Future
import scala.concurrent.ExecutionContext

sealed trait SeatStorage {
  def getSeats(): Future[Seq[Seat]]

  def getSeat(id: Long): Future[Option[Seat]]

  def avaliableSeats(screeningID: Long): Future[Seq[Seat]]

}

class H2SeatStorage(val databaseConnector: DatabaseConnector)(implicit executionContext: ExecutionContext)
    extends SeatTable
    with SeatStorage {

  import databaseConnector._
  import databaseConnector.profile.api._

  val joinQuery = for {
    ((seat, reservation), screening) <- seats join reservations join screenings
  } yield (screening.id, seat)

  def getSeats(): Future[Seq[Seat]] = db.run(seats.result)

  def getSeat(id: Long): Future[Option[Seat]] = db.run(seats.filter(_.id === id).result.headOption)

  def avaliableSeats(screeningID: Long): Future[Seq[Seat]] =
    db.run(joinQuery.filter(_._1 === screeningID).map(_._2).result)

  private def init() = db.run(
    DBIO.seq(
      screenings.schema.create
    )
  )

  db.run(halls.schema.create)
  init()
}
