package dump

import org.scalatest.{Matchers, FlatSpec}
import scala.io.Source
import metadata._
import metadata.ColumnType
import metadata.Integer
import metadata.Column
import metadata.DatabaseTable


class ParserTests extends FlatSpec with Matchers {

  "The comment parser" should "correctly handle comment lines and non-comment lines" in {
    applyParser(new CommentParser(), "-- blah") should be (ParsedSuccessfully(NothingOfInterest()))
    applyParser(new CommentParser(), "/* blah */") should be (ParsedSuccessfully(NothingOfInterest()))
    applyParser(new CommentParser(), "abc") should be (NoParse(linesToReturn = List("abc")))
  }

  "The blank line parser" should "parse blank lines only" in {
    applyParser(new BlankLineParser(), "") should be (ParsedSuccessfully(NothingOfInterest()))
    applyParser(new BlankLineParser(), "blah") should be (NoParse(linesToReturn = List("blah")))
  }

  "The DROP TABLE parser" should "parse a single-line DROP TABLE statement" in {
    applyParser(new DropTableParser(), "DROP TABLE IF EXISTS `redirect`;") should be (ParsedSuccessfully(NothingOfInterest()))
  }

  it should "not parse a line not starting with 'DROP TABLE'" in {
    applyParser(new DropTableParser(), "blah blah") should be (NoParse(List("blah blah")))
  }

  "The CREATE TABLE parser" should "parse a multi-line CREATE TABLE statement" in {
    val inputText =
"""CREATE TABLE `redirect` (
  `rd_from` int(8) unsigned NOT NULL DEFAULT '0',
  `rd_namespace` int(11) NOT NULL DEFAULT '0',
  `rd_title` varbinary(255) NOT NULL DEFAULT '',
  `rd_interwiki` varbinary(32) DEFAULT NULL,
  `rd_fragment` varbinary(255) DEFAULT NULL,
  PRIMARY KEY (`rd_from`),
  KEY `rd_ns_title` (`rd_namespace`,`rd_title`,`rd_from`)
) ENGINE=InnoDB DEFAULT CHARSET=binary;"""

    val lines: Iterator[String] = Source.fromString(inputText + "\n").getLines()

    val result: ParseStatusResult = new CreateTableParser().tryParse(lines)

    result should be (
      ParsedSuccessfully(
        ParsedATable(
          DatabaseTable(
            name = "redirect",
            columns = List(
              Column("rd_from", ColumnType(Integer(), 8)),
              Column("rd_namespace", ColumnType(Integer(), 11)),
              Column("rd_title", ColumnType(VarBinary(), 255)),
              Column("rd_interwiki", ColumnType(VarBinary(), 32)),
              Column("rd_fragment", ColumnType(VarBinary(), 255))
            )
          )
    )))

    lines.hasNext should be (false)
  }

  it should "not parse a line not starting with 'CREATE TABLE'" in {
    applyParser(new CreateTableParser(), "blarg") should be (NoParse(List("blarg")))
  }

  "The INSERT INTO parser" should "parse a multi-line INSERT INTO statement" in {
    applyParser(new InsertIntoParser(), "INSERT INTO `redirect` VALUES (1,2,3),(4,5,6),(7,8,9)") should be (ParsedSuccessfully(NothingOfInterest()))
  }

  it should "not parse a li ne not starting with 'INSERT INTO'" in {
    applyParser(new InsertIntoParser(), "blarg") should be (NoParse(List("blarg")))
  }


  def applyParser(parser: Parser, text: String): ParseStatusResult = {
    val lines: Iterator[String] = Source.fromString(text + "\n").getLines()
    parser.tryParse(lines)
  }
}
