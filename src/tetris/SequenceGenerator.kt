package tetris

import java.util.*

// Abstraction of (in)finite mino sequence
interface SequenceGenerator {
  fun get(i: Int): Int?
  fun slice(range: IntRange) = range.map { get(it) }
  fun toList(): List<Int>
}

class SequenceGeneratorFinite(val list: List<Int>): SequenceGenerator {
  override fun get(i: Int) = list.getOrNull(i)
  override fun toList() = list
}

class SequenceGeneratorInfinite(val seed: Long): SequenceGenerator {
  private val random = Random(seed)
  private val list = mutableListOf<Int>()
  override fun get(i: Int): Int {
    while (list.size <= i) list.add(random.nextInt(7))
    return list[i]
  }
  override fun toList() = list
}

