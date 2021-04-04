package core

import slick.lifted.TableQuery
import core.movies.MovieDataTable
import core.halls.HallDataTable

protected trait BaseStorage {
  // protected lazy val movies = TableQuery[Movies]
  protected lazy val halls = TableQuery[HallDataTable]
}
