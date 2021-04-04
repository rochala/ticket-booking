package core.halls

import core.Hall
import utils.DatabaseConnector
import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import core.BaseStorage

sealed trait HallDataStorage extends BaseStorage {
  def getHalls(): Future[Seq[Hall]]

  def getHall(id: Long): Future[Option[Hall]]

}

class H2HallDataStorage(val databaseConnector: DatabaseConnector)(implicit executionContext: ExecutionContext)
    extends HallDataStorage {

  import databaseConnector._
  import databaseConnector.profile.api._


  def getHalls(): Future[Seq[Hall]] = db.run(halls.result)

  def getHall(id: Long): Future[Option[Hall]] = db.run(halls.filter(_.id === id).result.headOption)
}
