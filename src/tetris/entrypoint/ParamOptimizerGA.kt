package tetris.entrypoint

import tetris.*
import java.io.FileWriter
import java.util.Random

data class InstanceResultPair(val instance: InstanceGreedy, val result: InstanceResult)

// Parameter optimizer by genetic algorithm, using InstanceGreedy
// ./gradlew run_ga
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
    val results = instances.map { InstanceResultPair(it, it.run()) }
    // Sort by final score
    val resultsSorted = results.sortedByDescending { it.instance.score }

    // Output rankings
    println("*** Generation ${n} ***")
    resultsSorted.forEach {
      println("${if (it.result.success) "*" else " "} ${it.instance.score} ${it.instance.params}")
    }
    println()

    // Define roulette selection
    val rouletteSum = results.sumBy { it.instance.score }
    fun roulette(): Int {
      val roulette = random.nextDouble() * rouletteSum
      var rouletteAccum = 0.0
      var rouletteIndex = 0
      for (it in resultsSorted) {
        rouletteAccum += it.instance.score
        if (roulette <= rouletteAccum) return rouletteIndex
        rouletteIndex ++
      }
      return resultsSorted.size - 1
    }

    // Output run of elitest instance to .log file
    val writer = FileWriter("log/greedy-ga/${n}.log", false)
    writer.let {
      it.write("Instance with parameter: ${params}\n")
      it.write("Sequence: " + sequence.map { Data.minoName[it] }.joinToString("") + "\n")
    }
    InstanceGreedy(sequence, resultsSorted[0].instance.params).run()
    // Output status if FileWriter is given
    /*writer.let {
      it.write("Mino: ${Data.minoName[minoId]}, Score: ${score}, Field: ${bestPoint}\n")
      it.write(field.prettify())
      it.write("\n")
    }*/
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

