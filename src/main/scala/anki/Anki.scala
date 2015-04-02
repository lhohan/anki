package anki

import java.io.PrintWriter

import anki.Fields.{ TAGS, INFO, HINT, DETAIL }

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

  private def printSummary(validCards: List[Card], outFile: String, deck: Deck) = {
    println(s"Cards written: ${validCards.size} to $outFile")
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

object Fields {

  val all = scala.collection.mutable.Map.empty[String, Field]

  sealed abstract class Field(val id: String) {
    all += (id -> this)

    def filterLines(cardLines: List[String]): String = cardLines.filter(_.startsWith(id)).map(_.tail).mkString(" ")
  }

  case object DETAIL extends Field(".")
  case object HINT extends Field(",")
  case object INFO extends Field("#")
  case object TAGS extends Field("§")

}

private[anki] object Anki {

  case class Card(front: String, back: String, detail: String = "", info: String = "", hint: String = "", tags: String = "")

  type Deck = List[Card]

  def transformToAnkiFormat(source: BufferedSource, writer: PrintWriter): (List[String], List[Card]) = {

    def comment(line: String): Boolean = line.startsWith("//")

    val linesInMem = source.getLines().filterNot(comment).toList
    val validCards = toDeck(linesInMem)
    writeDeckToWriter(validCards, writer)
    (linesInMem, validCards)
  }

  private def writeDeckToWriter(validCards: List[Card], writer: PrintWriter): Unit = {
    validCards.foreach(card => writer.println(toAnki(card)))
  }

  /**
   * Here we define a line to be imported into Anki.
   * When importing into Anki one need to use a card definition that matches.
   * TODO : Add to README on how to do this.
   */
  protected def toAnki(card: Card): String = {
    card.front + "\t" +
      card.back + "\t" +
      card.detail + "\t" +
      card.info + "\t" +
      card.hint + "\t" +
      card.tags
  }

  //  TODO @tailrec or use streams
  def toDeck(lines: List[String]): Deck = {

    def toCard(cardLines: List[String]): Option[Card] = {
      def invalid: None.type = {
        println(s"Skipping ... invalid card:${cardLines.mkString("\n")}")
        None
      }

      val frontAndBackLines = cardLines.filterNot(line => Fields.all.keySet.foldLeft(false)((acc, f) => acc || line.startsWith(f)))
      frontAndBackLines match {
        case frontOnly :: Nil => invalid
        case front :: backs => Some(
          Card(
            front,
            backs.mkString(" "),
            DETAIL.filterLines(cardLines),
            INFO.filterLines(cardLines),
            HINT.filterLines(cardLines),
            TAGS.filterLines(cardLines)))
        case _ => invalid
      }
    }

    if (lines.isEmpty) {
      List.empty[Card]
    } else {
      val (current, next) = lines.span(!_.trim.isEmpty)
      val nextTrimmed = next.dropWhile(_.trim.isEmpty)
      toCard(current) match {
        case Some(card) => card :: toDeck(nextTrimmed)
        case None       => toDeck(nextTrimmed)
      }
    }
  }

}
