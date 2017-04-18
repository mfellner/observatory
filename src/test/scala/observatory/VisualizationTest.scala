package observatory


import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.prop.Checkers
import org.scalatest.{FunSuite, Matchers}

@RunWith(classOf[JUnitRunner])
class VisualizationTest extends FunSuite with Checkers with Matchers {
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

  test("predictTemperature exact") {
    val temperatures = Iterable(
      (berlin, 20.0),
      (potsdam, 19.5),
      (dresden, 5.0),
      (munich, 4.0)
    )
    val prediction = Visualization.predictTemperature(temperatures, dresden)

    assert(prediction - 5.0 < 0.00001)
  }

  test("findBounds") {
    val points = Array(
      (60.0, Color(255, 255, 255)),
      (32.0, Color(255, 0, 0)),
      (12.0, Color(255, 255, 0)),
      (0.0, Color(0, 255, 255)),
      (-15.0, Color(0, 0, 255)),
      (-27.0, Color(255, 0, 255)),
      (-50.0, Color(33, 0, 107)),
      (-60.0, Color(0, 0, 0))
    )

    var expected = (points(1), points(2))
    assert(Visualization.findBounds(points, 30) === expected)

    expected = (points(5), points(6))
    assert(Visualization.findBounds(points, -30) === expected)

    expected = (points(1), points(1))
    assert(Visualization.findBounds(points, 32) === expected)

    expected = (points.head, points.head)
    assert(Visualization.findBounds(points, 61) === expected)

    expected = (points.last, points.last)
    assert(Visualization.findBounds(points, -61) === expected)
  }

  test("interpolateColor") {
    var points = List(
      (-1.0, Color(255, 0, 0)),
      (0.0, Color(0, 0, 255)))
    assert(Visualization.interpolateColor(points, -0.75) === Color(191, 0, 64))

    points = List(
      (-3.0, Color(255, 0, 0)),
      (2.147483647E9, Color(0, 0, 255)))
    assert(Visualization.interpolateColor(points, 1.073741822E9) === Color(128, 0, 128))

    points = List(
      (-1.0, Color(255, 0, 0)),
      (15.0, Color(0, 0, 255)))
    assert(Visualization.interpolateColor(points, 3.0) === Color(191, 0, 64))
  }

  test("arrayIndexToLocation topLeft") {
    val width = 360
    val location = Visualization.arrayIndexToLocation(0, width)
    assert(location === Location(90, -180))
  }

  test("arrayIndexToLocation bottomRight") {
    val width = 360
    val height = 180
    val i = (width * height) - 1
    val location = Visualization.arrayIndexToLocation(i, width)
    assert(location === Location(-89, 179))
  }
}
