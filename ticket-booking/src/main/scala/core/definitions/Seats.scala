package core.definitions

import core.{Seat, Reservation}
import slick.driver.H2Driver.api._


class Seats(tag: Tag) extends Table[Seat](tag, "SEATS") {

  def id            = column[Long]("SEAT_ID", O.PrimaryKey, O.AutoInc)
  def reservationID = column[Long]("RESERVATION_ID")
  def row           = column[Int]("ROW")
  def index         = column[Int]("INDEX")
  def price         = column[Double]("PRICE")

  override def *    = (id.?, reservationID, row, index, price).<>(Seat.tupled, Seat.unapply)

  def reservation   = foreignKey("RESERVATION_FK", reservationID, TableQuery[Reservations])(_.id)
}

