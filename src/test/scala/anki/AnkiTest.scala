package anki

import org.scalatest.FlatSpec

/**
 * User: hanlho
 * DateTime: 3/04/2014 7:39
 */
class AnkiTest extends FlatSpec {
  import anki.Anki._

  """Creating a card from lines:
      one line starts with a '.' (detail)
      one line starts with a '#' (info)
      one line starts with a ',' (hint)
    """   should
    """return a card which contains
      value for
      front,
      back which contains concat of 2 lines
      detail
      info
      hint
      """ in {

    val lines = List("#info", "front 1",",hint","back 1", "back 2", ".detail")

    val card = toCard(lines).right.get

    assert("front 1" === card.front)
    assert("back 1 back 2" === card.back)
    assert("detail" === card.detail)
    assert("info" === card.info)
    assert("hint" === card.hint)
  }
}
