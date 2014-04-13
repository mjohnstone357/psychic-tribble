package dump

import metadata.{DataType, ColumnType, Column, DatabaseTable}
import scala.collection.mutable


object Parsers {

  val AllParsers = List(
    new CommentParser(),
    new BlankLineParser(),
    new DropTableParser(),
    new CreateTableParser(),
    new UniversalParser()
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

class UniversalParser extends Parser {
  override def tryParse(linesIterator: Iterator[String]): ParseStatusResult = {
    val line: String = linesIterator.next()
    println("Warning: skipped line: " + firstFewChars(line) + "...")
    ParsedSuccessfully(NothingOfInterest())
  }


  def firstFewChars(s: String): String = {
    s.substring(0, Math.min(s.length, 50))
  }
}



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

      val tableName: String = parseTableName(firstLine)

      val columnLines = mutable.MutableList[String]()

      var currentLine = linesIterator.next()
      while (!currentLine.contains(';')) {

        val noWhitespace: String = currentLine.replace(" ", "")
        if (!currentLine.startsWith(")") && !noWhitespace.startsWith("PRIMARYKEY") && !noWhitespace.startsWith("KEY")) {
          columnLines += currentLine
        }

        currentLine = linesIterator.next()
      }

      val columns: List[Column] = (for (columnLine <- columnLines) yield makeColumn(columnLine)).toList


      ParsedSuccessfully(ParsedATable(DatabaseTable(tableName, columns)))
    } else {
      NoParse(List(firstLine))
    }
  }
  def parseTableName(createTableLine: String): String = createTableLine.split("`")(1)

  def makeColumn(columnLine: String) = {
    val words: Array[String] = columnLine.split(" ")
    val typeWord: String = words(3)
    val typeAndWidth: Array[String] = typeWord.split("\\(")
    Column(name = columnLine.split("`")(1), ColumnType(parseDataType(typeAndWidth(0)), typeAndWidth(1).split("\\)")(0).toInt))
  }

  def parseDataType(typeName: String): DataType = DataType.parse(typeName)
}