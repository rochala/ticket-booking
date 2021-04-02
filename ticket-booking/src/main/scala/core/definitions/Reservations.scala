// package core.definitions

// import java.sql.Timestamp
// import core.Reservation
// import slick.driver.H2Driver.api._
// import slick.sql.SqlProfile.ColumnOption.SqlType

// class Reservations(tag: Tag) extends Table[Reservation](tag, "RESERVATIONS") {
//   def id          = column[Long]("RESERVATION_ID", O.PrimaryKey, O.AutoInc)
//   def screeningID = column[Long]("SCREENING_ID")
//   def name        = column[String]("NAME")
//   def surname     = column[String]("SURNAME")
//   def status = column[Boolean]("PAID")
//   def reservatonTime = column[Timestamp](
//     "RESERVATION_TIME",
//     SqlType("TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
//   )
//   override def * =
//     (id.?, screeningID, name, surname, reservatonTime, status).<>(Reservation.tupled, Reservation.unapply)
//   def screening = foreignKey("SCREENING_FK", screeningID, TableQuery[Screenings])(_.id)
// }

