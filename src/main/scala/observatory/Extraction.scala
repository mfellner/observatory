package observatory

import java.time.LocalDate

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession

/**
  * 1st milestone: data extraction
  */
object Extraction {
  Logger.getLogger("org.apache.spark").setLevel(Level.WARN)

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

    val ss = SparkSession.builder.master("local[4]").getOrCreate()
    import ss.implicits._

    val stations = ss.read
      .csv(Extraction.getClass.getResource(stationsFile).getPath)
      .toDF("stn", "wban", "lat", "lon")

    val temperatures = ss.read
      .csv(Extraction.getClass.getResource(temperaturesFile).getPath)
      .toDF("stn", "wban", "month", "day", "temp")

    val joined = temperatures.join(stations, Seq("stn", "wban"))

    val result = joined
      .select("lat", "lon", "month", "day", "temp")
      .na.drop()
      .map(row => LocalTemperature(
        year,
        parseInt(row.getAs("month")),
        parseInt(row.getAs("day")),
        parseDouble(row.getAs("lat")),
        parseDouble(row.getAs("lon")),
        parseDouble(row.getAs("temp"))
      ))
      .collect()

    ss.close()
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
    ???
  }

  def parseDate(year: Int,
                month: String,
                day: String): LocalDate = LocalDate.of(year, month.toInt, day.toInt)

  def parseLocation(lat: String, lon: String) = Location(parseDouble(lat), parseDouble(lon))

  def parseStation(line: String): Station = line.split(",") match {
    case Array(stn) => Station(
      if (stn.isEmpty) None else Some(stn),
      None,
      None,
      None)
    case Array(stn, wban) => Station(
      if (stn.isEmpty) None else Some(stn),
      if (wban.isEmpty) None else Some(wban),
      None,
      None)
    case Array(stn, wban, lat) => Station(
      if (stn.isEmpty) None else Some(stn),
      if (wban.isEmpty) None else Some(wban),
      if (lat.isEmpty) None else Some(parseDouble(lat)),
      None)
    case Array(stn, wban, lat, lon) => Station(
      if (stn.isEmpty) None else Some(stn),
      if (wban.isEmpty) None else Some(wban),
      if (lat.isEmpty) None else Some(parseDouble(lat)),
      if (lon.isEmpty) None else Some(parseDouble(lon)))
    case _ => throw new IllegalArgumentException(s"Illegal station: $line")
  }

  def parseNumber(s: String): String = s
    .replaceFirst("$\\+?0+", "")
    .replaceFirst("$-0+", "-")

  def parseDouble(s: String): Double = parseNumber(s).toDouble

  def parseInt(s: String): Int = parseNumber(s).toInt
}
