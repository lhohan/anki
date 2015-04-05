package anki

import java.io._

import anki.Fields.{ TAGS, INFO, HINT, DETAIL }

import scala.io.Source

object AnkiApp extends App {

  import anki.Anki._

  parseArgs(args) match {
    case None => printUsage()
    case Some((inputFile, outFile)) =>
      val source = Source.fromFile(inputFile)
      val writer = new PrintWriter(outFile)
      val numberOfValidCards = transformToAnkiFormat(source, writer)
      writer.close()

      printSummary(numberOfValidCards, outFile)
  }

  private def printSummary(validCards: Int, outFile: String) = {
    println(s"Cards written: $validCards to $outFile")
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

  case object HINT extends Field(",")

  case object DETAIL extends Field(".")
  case object INFO extends Field("#")
  case object TAGS extends Field("§")

}

private[anki] object Anki {

  case class Card(front: String, back: String, detail: String = "", info: String = "", hint: String = "", tags: String = "")

  private[anki] def toCard(cardLines: List[String]): Either[String, Card] = {
    def invalid = Left(s"invalid card: ${cardLines.mkString("\n")}")

    // subtle: Fields need to be called at least once for the filterNot to work ...
    val detail = DETAIL.filterLines(cardLines)
    val info = INFO.filterLines(cardLines)
    val hint = HINT.filterLines(cardLines)
    val tags = TAGS.filterLines(cardLines)
    val frontAndBackLines = cardLines.filterNot(line => Fields.all.keySet.foldLeft(false)((acc, f) => acc || line.startsWith(f)))
    frontAndBackLines match {
      case frontOnly :: Nil => invalid
      case front :: backs =>
        Right(
          Card(
            front,
            backs.mkString(" "),
            detail,
            info,
            hint,
            tags))
      case _ => invalid
    }
  }

  def transformToAnkiFormat(source: Source, writer: PrintWriter): Int = {

    def comment(line: String): Boolean = line.startsWith("//")

    val reader = source.getLines().filter { line => !comment(line) }
    var validCount = 0
    
    def writeCard(acc: List[String]): Unit = {
      toCard(acc.reverse) match {
        case Right(card) =>
          writer.println(toAnki(card))
          validCount = validCount + 1
        case Left(msg) => println(s"Skipping ... $msg")
      }
    }
    
    val rest = reader.foldLeft(List.empty[String]) { (acc, line) =>
      if (line.isEmpty) {
        if (acc.nonEmpty) {
          writeCard(acc)
        }
        List.empty[String]
      } else {
        line :: acc
      }
    }
    writeCard(rest)
    validCount
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
}
