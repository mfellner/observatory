package observatory

import java.lang.Math.pow

import observatory.Visualization.{distanceSimple}

import scala.collection.{GenIterable, GenMap}

/**
  * 4th milestone: value-added information
  */
object Manipulation {

  class TemperatureGrid(temperatures: Iterable[(Location, Double)]) {

    private val distanceThresholdMeters = 1000.0
    private val p = 2.5

    private lazy val weightMap: GenMap[Location, GenIterable[Double]] = {

      def getWeights(location: Location) = temperatures.par.map {
        case (loc, _) =>
          val d = distanceSimple(loc, location)
          if (d > distanceThresholdMeters)
            1.0 / pow(d, p)
          else
            1.0
      }

      val locations = for {
        lat <- -89 to 90
        lon <- -180 to 179
      } yield Location(lat, lon)

      locations.par.map(loc => (loc, getWeights(loc))).toMap
    }

    def getAverageTemperature(lat: Int, lon: Int): Double = {
      val location = Location(lat, lon)
      val weights = weightMap(location)

      val sumOfWeightedTemps = temperatures.par.zip(weights).map {
        case ((loc, temp), weight) =>
          val d = distanceSimple(loc, location)
          if (d > distanceThresholdMeters) {
            weight * temp
          } else
            temp
      }.sum

      sumOfWeightedTemps / weights.sum
    }
  }

  /**
    * @param temperatures Known temperatures
    * @return A function that, given a latitude in [-89, 90] and a longitude in [-180, 179],
    *         returns the predicted temperature at this location
    */
  def makeGrid(temperatures: Iterable[(Location, Double)]): (Int, Int) => Double = {
    val grid = new TemperatureGrid(temperatures)
    grid.getAverageTemperature
  }

  //    (lat: Int, lon: Int) => Visualization.predictTemperature(temperatures, Location(lat, lon))

  /**
    * @param temperaturess Sequence of known temperatures over the years (each element of the collection
    *                      is a collection of pairs of location and temperature)
    * @return A function that, given a latitude and a longitude, returns the average temperature at this location
    */
  def average(temperaturess: Iterable[Iterable[(Location, Double)]]): (Int, Int) => Double = {
    ???
    //    val n = temperaturess.size
    //    (lat: Int, lon: Int) =>
    //      temperaturess.toStream.par.map(makeGrid).map(gridFn => gridFn(lat, lon)).sum / n.toDouble
  }

  /**
    * @param temperatures Known temperatures
    * @param normals      A grid containing the “normal” temperatures
    * @return A sequence of grids containing the deviations compared to the normal temperatures
    */
  def deviation(temperatures: Iterable[(Location, Double)],
                normals: (Int, Int) => Double): (Int, Int) => Double = {
    ???
  }


}

