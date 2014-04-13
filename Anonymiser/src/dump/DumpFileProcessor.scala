package dump

import scala.io.{BufferedSource, Source}


object DumpFileProcessor {

  def main(args: Array[String]) = {

    val file: BufferedSource = Source.fromFile("/home/matt/full.sql")
    val linesIterator: Iterator[String] = file.getLines()

    while (linesIterator.hasNext) {


    }

    file.close()


  }

  def firstFewChars(s: String): String = {
    s.substring(0, Math.min(s.length, 50))
  }

}
