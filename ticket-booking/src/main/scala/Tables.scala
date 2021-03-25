import slick.driver.H2Driver.api._
import slick.lifted.ProvenShape
import java.sql.Timestamp
import slick.sql.SqlProfile.ColumnOption.SqlType
import java.sql.Time

case class Hall(id: Option[Long], name: String,rows: Int, columns: Int)

class Halls(tag: Tag) extends Table[Hall](tag, "HALLS") {
  def id         = column[Long]("HALL_ID", O.PrimaryKey, O.AutoInc)
  def name       = column[String]("HALL_NAME")
  def rows = column[Int]("ROWS_NUMBER")
  def columns = column[Int]("COLUMNS_NUMBER")
  override def * = (id.?, name, rows, columns).<>(Hall.tupled, Hall.unapply)
}

case class Movie(id: Option[Long], imbdID: String, title: String, duration: Long)

class Movies(tag: Tag) extends Table[Movie](tag, "MOVIES") {
  def id         = column[Long]("MOVIE_ID", O.PrimaryKey, O.AutoInc)
  def imbdID     = column[String]("IMBD_ID")
  def title      = column[String]("TITLE")
  def duration   = column[Long]("DURATION")
  override def * = (id.?, imbdID, title, duration).<>(Movie.tupled, Movie.unapply)
}

case class Screening(id: Option[Long], hallID: Long, movieID: Long, screeningTime: Timestamp)

class Screenings(tag: Tag) extends Table[Screening](tag, "SCREENINGS") {
  def id      = column[Long]("SCREENING_ID", O.PrimaryKey, O.AutoInc)
  def hallID  = column[Long]("HALL_ID")
  def movieID = column[Long]("MOVIE_ID")
  def screeningTime = column[Timestamp](
    "SCREENING_TIME",
    SqlType("TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
  )
  override def * =
    (id.?, hallID, movieID, screeningTime).<>(Screening.tupled, Screening.unapply)
  def hall  = foreignKey("HALL_FK", hallID, TableQuery[Halls])(_.id)
  def movie = foreignKey("MOVIE_FK", movieID, TableQuery[Movies])(_.id)
}

case class Reservation(
    id: Option[Long],
    screeningID: Long,
    name: String,
    surname: String,
    reservationTime: Timestamp,
    status: Boolean
)

class Reservations(tag: Tag) extends Table[Reservation](tag, "RESERVATIONS") {
  def id          = column[Long]("RESERVATION_ID", O.PrimaryKey, O.AutoInc)
  def screeningID = column[Long]("SCREENING_ID")
  def name        = column[String]("NAME")
  def surname     = column[String]("SURNAME")
  def status = column[Boolean]("PAID")
  def reservatonTime = column[Timestamp](
    "RESERVATION_TIME",
    SqlType("TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
  )
  override def * =
    (id.?, screeningID, name, surname, reservatonTime, status).<>(Reservation.tupled, Reservation.unapply)
  def screening = foreignKey("SCREENING_FK", screeningID, TableQuery[Screenings])(_.id)
}

case class Seat(id: Option[Long], reservationID: Long, row: Int, index: Int, price: Double)

class Seats(tag: Tag) extends Table[Seat](tag, "SEATS") {

  def id            = column[Long]("SEAT_ID", O.PrimaryKey, O.AutoInc)
  def reservationID = column[Long]("RESERVATION_ID")
  def row           = column[Int]("ROW")
  def index         = column[Int]("INDEX")
  def price         = column[Double]("PRICE")
  override def *    = (id.?, reservationID, row, index, price).<>(Seat.tupled, Seat.unapply)
  def reservation   = foreignKey("RESERVATION_FK", reservationID, TableQuery[Reservations])(_.id)
}
