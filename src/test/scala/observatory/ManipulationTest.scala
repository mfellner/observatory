package observatory

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.prop.Checkers
import org.scalactic.TolerantNumerics

@RunWith(classOf[JUnitRunner])
class ManipulationTest extends FunSuite with Checkers {
  test("distance") {

    val london = Location(51.509865, -0.118092)
    val berlin = Location(52.520008, 13.404954)
    val d = Visualization.distance(london, berlin)
    implicit val doubleEquality = TolerantNumerics.tolerantDoubleEquality(2F)
    assert(d === 930851F)
  }

  test("predictTemperature") {
    val path = "/2000-local.csv"
    val file = Main.getClass.getResource(path).toURI
    val temperatures = Utils.readAverageTemperatures(file)
    val prediction = Visualization.predictTemperature(temperatures, Location(0.0, 0.0))

    assert(prediction == 0)
  }
}
