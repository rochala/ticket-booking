package core.services

import core.Hall
import core.repositories.HallDataStorage

import scala.concurrent.{ExecutionContext, Future}

class HallDataService(hallDataStorage: HallDataStorage)(implicit executionContext: ExecutionContext) {
  def getHalls: Future[Seq[Hall]] = hallDataStorage.getHalls

  def getHall(id: Long): Future[Option[Hall]] = hallDataStorage.getHall(id)
}
