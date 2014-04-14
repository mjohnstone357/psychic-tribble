package dump

import scala.io.{BufferedSource, Source}
import scala.collection.mutable
import metadata.DatabaseTable


object DumpFileProcessor {

  def main(args: Array[String]) = {

    val file: BufferedSource = Source.fromFile("/home/matt/firstTwo.sql")

    val linesIterator: Iterator[String] = file.getLines()

    val tables: Set[DatabaseTable] = readTableData(linesIterator)

    file.close()

    println("Got " + tables.size + " table(s):")

    println("Next line starts with 'INSERT INTO'? " + (if (linesIterator.next().startsWith("INSERT INTO")) "yes" else "no"))

  }

  def readTableData(iterator: Iterator[String]): Set[DatabaseTable] = {

    val tables = new mutable.MutableList[DatabaseTable]()

    var linesIterator = iterator
    
    var reachedInsertStatements = false

    while (linesIterator.hasNext && !reachedInsertStatements) {
      var parserMatched = false

      var i = 0

      while (i < Parsers.AllParsers.length && !parserMatched && !reachedInsertStatements) {
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

        val candidateInsertLine: String = linesIterator.next()
        if (candidateInsertLine.startsWith("INSERT INTO")) {
          println("Found INSERT INTO statement. Bailing out.")
          reachedInsertStatements = true
        } else {
          linesIterator = new ReclaimingIterator(List(candidateInsertLine), linesIterator)
        }

      }
    }
    tables.toSet
  }

}
