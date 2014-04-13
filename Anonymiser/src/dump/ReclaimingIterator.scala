package dump


class ReclaimingIterator(linesReclaimed: List[String], baseIterator: Iterator[String]) extends Iterator[String] {

  val primaryIterator = linesReclaimed.iterator

  override def next(): String = {
    if (primaryIterator.hasNext) {
      primaryIterator.next()
    } else {
      baseIterator.next()
    }
  }

  override def hasNext: Boolean = {
    primaryIterator.hasNext || baseIterator.hasNext
  }
}
