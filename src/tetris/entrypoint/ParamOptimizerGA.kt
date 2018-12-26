package tetris.entrypoint

import tetris.EvalParamsRandom
import tetris.InstanceGreedy
import tetris.InstanceGreedyResult
import tetris.blend
import java.io.FileWriter
import java.util.Random

data class InstanceResultPair(val instance: InstanceGreedy, val result: InstanceGreedyResult)

// Entry point
fun main(args: Array<String>) {
  val random = Random(641152)
  val numInstance = 30
  val numElite = 5

  // Initial evaluation parameters are randomly generated
  val params = Array(numInstance) { EvalParamsRandom(random) }

  // Generation loop
  (1..100).forEach { n ->
    // Sequence of mino (shared with all instances within a generation)
    val sequence = Array(500) { random.nextInt(7) }
    // Create game instances with specified sequence and parameters
    val instances = params.map { InstanceGreedy(sequence, it) }
    // Run all instances and store results
    val results = instances.map { InstanceResultPair(it, it.run(null)) }
    // Sort by final score
    val resultsSorted = results.sortedByDescending { it.result.score }

    // Output rankings
    println("*** Generation ${n} ***")
    resultsSorted.forEach {
      println("${if (it.result.success) "*" else " "} ${it.result.score} ${it.instance.params}")
    }
    println()

    // Define roulette selection
    val rouletteSum = results.sumBy { it.result.score }
    fun roulette(): Int {
      val roulette = random.nextDouble() * rouletteSum
      var rouletteAccum = 0.0
      var rouletteIndex = 0
      for (it in resultsSorted) {
        rouletteAccum += it.result.score
        if (roulette <= rouletteAccum) return rouletteIndex
        rouletteIndex ++
      }
      return resultsSorted.size - 1
    }

    // Output run of elitest instance to .log file
    val writer = FileWriter("${n}.log", false)
    InstanceGreedy(sequence, resultsSorted[0].instance.params).run(writer)
    writer.close()

    // Choose parameters for next generation
    // Elite selections
    (0..numElite-1).forEach {
      params[it] = resultsSorted[it].instance.params
    }
    // Blends
    (numElite..numInstance-1).forEach {
      val parentIndex1 = roulette()
      val parentIndex2 = roulette()
      params[it] = blend(resultsSorted[parentIndex1].instance.params, resultsSorted[parentIndex2].instance.params, random)
    }
  }
}

