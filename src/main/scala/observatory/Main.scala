package observatory

import java.io.File
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

  private def colors = Array(
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

  private def generateOneTile() = {
    val temperatures = readTemperatures("/2000-local.csv")
    val zoom = 0
    val x = 0
    val y = 0
    val image = Interaction.tile(temperatures, colors, zoom, x, y)
    image.output(new File("test.png"))
  }

  val start = System.nanoTime
  //  extractTemperatures()
  //  visualize()
  generateOneTile()
  val seconds = TimeUnit.SECONDS.convert(System.nanoTime - start, TimeUnit.NANOSECONDS)
  println(s"Time: $seconds seconds.")
}
