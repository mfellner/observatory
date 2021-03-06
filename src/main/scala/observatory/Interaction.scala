package observatory

import java.awt.image.BufferedImage

import com.sksamuel.scrimage.{Image, Pixel}
import observatory.Visualization.{interpolateColor, predictTemperature}

/**
  * 3rd milestone: interactive visualization
  */
object Interaction {

  /**
    * @param zoom Zoom level
    * @param x    X coordinate
    * @param y    Y coordinate
    * @return The latitude and longitude of the top-left corner of the tile, as per http://wiki.openstreetmap.org/wiki/Slippy_map_tilenames
    */
  def tileLocation(zoom: Int, x: Int, y: Int): Location = {
    preciseTileLocation(zoom, x.toDouble, y.toDouble)
  }

  def preciseTileLocation(zoom: Int, x: Double, y: Double): Location = {
    val n = Math.pow(2.0, zoom)
    val lonDeg = x / n * 360.0 - 180.0
    val latRad = Math.atan(Math.sinh(Math.PI * (1.0 - 2.0 * y / n)))
    val latDeg = Math.toDegrees(latRad)
    Location(latDeg, lonDeg)
  }

  /**
    * @param temperatures Known temperatures
    * @param colors       Color scale
    * @param zoom         Zoom level
    * @param x            X coordinate
    * @param y            Y coordinate
    * @return A 256×256 image showing the contents of the tile defined by `x`, `y` and `zooms`
    */
  def tile(temperatures: Iterable[(Location, Double)],
           colors: Iterable[(Double, Color)],
           zoom: Int,
           x: Int,
           y: Int): Image = {

    val imgType = BufferedImage.TYPE_INT_ARGB
    val width = 256
    val height = 256
    val alpha = 127
    val yStart = y * height
    val xStart = x * width

    val stream = (for {
      k <- yStart until yStart + height
      j <- xStart until xStart + width
    } yield (j, k)).toStream.par

    val pixels = stream
      .map({
        case (j, k) => tileLocation(zoom + 8, j, k)
      })
      .map(predictTemperature(temperatures, _))
      .map(interpolateColor(colors, _))
      .map(color => Pixel(color.red, color.green, color.blue, alpha))
      .toArray

    Image(width, height, pixels, imgType)
  }

  /**
    * Generates all the tiles for zoom levels 0 to 3 (included), for all the given years.
    *
    * @param yearlyData    Sequence of (year, data), where `data` is some data associated with
    *                      `year`. The type of `data` can be anything.
    * @param generateImage Function that generates an image given a year, a zoom level, the x and
    *                      y coordinates of the tile and the data to build the image from
    */
  def generateTiles[Data](yearlyData: Iterable[(Int, Data)],
                          generateImage: (Int, Int, Int, Int, Data) => Unit
                         ): Unit = {
    val zoomLevels = 0 to 3

    yearlyData.toStream.par.foreach({
      case (year, data) => zoomLevels.toStream.par.foreach(zoom => {
        val tiles = for {
          y <- 0 until Math.pow(2, zoom).toInt
          x <- 0 until Math.pow(2, zoom).toInt
        } yield (x, y)

        tiles.toStream.par.foreach({
          case (x, y) => generateImage(year, zoom, x, y, data)
        })
      })
    })
  }

}
