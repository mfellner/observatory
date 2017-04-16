package observatory

import java.lang.Math._

import com.sksamuel.scrimage.Image

/**
  * 2nd milestone: basic visualization
  */
object Visualization {
  val earthRadiusMeters = 6371000.0
  val distanceThresholdMeters = 1000.0
  val p = 2.0

  /**
    * @param temperatures Known temperatures: pairs containing a location and the temperature at this location
    * @param location     Location where to predict the temperature
    * @return The predicted temperature at `location`
    */
  def predictTemperature(temperatures: Iterable[(Location, Double)], location: Location): Double = {

    val weights = temperatures.par.toStream.map {
      case (loc, _) =>
        val d = distance(loc, location)
        if (d > distanceThresholdMeters)
          1.0 / pow(d, p)
        else
          1.0
    }

    val sumOfWeights = weights.sum

    val sumOfWeightedTemps = temperatures.par.toStream.zip(weights).map {
      case ((loc, temp), weight) =>
        val d = distance(loc, location)
        if (d > distanceThresholdMeters) {
          weight * temp
        } else
          temp
    }.sum

    sumOfWeightedTemps / sumOfWeights
  }

  // https://en.wikipedia.org/wiki/Great-circle_distance
  def distance(a: Location, b: Location): Double = {
    val dLat = toRadians(abs(a.lat - b.lat))
    val dLon = toRadians(abs(a.lon - b.lon))
    val aLatR = toRadians(a.lat)
    val bLatR = toRadians(b.lat)

    val dividend = sqrt(pow(cos(bLatR) * sin(dLon), 2.0) +
      pow(cos(aLatR) * sin(bLatR) - sin(aLatR) * cos(bLatR) * cos(dLon), 2.0))
    val divisor = sin(aLatR) * sin(bLatR) + cos(aLatR) * cos(bLatR) * cos(dLon)
    //    val radians = atan(dividend / divisor)
    val radians = atan2(dividend, divisor)
    6371000.0 * radians
  }

  /**
    * @param points Pairs containing a value and its associated color
    * @param value  The value to interpolate
    * @return The color that corresponds to `value`, according to the color scale defined by `points`
    */
  def interpolateColor(points: Iterable[(Double, Color)], value: Double): Color = {
    val (hi, lo) = findBounds(points.toArray.sortBy(-_._1), value)

    if (hi == lo)
      hi._2
    else {
      //      val t = Math.abs(value / (hi._1 + lo._1))
      val t = Math.abs((value - lo._1) / (hi._1 - lo._1))
      Color(
        lerpColor(hi._2.red, lo._2.red, t),
        lerpColor(hi._2.green, lo._2.green, t),
        lerpColor(hi._2.blue, lo._2.blue, t))
    }
  }

  def lerpColor(hi: Int, lo: Int, t: Double): Int = {
    if (hi == lo)
      hi
    else
      Math.round(BigDecimal(lo + ((hi - lo) * t)).toFloat)
  }

  def findBounds(points: Iterable[(Double, Color)],
                 value: Double): ((Double, Color), (Double, Color)) = {
    var hi: (Double, Color) = points.head
    var lo: (Double, Color) = hi

    for (p <- points) {
      if (p._1 == value) {
        return (p, p)
      } else if (p._1 < value) {
        lo = p
        return (hi, lo)
      } else {
        hi = p
        lo = p
      }
    }
    (hi, lo)
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

