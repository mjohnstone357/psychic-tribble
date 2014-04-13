package dump

import org.scalatest.{Matchers, FlatSpec}


class ReclaimingIteratorTests extends FlatSpec with Matchers {

  "The reclaiming iterator" should "return indicate no data is present with no reclaimed lines or base iterator values" in {

    val linesReclaimed: List[String] = List()
    val baseIterator = List().iterator

    val reclaimingIterator: ReclaimingIterator = new ReclaimingIterator(linesReclaimed, baseIterator)

    reclaimingIterator.hasNext should be (false)
  }

  it should "use the reclaimed lines where they are available" in {
    val linesReclaimed: List[String] = List("one", "two")
    val baseIterator = List().iterator

    val reclaimingIterator: ReclaimingIterator = new ReclaimingIterator(linesReclaimed, baseIterator)

    reclaimingIterator.hasNext should be (true)
    reclaimingIterator.next() should be ("one")
    reclaimingIterator.next() should be ("two")
  }

  it should "combine the reclaimed lines with those provided by the base iterator" in {
    val linesReclaimed: List[String] = List("one", "two")
    val baseIterator = List("three", "four").iterator

    val reclaimingIterator: ReclaimingIterator = new ReclaimingIterator(linesReclaimed, baseIterator)

    reclaimingIterator.hasNext should be (true)
    reclaimingIterator.next() should be ("one")
    reclaimingIterator.next() should be ("two")
    reclaimingIterator.next() should be ("three")
    reclaimingIterator.next() should be ("four")
  }

}
