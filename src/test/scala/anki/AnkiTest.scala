package anki

import org.scalatest.{FunSuite, FlatSpec}

/**
 * User: hanlho
 * DateTime: 3/04/2014 7:39
 */
class AnkiTest extends FunSuite {
  import anki.Anki._

  test("card: complete"){

    val lines = List("#info", "front 1",",hint","back 1", "§the tags","back 2", ".detail")

    val card = toCard(lines).right.get

    assert("front 1" === card.front)
    assert("back 1 back 2" === card.back)
    assert("detail" === card.detail)
    assert("info" === card.info)
    assert("hint" === card.hint)
    assert("the tags" === card.tags)
  }

  test("card: most basic"){

    val lines = List("front 1","back 1")

    val card = toCard(lines).right.get

    assert("front 1" === card.front)
    assert("back 1" === card.back)
    assert("" === card.detail)
    assert("" === card.info)
    assert("" === card.hint)
    assert("" === card.tags)
  }
  
  test("card: missing back"){

    val lines = List("front 1")

    val msg = toCard(lines).left.get

    assert(msg.startsWith("invalid"))
  }

  test("card: missing back, all other meta data present"){

    val lines = List("#info", "front 1",",hint","§the tags",".detail")

    val msg = toCard(lines).left.get

    assert(msg.startsWith("invalid"))
  }

}
