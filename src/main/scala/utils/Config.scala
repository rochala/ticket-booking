package utils

import com.typesafe.config.ConfigFactory

object Config {
  private val config       = ConfigFactory.load()
  private val httpConfig   = config.getConfig("http")
  private val cinemaConfig = config.getConfig("cinema")

  val httpHost: String = httpConfig.getString("host")
  val httpPort: Int    = httpConfig.getInt("port")

  val reservationAdvanceMinutes = cinemaConfig.getInt("reservationAdvanceMinutes")
  val reservationDays           = cinemaConfig.getInt("reservationDays")
  val childPrice                = cinemaConfig.getDouble("prices.child")
  val studentPrice              = cinemaConfig.getDouble("prices.student")
  val adultPrice                = cinemaConfig.getDouble("prices.adult")
}
