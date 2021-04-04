package core.halls

import core.Hall
import utils.DatabaseConnector
import slick.driver.PostgresDriver.api._


class HallDataTable(tag: Tag) extends Table[Hall](tag, "halls") {
  def id      = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def name    = column[String]("name")
  def rows    = column[Int]("row_num")
  def columns = column[Int]("column_num")

  override def * = (id.?, name, rows, columns).<>(Hall.tupled, Hall.unapply)
}

