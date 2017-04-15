package observatory

import java.io.{BufferedWriter, FileWriter}
import java.net.URI
import java.nio.file.{Files, Paths}

import scala.io.Source

object Utils {
  def extractAverageTemperatures(target: String, years: Seq[Int]): Seq[String] = {
    val stations = Extraction.readStations("/stations.csv").cache()

    years.map { year =>
      val outputPath = Paths.get(target, s"/$year-local.csv")
      val temperatures = Extraction.readTemperatures(s"/$year.csv")
      val localTemperatures = Extraction.createLocalTemperatures(year, stations, temperatures)
      val locatedTemperatures = Extraction.locateTemperatures(localTemperatures)
      val averageTemperatures = Extraction.locationYearlyAverageRecords(locatedTemperatures)

      Files.deleteIfExists(outputPath)
      Files.createFile(outputPath)
      val bw = new BufferedWriter(new FileWriter(outputPath.toFile))

      for (temperature <- averageTemperatures) {
        bw.append(s"${temperature._1.lat},${temperature._1.lon},${temperature._2}\n")
      }
      bw.flush()
      bw.close()
      outputPath.toString
    }
  }

  def readAverageTemperatures(file: URI): Iterable[(Location, Double)] = {
    Source.fromFile(file)
      .getLines
      .map(line => line.split(",") match {
        case Array(lat: String, lon: String, temp: String) =>
          (Location(lat.toDouble, lon.toDouble), temp.toDouble)
      })
      .toStream
  }
}
