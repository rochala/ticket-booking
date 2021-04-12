package core.repositories

import core.Hall
import core.tables.HallDataTable
import utils.DatabaseConnector

import scala.concurrent.{ExecutionContext, Future}

class HallDataStorage(val databaseConnector: DatabaseConnector)(implicit executionContext: ExecutionContext)
    extends HallDataTable {

  import databaseConnector._
  import databaseConnector.profile.api._

  def getHalls: Future[Seq[Hall]] = db.run(halls.result)

  def getHall(id: Long): Future[Option[Hall]] = db.run(halls.filter(_.id === id).result.headOption)
}
