package observatory

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.prop.Checkers

import scala.collection.concurrent.TrieMap

@RunWith(classOf[JUnitRunner])
class InteractionTest extends FunSuite with Checkers {
  test("tileLocation") {
    val location = Interaction.tileLocation(0, 0, 0)
    assert(location === Location(85.05112877980659, -180))
  }
}
