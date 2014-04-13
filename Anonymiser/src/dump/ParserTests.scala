package dump

import org.scalatest.{Matchers, FlatSpec}
import scala.io.Source


class ParserTests extends FlatSpec with Matchers {

  "The comment parser" should "correctly handle comment lines and non-comment lines" in {
    applyParser(new CommentParser(), "-- blah") should be (ParsedSuccessfully())
    applyParser(new CommentParser(), "/* blah */") should be (ParsedSuccessfully())
    applyParser(new CommentParser(), "abc") should be (NoParse(linesToReturn = List("abc")))
  }

  "The blank line parser" should "parse blank lines only" in {
    applyParser(new BlankLineParser(), "") should be (ParsedSuccessfully())
    applyParser(new BlankLineParser(), "blah") should be (NoParse(linesToReturn = List("blah")))
  }


  def applyParser(parser: Parser, text: String): ParseStatusResult = {
    val lines: Iterator[String] = Source.fromString(text+"\n").getLines()
    parser.tryParse(lines)
  }
}
