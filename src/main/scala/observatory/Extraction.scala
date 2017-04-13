package observatory

import java.time.LocalDate

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.{Dataset, SparkSession}

/**
  * 1st milestone: data extraction
  */
object Extraction {
  Logger.getLogger("org.apache.spark").setLevel(Level.WARN)

  private lazy val spark = SparkSession.builder.master("local[4]").getOrCreate()

  import spark.implicits._

  /**
    * @param year             Year number
    * @param stationsFile     Path of the stations resource file to use (e.g. "/stations.csv")
    * @param temperaturesFile Path of the temperatures resource file to use (e.g. "/1975.csv")
    * @return A sequence containing triplets (date, location, temperature)
    */
  def locateTemperatures(year: Int,
                         stationsFile: String,
                         temperaturesFile: String): Iterable[(LocalDate, Location, Double)] = {
    //    val stations = io.file.readAll[Task](Paths.get(stationsFile), 4096)
    //      .through(text.utf8Decode)
    //      .through(text.lines)
    //      .filter(_.isEmpty)
    //      .map(parseStation)

    val stations = spark.read
      .csv(Extraction.getClass.getResource(stationsFile).getPath)
      .toDF("stn", "wban", "lat", "lon")
      .na.fill("", Seq("stn", "wban"))

    val temperatures = spark.read
      .csv(Extraction.getClass.getResource(temperaturesFile).getPath)
      .toDF("stn", "wban", "month", "day", "temp")
      .na.fill("", Seq("stn", "wban"))

    val joined = temperatures.join(stations, Seq("stn", "wban"))

    val result = joined
      .select("lat", "lon", "month", "day", "temp")
      .na.drop()
      .map(row => LocalTemperature(
        year,
        parseInt(row.getAs[String]("month")),
        parseInt(row.getAs[String]("day")),
        parseDouble(row.getAs[String]("lat")),
        parseDouble(row.getAs[String]("lon")),
        parseDouble(row.getAs[String]("temp"))
      ))
      .collect()

    result.map(t => (
      LocalDate.of(t.year, t.month, t.day),
      Location(t.lat, t.lon),
      t.temp
    ))
  }

  /**
    * @param records A sequence containing triplets (date, location, temperature)
    * @return A sequence containing, for each location, the average temperature over the year.
    */
  def locationYearlyAverageRecords(records: Iterable[(LocalDate, Location, Double)]): Iterable[(Location, Double)] = {
    records.par.toStream.groupBy(_._2).mapValues(records => {
      records.map(_._3).sum / records.size
    })
  }

  def parseDate(year: Int,
                month: String,
                day: String): LocalDate = LocalDate.of(year, month.toInt, day.toInt)

  def parseLocation(lat: String, lon: String) = Location(parseDouble(lat), parseDouble(lon))

  def parseNumber(s: String): String = s
    .replaceFirst("$\\+?0+", "")
    .replaceFirst("$-0+", "-")

  def parseDouble(s: String): Double = parseNumber(s).toDouble

  def parseInt(s: String): Int = parseNumber(s).toInt
}
