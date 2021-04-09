import java.sql.{Time, Timestamp}
import java.time.LocalDateTime
import core.Status.Status

package object core {

  final case class Hall(id: Option[Long], name: String, rows: Int, columns: Int)

  final case class Movie(id: Option[Long], imbdID: String, title: String, duration: Time)

  final case class Screening(id: Option[Long], hallID: Long, movieID: Long, screeningTime: LocalDateTime)

  final case class Reservation(
      id: Option[Long],
      screeningID: Long,
      name: String,
      surname: String,
      reservationTime: LocalDateTime,
      status: Status
  )

  final case class Seat(id: Option[Long], reservationID: Long, row: Int, index: Int, price: BigDecimal)

}
