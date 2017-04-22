package observatory

import java.io.File
import java.util.concurrent.TimeUnit

import org.apache.log4j.{Level, Logger}

object Main extends App {

  case class Options(yearStart: Option[Int], yearEnd: Option[Int], test: Boolean) {
    override def toString: String = s"{ start=$yearStart end=$yearEnd test=$test }"
  }

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

  var totalTiles = 0
  var processedTiles = 0

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
    processedTiles += 1
    println(s"Wrote $processedTiles of $totalTiles tiles.")
    return
  }

  private def writeTiles(years: Seq[Int]): Unit = {
    totalTiles = getComplexity(Seq(0, 1, 2, 3), years.size)
    val yearlyData = Utils.getAverageTemperaturesByYear(years)
    Interaction.generateTiles(yearlyData, writeTileImage)
  }

  private def createTestImages() = {
    val testTemperatures = Array(
      (Location(45, -90), 0.0),
      (Location(45, 90), 32.0),
      (Location(-45, 90), 32.0),
      (Location(-45, -90), 0.0),
      (Location(83.62348, -34.145508), 16.0),
      (Location(83.7, -34.145508), -18.0)
    )
    val testYearlyData = Seq((2015, testTemperatures.toIterable))
    totalTiles = getComplexity(Seq(0, 1, 2, 3), 1)
    println(s"Generating $totalTiles tiles.")
    Interaction.generateTiles(testYearlyData, writeTileImage)
  }

  private def run(options: Options): Unit = {
    val start = System.nanoTime

    if (options.yearStart.nonEmpty && options.yearEnd.nonEmpty)
      writeTiles(options.yearStart.get to options.yearEnd.get)
    else if (options.test) {
      println("Create test images.")
      createTestImages()
    } else
      sys.error("Missing arguments.")

    val seconds = TimeUnit.SECONDS.convert(System.nanoTime - start, TimeUnit.NANOSECONDS)
    println(s"Time: $seconds seconds.")
  }

  private def parseArgs(args: Seq[String]): Options = {
    val startYear = args.find(s => s.matches("^--start=\\S+$")).map(_.substring(8).toInt)
    val endYear = args.find(s => s.matches("^--end=\\S+$")).map(_.substring(6).toInt)
    val test = args.exists(s => s.matches("^--test$"))
    Options(startYear, endYear, test)
  }

  def getComplexity(zoomLevels: Seq[Int], numberOfYears: Int): Int = {
    val tilesPerYear = zoomLevels.map(z => Math.pow(2, 2 * z).toInt).sum
    numberOfYears * tilesPerYear
  }

  Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
  val options = parseArgs(args)
  println(s"Starting with $options")
  run(options)
}
