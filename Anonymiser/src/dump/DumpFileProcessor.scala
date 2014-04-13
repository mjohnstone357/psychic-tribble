package dump

import scala.io.{BufferedSource, Source}
import scala.collection.mutable
import metadata.DatabaseTable


object DumpFileProcessor {

  def main(args: Array[String]) = {

    val tables = new mutable.MutableList[DatabaseTable]()

    val file: BufferedSource = Source.fromFile("/home/matt/firstTwo.sql")

    var linesIterator: Iterator[String] = file.getLines()

    while (linesIterator.hasNext) {
      var parserMatched = false

      var i = 0

      while (i < Parsers.AllParsers.length && !parserMatched) {
        val result: ParseStatusResult = Parsers.AllParsers(i).tryParse(linesIterator)
        result match {
          case NoParse(linesToReturn) => linesIterator = new ReclaimingIterator(linesToReturn, linesIterator)
          case ParsedSuccessfully(resultValue) =>
            parserMatched = true
            resultValue match {
              case NothingOfInterest() => ()
              case ParsedATable(table) => tables += table
            }
        }
        i += 1
      }

      assert(parserMatched, "No parser matched the input: " + linesIterator.next)

    }

    file.close()

    println("Got " + tables.size + " table(s):")

  }

}
