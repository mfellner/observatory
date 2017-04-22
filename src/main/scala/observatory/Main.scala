package observatory

import java.io.File
import java.nio.file.Files
import java.util.concurrent.TimeUnit

object Main extends App {

  private def extractTemperatures() = {
    val outputPaths = Utils.extractAverageTemperatures("", Seq(2000))
    System.out.println(s"""Wrote files: ${outputPaths.mkString("\n")}""")
  }

  private def readTemperatures(path: String) = {
    val file = Main.getClass.getResource(path).toURI
    Utils.readAverageTemperatures(file)
  }

  val colors = Array(
    (60.0, Color(255, 255, 255)),
    (32.0, Color(255, 0, 0)),
    (12.0, Color(255, 255, 0)),
    (0.0, Color(0, 255, 255)),
    (-15.0, Color(0, 0, 255)),
    (-27.0, Color(255, 0, 255)),
    (-50.0, Color(33, 0, 107)),
    (-60.0, Color(0, 0, 0))
  )

  private def visualize() = {
    val temperatures = readTemperatures("/2000-local.csv")
    val image = Visualization.visualize(temperatures, colors)
    image.output(new File("test.png"))
  }

  private def generateOneTile(zoom: Int = 0,
                              x: Int = 0,
                              y: Int = 0,
                              fileName: String = "test.png") = {
    val temperatures = readTemperatures("/2000-local.csv")
    val image = Interaction.tile(temperatures, colors, zoom, x, y)
    image.output(new File(fileName))
  }

  private def writeTileImage(year: Int,
                             zoom: Int,
                             x: Int,
                             y: Int,
                             temperatures: Iterable[(Location, Double)]): Unit = {
    val image = Interaction.tile(temperatures, colors, zoom, x, y)
    val file = new File(s"target/temperatures/$year/$zoom/$x-$y.png")
    if (file.exists()) file.delete()
    file.getParentFile.mkdirs()
    image.output(file)
    return
  }

  private def writeTiles(years: Seq[Int]): Unit = {
    val yearlyData = Utils.getAverageTemperaturesByYear(years)
    Interaction.generateTiles(yearlyData, writeTileImage)
  }

  //  val temperatures = Array(
  //    (Location(45, -90), 0.0),
  //    (Location(45, 90), 32.0),
  //    (Location(-45, 90), 32.0),
  //    (Location(-45, -90), 0.0),
  //    (Location(83.62348, -34.145508), 16.0),
  //    (Location(83.7, -34.145508), -18.0)
  //  )

  val start = System.nanoTime
  //  extractTemperatures()
  //  visualize()
  //  generateOneTile(fileName = "total.png")
  //  generateOneTile(1, 0, 0, "00.png")
  //  generateOneTile(1, 1, 0, "10.png")
  //  generateOneTile(1, 0, 1, "01.png")
  //  generateOneTile(1, 1, 1, "11.png")
  writeTiles(1975 to 1995)

  val seconds = TimeUnit.SECONDS.convert(System.nanoTime - start, TimeUnit.NANOSECONDS)
  println(s"Time: $seconds seconds.")
}
