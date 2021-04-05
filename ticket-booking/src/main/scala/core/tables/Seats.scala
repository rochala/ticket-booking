package core.tables

import core.Seat
import utils.DatabaseConnector
import slick.driver.PostgresDriver.api._

class Seats(tag: Tag) extends Table[Seat](tag, "seats") {

  def id            = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def reservationID = column[Long]("reservationid")
  def row           = column[Int]("row_num")
  def index         = column[Int]("seat_index")
  def price         = column[Double]("price")

  override def * = (id.?, reservationID, row, index, price).<>(Seat.tupled, Seat.unapply)

  def reservation = foreignKey("reservation_fk", reservationID, TableQuery[Reservations])(_.id)
}
