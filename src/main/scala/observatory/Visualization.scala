package observatory

import java.awt.image.BufferedImage
import java.lang.Math._

import com.sksamuel.scrimage.{Image, Pixel}

/**
  * 2nd milestone: basic visualization
  */
object Visualization {
  val earthRadiusMeters = 6371000.0
  val distanceThresholdMeters = 1000.0
  val p = 2.5
  var temperatureMap: Map[Location, Double] = Map.empty

  /**
    * @param temperatures Known temperatures: pairs containing a location and the temperature at this location
    * @param location     Location where to predict the temperature
    * @return The predicted temperature at `location`
    */
  def predictTemperature(temperatures: Iterable[(Location, Double)], location: Location): Double = {

    val weights = temperatures.toStream.par.map {
      case (loc, _) =>
        val d = distanceSimple(loc, location)
        if (d > distanceThresholdMeters)
          1.0 / pow(d, p)
        else
          1.0
    }

    val sumOfWeights = weights.sum

    val sumOfWeightedTemps = temperatures.toStream.par.zip(weights).map {
      case ((loc, temp), weight) =>
        val d = distanceSimple(loc, location)
        if (d > distanceThresholdMeters) {
          weight * temp
        } else
          temp
    }.sum

    sumOfWeightedTemps / sumOfWeights
  }

  // https://en.wikipedia.org/wiki/Great-circle_distance
  def distance(a: Location, b: Location): Double = {
    //    val dLat = toRadians(abs(a.lat - b.lat))
    val dLon = toRadians(abs(a.lon - b.lon))
    val aLatR = toRadians(a.lat)
    val bLatR = toRadians(b.lat)

    val dividend = sqrt(pow(cos(bLatR) * sin(dLon), 2.0) +
      pow(cos(aLatR) * sin(bLatR) - sin(aLatR) * cos(bLatR) * cos(dLon), 2.0))
    val divisor = sin(aLatR) * sin(bLatR) + cos(aLatR) * cos(bLatR) * cos(dLon)
    val radians = atan2(dividend, divisor)
    earthRadiusMeters * radians
  }

  def distanceSimple(a: Location, b: Location): Double = {
    val dLon = toRadians(abs(a.lon - b.lon))
    val aLatR = toRadians(a.lat)
    val bLatR = toRadians(b.lat)

    val radians = acos(sin(aLatR) * sin(bLatR) + cos(aLatR) * cos(bLatR) * cos(dLon))
    earthRadiusMeters * radians
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
      (lo * (1.0 - t) + hi * t + 0.5).toInt
    //      Math.round(BigDecimal(lo + ((hi - lo) * t)).toFloat)
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

    val imgType = BufferedImage.TYPE_INT_RGB
    val width = 360
    val height = 180

    val pixels = Stream.range(0, width * height).par
      .map(arrayIndexToLocation(_, width))
      .map(predictTemperature(temperatures, _))
      .map(interpolateColor(colors, _))
      .map(color => Pixel(color.red, color.green, color.blue, 255))
      .toArray

    Image(width, height, pixels, imgType)
  }

  def arrayIndexToLocation(i: Int, rowWidth: Int): Location = {
    val rowIndex = i / rowWidth
    val colIndex = i - (rowIndex * rowWidth)
    val lat = if (rowIndex <= 90) 90 - rowIndex else (rowIndex - 90) * -1
    val lon = if (colIndex <= 180) colIndex - 180 else colIndex - 180
    Location(lat, lon)
  }
}
