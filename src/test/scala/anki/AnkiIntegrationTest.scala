package anki

import java.io.{PrintWriter, StringWriter}

import org.scalatest.FunSuite

import scala.io.Source

/**
 * Created by hans on 29/03/15.
 */
class AnkiIntegrationTest extends FunSuite {

  test("transform file input to Anki format") {
    val input = Source.fromURL(getClass.getResource("/sample_input.txt"))

    val writer = new StringWriter()

    val printWriter = new PrintWriter(writer)
    Anki.transformToAnkiFormat(input, printWriter)
    printWriter.close()

    assertResult {
      Source.fromURL(getClass.getResource("/expected_output.txt")).getLines().mkString("\n")
    } {
         writer.toString
    }

  }

}
