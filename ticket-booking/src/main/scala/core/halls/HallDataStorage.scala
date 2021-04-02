package core.halls

import core.Hall
import utils.DatabaseConnector
import scala.concurrent.Future
import scala.concurrent.ExecutionContext

sealed trait HallDataStorage {
  def getHalls(): Future[Seq[Hall]]

  def getHall(id: Long): Future[Option[Hall]]

}

class H2HallDataStorage(val databaseConnector: DatabaseConnector)(implicit executionContext: ExecutionContext)
    extends HallDataTable
    with HallDataStorage {

  import databaseConnector._
  import databaseConnector.profile.api._


  def getHalls(): Future[Seq[Hall]] = db.run(halls.result)

  def getHall(id: Long): Future[Option[Hall]] = db.run(halls.filter(_.id === id).result.headOption)

  private def populate() = db.run(halls ++= Seq(
            Hall(None, "Main hall", 18, 30),
            Hall(None, "Studio hall", 12, 20),
            Hall(None, "Small hall", 10, 15)
          )
  )

  db.run(halls.schema.create)
  populate()
}
