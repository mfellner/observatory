package observatory

case class Location(lat: Double, lon: Double)

case class Color(red: Int, green: Int, blue: Int)

case class Station(stn: Option[String] = None,
                   wban: Option[String] = None,
                   lat: Option[Double] = None,
                   lon: Option[Double] = None)

case class LocalTemperature(year: Int,
                            month: Int,
                            day: Int,
                            lat: Double,
                            lon: Double,
                            temp: Double)
