package anki

import java.io.PrintWriter

import scala.io.{ BufferedSource, Source }

object AnkiApp extends App {

  import anki.Anki._

  parseArgs(args) match {
    case None => printUsage()
    case Some((inputFile, outFile)) =>
      val source = Source.fromFile(inputFile)
      val writer = new PrintWriter(outFile)
      val (linesInMem, validCards) = transformToAnkiFormat(source, writer)
      writer.close()

      printSummary(validCards, outFile, toDeck(linesInMem))
  }

  private def printSummary(validCards: List[Card], outFile: String, deck: Deck) {
    println(s"Cards written: ${validCards.size} to $outFile")
    if (deck.size > validCards.size) {
      deck.filterNot(_.valid).foreach(c => println("Invalid card found. Skipped: " + c))
    }
  }

  private def parseArgs(args: Array[String]) = {
    args match {
      case Array(input) =>
        val outputName: String = {
          (if (input.contains(".")) input.split('.').init.mkString else input) + "_anki.txt"
        }
        Some((input, outputName))
      case Array(input, output, _*) =>
        Some((input, output))
      case Array() => None
    }
  }

  private def printUsage() = {
    println("usage: Anki <input_file> <output_file>")
  }
}

private[anki] object Anki {

  case class Card(front: String, back: String, detail: String = "", info: String = "", hint: String = "", valid: Boolean = true)

  type Deck = List[Card]

  def transformToAnkiFormat(source: BufferedSource, writer: PrintWriter): (List[String], List[Card]) = {

    def comment(line: String): Boolean = line.startsWith("//")

    val linesInMem = source.getLines().filterNot(comment).toList
    val validCards = toDeck(linesInMem).filter(_.valid)
    writeDeckToWriter(validCards, writer)
    (linesInMem, validCards)
  }

  private def writeDeckToWriter(validCards: List[Card], writer: PrintWriter): Unit = {
    validCards.foreach(card => writer.println(toAnki(card)))
  }

  /**
   *   Here we define a line to be imported into Anki.
   *   When importing into Anki one need to use a card definition that matches.
   *   TODO : Add to README on how to do this.
   */
  protected def toAnki(card: Card): String = {
    card.front + "\t" +
      card.back + "\t" +
      card.detail + "\t" +
      card.info + "\t" +
      card.hint
  }

  //  TODO @tailrec or use streams
  def group(lines: List[String]): List[List[String]] = {
    if (lines.isEmpty) {
      List(List())
    } else {
      val (current, next) = lines.span(!_.trim.isEmpty)
      val nextNoEmptyHead = next.dropWhile(_.trim.isEmpty)
      if (nextNoEmptyHead == Nil) {
        List(current)
      } else {
        current :: group(nextNoEmptyHead)
      }
    }
  }

  def toDeck(lines: List[String]): Deck = {
    val cardLines = group(lines)
    cardLines.map {
      list =>
        val detail = list.filter(_.startsWith(".")).map(_.tail).mkString(" ")
        val hint = list.filter(_.startsWith(",")).map(_.tail).mkString(" ")
        val info = list.filter(_.startsWith("#")).map(_.tail).mkString(" ")
        val frontAndBackLines = list.filterNot(line => line.startsWith(".") || line.startsWith("#") || line
          .startsWith(","))
        frontAndBackLines match {
          case Nil           => Card("No front content", "No back content", detail, info, hint, false)
          case front :: Nil  => Card(front, "No back content", detail, info, hint, false)
          case front :: rest => Card(front, rest.mkString(" "), detail, info, hint)
        }
    }
  }

}
