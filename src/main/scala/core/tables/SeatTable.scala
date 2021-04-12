package core.tables

import core.Seat
import utils.DatabaseConnector

private[core] trait SeatTable extends ReservationTable {

  protected val databaseConnector: DatabaseConnector

  import databaseConnector.profile.api._

  protected val seats = TableQuery[Seats]

  class Seats(tag: Tag) extends Table[Seat](tag, "seats") {

    override def * = (id.?, reservationID, row, index, price).<>(Seat.tupled, Seat.unapply)

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def reservationID = column[Long]("reservation_id")

    def row = column[Int]("row_num")

    def index = column[Int]("seat_index")

    def price = column[BigDecimal]("price")

    def reservation = foreignKey("reservation_fk", reservationID, TableQuery[Reservations])(_.id, onDelete=ForeignKeyAction.Cascade)
  }

}
