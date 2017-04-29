package observatory

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.prop.Checkers

@RunWith(classOf[JUnitRunner])
class ManipulationTest extends FunSuite with Checkers {
  val testTemperatures = Array(
    (Location(45, -90), 0.0),
    (Location(45, 90), 32.0),
    (Location(-45, 90), 32.0),
    (Location(-45, -90), 0.0),
    (Location(83.62348, -34.145508), 16.0),
    (Location(83.7, -34.145508), -18.0)
  )

  test("makeGrid") {
    val getTemp = Manipulation.makeGrid(testTemperatures)

    assert(Math.abs(getTemp(45, -90)) < 0.001)
    assert(getTemp(45, 90) === 32.0)
    assert(getTemp(-45, 90) === 32.0)
    assert(Math.abs(getTemp(-45, -90)) < 0.001)
  }
}
