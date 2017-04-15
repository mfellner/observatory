package observatory

import java.lang.Math._

import com.sksamuel.scrimage.Image

/**
  * 2nd milestone: basic visualization
  */
object Visualization {
  val earthRadiusMeters = 6371000F

  /**
    * @param temperatures Known temperatures: pairs containing a location and the temperature at this location
    * @param location     Location where to predict the temperature
    * @return The predicted temperature at `location`
    */
  def predictTemperature(temperatures: Iterable[(Location, Double)], location: Location): Double = {
    val p = 2

    temperatures.par.toStream.map {
      case (Location(lat, lon), temp) => temp
    }

    0
  }

  private def weight(interpolated: Location, sampled: Location) = {

  }

  // https://en.wikipedia.org/wiki/Great-circle_distance
  def distance(a: Location, b: Location): Double = {
    val dLat = toRadians(abs(a.lat - b.lat))
    val dLon = toRadians(abs(a.lon - b.lon))
    val aLatR = toRadians(a.lat)
    val bLatR = toRadians(b.lat)

    val dividend = sqrt(pow(cos(bLatR) * sin(dLon), 2) +
      pow(cos(aLatR) * sin(bLatR) - sin(aLatR) * cos(bLatR) * cos(dLon), 2))
    val divisor = sin(aLatR) * sin(bLatR) + cos(aLatR) * cos(bLatR) * cos(dLon)
    //    val radians = atan(dividend / divisor)
    val radians = atan2(dividend, divisor)
    6371000 * radians
  }

  /**
    * @param points Pairs containing a value and its associated color
    * @param value  The value to interpolate
    * @return The color that corresponds to `value`, according to the color scale defined by `points`
    */
  def interpolateColor(points: Iterable[(Double, Color)], value: Double): Color = {
    ???
  }

  /**
    * @param temperatures Known temperatures
    * @param colors       Color scale
    * @return A 360×180 image where each pixel shows the predicted temperature at its location
    */
  def visualize(temperatures: Iterable[(Location, Double)],
                colors: Iterable[(Double, Color)]): Image = {
    ???
  }

}

