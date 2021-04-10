package utils

import com.typesafe.config.ConfigFactory

object Config {
  private val config       = ConfigFactory.load()
  private val httpConfig   = config.getConfig("http")
  private val cinemaConfig = config.getConfig("cinema")

  val httpHost: String = httpConfig.getString("host")
  val httpPort: Int    = httpConfig.getInt("port")

  val reservationAdvanceMinutes: Int = cinemaConfig.getInt("reservationAdvanceMinutes")
  val reservationDays: Int           = cinemaConfig.getInt("reservationDays")
  val childPrice: Double             = cinemaConfig.getDouble("prices.child")
  val studentPrice: Double           = cinemaConfig.getDouble("prices.student")
  val adultPrice: Double             = cinemaConfig.getDouble("prices.adult")
}
