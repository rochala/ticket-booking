package core.halls

import core.Hall
import utils.DatabaseConnector

private[halls] trait HallDataTable {

  protected val databaseConnector: DatabaseConnector
  import databaseConnector.profile.api._

  class Halls(tag: Tag) extends Table[Hall](tag, "HALLS") {
    def id      = column[Long]("HALL_ID", O.PrimaryKey, O.AutoInc)
    def name    = column[String]("HALL_NAME")
    def rows    = column[Int]("ROWS_NUMBER")
    def columns = column[Int]("COLUMNS_NUMBER")

    override def * = (id.?, name, rows, columns).<>(Hall.tupled, Hall.unapply)
  }

  protected val halls = TableQuery[Halls]
}
