package observatory

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.prop.TableDrivenPropertyChecks._

@RunWith(classOf[JUnitRunner])
class ExtractionTest extends FunSuite {

  val stationsFile = "/stations.csv"

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

  test("parseStation") {
    forAll(Table(
      ("input", "expected"),
      ("152950,,,", Station(Some("152950"))),
      ("152960,,+45.417,+023.383", Station(Some("152960"), None, Some(45.417), Some(23.383))),
      ("176020,,+35.233,-033.717", Station(Some("176020"), None, Some(35.233), Some(-33.717))),
      ("162894,34113,+40.900,+014.300", Station(Some("162894"), Some("34113"), Some(40.900), Some(14.300)))
    )) { (input, expected) =>
      assert(Extraction.parseStation(input) === expected)
    }
  }

  test("locateTemperatures") {
    forAll(Table(
      ("year", "stationsFile", "temperaturesFile"),
      (1975, stationsFile, "/1975.csv")
    )) { (year, stationsFile, temperaturesFile) =>
      val temperatures = Extraction.locateTemperatures(year, stationsFile, temperaturesFile)
      assert(temperatures.nonEmpty)
    }
  }
}
