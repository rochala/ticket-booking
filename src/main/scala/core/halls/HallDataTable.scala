package core.halls

import core.Hall
import utils.DatabaseConnector

private[core] trait HallDataTable {

  protected val databaseConnector: DatabaseConnector

  import databaseConnector.profile.api._

  protected val halls = TableQuery[Halls]

  class Halls(tag: Tag) extends Table[Hall](tag, "halls") {
    override def * = (id.?, name, rows, columns).<>(Hall.tupled, Hall.unapply)

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")

    def rows = column[Int]("row_num")

    def columns = column[Int]("column_num")
  }

}
