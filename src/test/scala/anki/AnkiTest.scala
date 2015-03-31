package anki

import org.scalatest.FlatSpec

/**
 * User: hanlho
 * DateTime: 3/04/2014 7:39
 */
class AnkiTest extends FlatSpec {
  import anki.Anki._

  "grouping lines" should "split a list of lines in a list of list of lines based on empty lines" in {

    val lines = List("line 1", "line 2", "   ", "", "", "lines 3", "line 4", "")

    val grouped = toDeck(lines)

    assert(2 === grouped.size, s"wrong number of groupings in $grouped")
  }

  "creating a new deck in a setup where the first line contains front and second contains back for 3 cards definitions" should
    "return a deck of 3 cards" in {

    val lines = List("front 1","back 1", "  ", "front 2","  back 2", "","   front 3","back 3")

    val deck = toDeck(lines)

    assert(3 === deck.size, s"wrong number of cards in $deck")
  }

  """Creating a new deck in a setup where
      there are three lines
      one line starts with a '.' (detail)
      one line starts with a '#' (info)
      one line starts with a ',' (hint)
    """   should
    """return a deck of
      "1 card which contains
      "value for
      "front,
      "back which contains concat of 2 lines
      "detail
      "info
      "hint
      """ in {

    val lines = List("#info", "front 1",",hint","back 1", "back 2", ".detail")

    val deck = toDeck(lines)

    assert(1 === deck.size, s"back card continuation: wrong number of cards in $deck")
    val card = deck(0)
    assert("front 1" === card.front)
    assert("back 1 back 2" === card.back)
    assert("detail" === card.detail)
    assert("info" === card.info)
    assert("hint" === card.hint)
  }
}
