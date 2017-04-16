package observatory

object Main extends App {
  val target = Main.getClass.getResource("/").getPath
  val outputPaths = Utils.extractAverageTemperatures(target, Seq(1975, 2000, 2015))
  System.out.println(s"""Wrote files: ${outputPaths.mkString("\n")}""")
}
