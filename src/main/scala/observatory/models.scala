package observatory

case class Location(lat: Double, lon: Double)

case class Color(red: Int, green: Int, blue: Int)

case class LocalTemperature(year: Int,
                            month: Int,
                            day: Int,
                            lat: Double,
                            lon: Double,
                            temp: Double)
