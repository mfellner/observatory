package observatory

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.prop.TableDrivenPropertyChecks._

@RunWith(classOf[JUnitRunner])
class ExtractionTest extends FunSuite {

  test("parseDouble") {
    forAll(Table(
      ("input", "expected"),
      ("+75.417", 75.417),
      ("+088.900", 88.900),
      ("-179.600", -179.600),
      ("-00.693", -0.693)
    )) { (input, expected) =>
      assert(Extraction.parseDouble(input) === expected)
    }
  }

  //  test("parseStation") {
  //    forAll(Table(
  //      ("input", "expected"),
  //      ("152950,,,", Station(Some("152950"))),
  //      ("152960,,+45.417,+023.383", Station(Some("152960"), None, Some(45.417), Some(23.383))),
  //      ("176020,,+35.233,-033.717", Station(Some("176020"), None, Some(35.233), Some(-33.717))),
  //      ("162894,34113,+40.900,+014.300", Station(Some("162894"), Some("34113"), Some(40.900), Some(14.300)))
  //    )) { (input, expected) =>
  //      assert(Extraction.parseStation(input) === expected)
  //    }
  //  }

  val stationsFile = "/stations.csv"

  test("getFiles") {
    assert(Extraction.getClass.getResource(stationsFile).getPath.isEmpty === false)
    assert(Extraction.getClass.getResource("/1975-test.csv").getPath.isEmpty === false)
    assert(Extraction.getClass.getResource("/1995-test.csv").getPath.isEmpty === false)
    assert(Extraction.getClass.getResource("/2000-test.csv").getPath.isEmpty === false)
    assert(Extraction.getClass.getResource("/2015-test.csv").getPath.isEmpty === false)
  }

  test("locateTemperatures") {
    forAll(Table(
      ("year", "stationsFile", "temperaturesFile"),
      (1975, stationsFile, "/1975-test.csv"),
      (1995, stationsFile, "/1995-test.csv"),
      (2000, stationsFile, "/2000-test.csv"),
      (2015, stationsFile, "/2015-test.csv")
    )) { (year, stationsFile, temperaturesFile) =>
      val temperatures = Extraction.locateTemperatures(year, stationsFile, temperaturesFile)
      assert(temperatures.nonEmpty)
      assert(temperatures.head._1.getYear === year)
      assert(temperatures.head._2.lat !== null)
      assert(temperatures.head._2.lon !== null)
      assert(temperatures.head._3 !== null)
    }
  }
}
