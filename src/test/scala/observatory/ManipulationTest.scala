package observatory

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.prop.Checkers
import org.scalatest.{FunSuite, Matchers}

@RunWith(classOf[JUnitRunner])
class ManipulationTest extends FunSuite with Checkers with Matchers {
  val london = Location(51.509865, -0.118092)
  val berlin = Location(52.520008, 13.404954)
  val munich = Location(48.137154, 11.576124)
  val dresden = Location(51.0508900, 13.7383200)
  val potsdam = Location(52.3988600, 13.0656600)
  val berlin2 = Location(52.537831, 13.375607)
  val berlin3 = Location(52.537539, 13.372547)
  val berlin4 = Location(52.535344, 13.374048)

  test("distance") {
    assert(Visualization.distance(berlin, london) - 930851 < 1)
    assert(Visualization.distance(berlin, munich) - 504300 < 1)
    assert(Visualization.distance(berlin, dresden) - 504300 < 1)
    assert(Visualization.distance(berlin, potsdam) - 26644 < 1)
    assert(Visualization.distance(berlin2, berlin3) - 224 < 1)
  }

  test("predictTemperature local") {
    //    val path = "/2000-local.csv"
    //    val file = Main.getClass.getResource(path).toURI
    //    val temperatures = Utils.readAverageTemperatures(file)
    val temperatures = Iterable(
      (berlin2, 20.0),
      (berlin3, 10.0)
    )
    val prediction = Visualization.predictTemperature(temperatures, berlin4)

    assert(prediction == 15.0)
  }

  test("predictTemperature Germany") {
    val temperatures = Iterable(
      (berlin, 20.0),
      (dresden, 5.0),
      (munich, 5.0)
    )
    val prediction = Visualization.predictTemperature(temperatures, potsdam)

    assert(prediction - 19.0 < 1)
  }
}
