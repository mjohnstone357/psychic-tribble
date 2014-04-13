package dump

import metadata.DatabaseTable


class Parsers {

  val AllParsers = List(
    new CommentParser(),
    new BlankLineParser()
  )

}

abstract class Parser {

  def tryParse(linesIterator: Iterator[String]): ParseStatusResult

}

sealed abstract class ParseStatusResult()

/**
 * This parser could not parse the input.
 * @param linesToReturn lines which we read before we realised we couldn't parse the input
 */
sealed case class NoParse(linesToReturn: List[String]) extends ParseStatusResult

/**
 * The parser successfully read the input, but doesn't need to return a value.
 */
sealed case class ParsedSuccessfully(result: ParseResultValue) extends ParseStatusResult

/**
 * A value returned by a parser.
 */
sealed abstract class ParseResultValue()

/**
 * The parser doesn't have anything to return.
 */
sealed case class NothingOfInterest() extends ParseResultValue

/**
 * The parser parsed some information about a table.
 */
sealed case class ParsedATable(table: DatabaseTable) extends ParseResultValue

class CommentParser extends Parser {
  override def tryParse(linesIterator: Iterator[String]): ParseStatusResult = {

    val firstLine: String = linesIterator.next()

    if (firstLine.startsWith("--") || firstLine.startsWith("/*")) {
      ParsedSuccessfully(NothingOfInterest())
    } else {
      NoParse(linesToReturn = List(firstLine))
    }

  }
}

class BlankLineParser extends Parser {
  override def tryParse(linesIterator: Iterator[String]): ParseStatusResult = {

    val firstLine: String = linesIterator.next()

    if (firstLine.isEmpty) {
      ParsedSuccessfully(NothingOfInterest())
    } else {
      NoParse(linesToReturn = List(firstLine))
    }

  }
}

class DropTableParser extends Parser {
  override def tryParse(linesIterator: Iterator[String]): ParseStatusResult = {
    val firstLine: String = linesIterator.next()
    if (firstLine.startsWith("DROP TABLE")) {
      ParsedSuccessfully(NothingOfInterest())
    } else {
      NoParse(List(firstLine))
    }
  }
}

class CreateTableParser extends Parser {
  override def tryParse(linesIterator: Iterator[String]): ParseStatusResult = {
    val firstLine: String = linesIterator.next()

    if (firstLine.startsWith("CREATE TABLE")) {
      var currentLine = firstLine
      while (!currentLine.contains(';')) {
        currentLine = linesIterator.next()
      }
      ParsedSuccessfully(NothingOfInterest())
    } else {
      NoParse(List(firstLine))
    }





  }
}