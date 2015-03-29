package anki

import scala.io.Source
import java.io.PrintWriter

object AnkiApp extends App {

  import Anki._

  parseArgs(args) match {
    case None => printUsage
    case Some((inputFile, outFile)) =>
      val linesInMem = Source.fromFile(inputFile).getLines().filterNot(comment).toList
      val validCards = toDeck(linesInMem).filter(_.valid)
      val writer = new PrintWriter(outFile)
      validCards.foreach(card => writer.println(
        card.front + "\t" +
          card.back + "\t" +
          card.detail + "\t" +
          card.info + "\t" +
          card.hint))
      writer.close()

      printSummary(validCards, outFile, toDeck(linesInMem))
  }

  private def comment(line: String): Boolean = line.startsWith("//")

  private def printSummary(validCards: List[Card], outFile: String, deck: Deck) {
    println(s"Cards written: ${validCards.size} to $outFile")
    if (deck.size > validCards.size) {
      deck.filterNot(_.valid).foreach(c => println("Invalid card found. Skipped: " + c))
    }
  }

  private def parseArgs(args: Array[String]) = {
    args match {
      case Array(input) =>
        Some((input, input + "_anki.txt"))
      case Array(input, output, _*) =>
        Some((input, output))
      case Array() => None
    }
  }

  private def printUsage = {
    println("usage: Anki <input_file> <output_file>")
  }
}

object Anki {

  case class Card(front: String, back: String, detail: String = "", info: String = "", hint: String = "", valid: Boolean = true)

  type Deck = List[Card]

  //  TODO @tailrec
  def group(lines: List[String]): List[List[String]] = {
    if (lines.isEmpty) List(List())
    else {
      val (current, next) = lines.span(!_.trim.isEmpty)
      val nextNoEmptyHead = next.dropWhile(_.trim.isEmpty)
      if (nextNoEmptyHead == Nil) List(current)
      else current :: group(nextNoEmptyHead)
    }
  }

  def toDeck(lines: List[String]): Deck = {
    val cardLines = group(lines)
    cardLines.map {
      list =>
        val detail = list.filter(_.startsWith(".")).map(_.tail).mkString(" ")
        val hint = list.filter(_.startsWith(",")).map(_.tail).mkString(" ")
        val info = list.filter(_.startsWith("#")).map(_.tail).mkString(" ")
        val frontAndBackLines = list.filterNot(line => line.startsWith(".") || line.startsWith("#") || line.startsWith(","))
        frontAndBackLines match {
          case Nil           => Card("No front content", "No back content", detail, info, hint, false)
          case front :: Nil  => Card(front, "No back content", detail, info, hint, false)
          case front :: rest => Card(front, rest.mkString(" "), detail, info, hint)
        }
    }
  }

}
