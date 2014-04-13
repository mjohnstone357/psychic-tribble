package dump


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
 * The parser successfully read the input.
 */
sealed case class ParsedSuccessfully() extends ParseStatusResult

class CommentParser extends Parser {
  override def tryParse(linesIterator: Iterator[String]): ParseStatusResult = {

    val firstLine: String = linesIterator.next()

    if (firstLine.startsWith("--") || firstLine.startsWith("/*")) {
      ParsedSuccessfully()
    } else {
      NoParse(linesToReturn = List(firstLine))
    }

  }
}

class BlankLineParser extends Parser {
  override def tryParse(linesIterator: Iterator[String]): ParseStatusResult = {

    val firstLine: String = linesIterator.next()

    if (firstLine.isEmpty) {
      ParsedSuccessfully()
    } else {
      NoParse(linesToReturn = List(firstLine))
    }

  }
}