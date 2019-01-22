package tetris.entrypoint

import com.beust.klaxon.Klaxon
import tetris.*
import java.io.FileWriter
import java.util.Random

// For comparison of "Mixed score function" instances
// ./gradlew run_sampler_mixed
// All logs output to log/mixed-comparison/
fun main(args: Array<String>) {
  val klaxon = Klaxon()
  val seeds = listOf(8182019L, 641152L, 777L, 999999L, 29356L, 308562L, 289506L, 10395678L, 20459876L, 183920576L)
  val instanceTypes = listOf<Pair<String, (SequenceGenerator, EvalParams) -> Instance>>(
      Pair("d1-eval", { seq, param -> InstanceBreadthFirst(seq, param, 1, 1, Instance.evalScoreFunction, Int.MAX_VALUE, Instance.evalScoreFunction) }),
      Pair("d1-mixed", { seq, param -> InstanceBreadthFirst(seq, param, 1, 1, Instance.mixedScoreFunction, Int.MAX_VALUE, Instance.evalScoreFunction) }),
      Pair("d2-eval", { seq, param -> InstanceBreadthFirst(seq, param, 2, 2, Instance.evalScoreFunction, Int.MAX_VALUE, Instance.evalScoreFunction) }),
      Pair("d2-mixed", { seq, param -> InstanceBreadthFirst(seq, param, 2, 2, Instance.mixedScoreFunction, Int.MAX_VALUE, Instance.evalScoreFunction) }),
      Pair("b20-game-w100-eval", { seq, param -> InstanceBreadthFirst(seq, param, 20, 2, Instance.gameScoreFunction, 100, Instance.evalScoreFunction) }),
      Pair("b20-eval-w100-eval", { seq, param -> InstanceBreadthFirst(seq, param, 20, 2, Instance.evalScoreFunction, 100, Instance.evalScoreFunction) }),
      Pair("b20-mixed-w100-eval", { seq, param -> InstanceBreadthFirst(seq, param, 20, 2, Instance.mixedScoreFunction, 100, Instance.evalScoreFunction) }),
      Pair("b20-mixed-w100-mixed", { seq, param -> InstanceBreadthFirst(seq, param, 20, 2, Instance.mixedScoreFunction, 100, Instance.mixedScoreFunction) }),
      Pair("em1-eval", { seq, param -> InstanceExpectiMax(seq, param, 2, 1, Instance.evalScoreFunction) }),
      Pair("em1-mixed", { seq, param -> InstanceExpectiMax(seq, param, 2, 1, Instance.mixedScoreFunction) })
  )

  val param = EvalParams(
      heightMaxCoeff = -0.01874245677636222,
      heightMeanCoeff = -0.04085781504556378,
      heightVarCoeff = -0.008293916454396812,
      holeCoeff = -0.08156931832466235,
      holeDepthPenaltyCoeff = -0.018919118549830517,
      parityCoeff = -0.0026200600157692796,
      erasableLinesByIPieceCoeff = 0.07530264712132304
  )

  fun run(seed: Long, instanceType: Pair<String, (SequenceGenerator, EvalParams) -> Instance>) {
    val (name, generator) = instanceType
    val random = Random(seed)
    val sequence = SequenceGeneratorFinite(List(500) { random.nextInt(7) })

    val instance = generator(sequence, param)
    val writer = FileWriter("log/mixed-comparison/${seed}-${name}.log", false)
    val result = instance.run()
    val sequenceList = sequence.toList()

    println("Score: ${instance.getHistory().last().gameScore}")

    writer.write("Instance with parameter: ${param}\n")
    writer.write("Sequence: " + sequenceList.map { Data.minoName[it] }.joinToString("") + "\n")
    instance.getHistory().forEachIndexed { i, history ->
      writer.write("Mino: ${Data.minoName[sequenceList[i]]}, Score: ${history.gameScore}, Field: ${history.evalScore}\n")
      writer.write(history.field.prettify())
      writer.write("\n")
    }
    writer.close()

    FileWriter("log/mixed-comparison/${seed}-${name}.json", false).let {
      it.write(klaxon.toJsonString(InstanceLog(sequence.toList(), param, instance.getHistory())))
      it.close()
    }
  }

  seeds.forEach { seed ->
    instanceTypes.forEach { type ->
      println("${seed}-${type.first}")
      run(seed, type)
    }
  }
}


