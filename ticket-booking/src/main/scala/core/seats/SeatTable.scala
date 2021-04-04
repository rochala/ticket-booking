package core.seats

import core.Seat
import utils.DatabaseConnector
import core.reservations.ReservationTable

import slick.driver.H2Driver.api._


private[core] trait SeatTable extends ReservationTable {

  protected val databaseConnector: DatabaseConnector
  import databaseConnector.profile.api._

  class Seats(tag: Tag) extends Table[Seat](tag, "seats") {

    def id            = column[Long]("seatid", O.PrimaryKey, O.AutoInc)
    def reservationID = column[Long]("reservationid")
    def row           = column[Int]("row_num")
    def index         = column[Int]("seat_index")
    def price         = column[Double]("price")

    override def * = (id.?, reservationID, row, index, price).<>(Seat.tupled, Seat.unapply)

    def reservation = foreignKey("reservation_fk", reservationID, TableQuery[Reservations])(_.id)
  }

  protected val seats = TableQuery[Seats]
}

