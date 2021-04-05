import java.sql.{Time, Timestamp}

package object core {

  final case class Hall(id: Option[Long], name: String, rows: Int, columns: Int)

  final case class Movie(id: Option[Long], imbdID: String, title: String, duration: Time)

  final case class Screening(id: Option[Long], hallID: Long, movieID: Long, screeningTime: Timestamp)

  final case class Reservation(
                                id: Option[Long],
                                screeningID: Long,
                                name: String,
                                surname: String,
                                reservationTime: Timestamp,
                                status: String
                              )

  final case class Seat(id: Option[Long], reservationID: Long, row: Int, index: Int, price: Double)

}
