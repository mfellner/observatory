package observatory

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.prop.Checkers

@RunWith(classOf[JUnitRunner])
class ManipulationTest extends FunSuite with Checkers {
  test("predictTemperature") {
    val path = "/2000-local.csv"
    val file = Main.getClass.getResource(path).toURI
    val temperatures = Utils.readAverageTemperatures(file)
    val prediction = Visualization.predictTemperature(temperatures, Location(0.0, 0.0))

    assert(prediction == 0)
  }
}
