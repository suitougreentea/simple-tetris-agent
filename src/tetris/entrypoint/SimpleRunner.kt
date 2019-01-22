package tetris.entrypoint

import tetris.*
import java.io.FileWriter
import java.util.Random

// A simplest way to run an instance and output a log
// ./gradlew run_simple
fun main(args: Array<String>) {
  val random = Random(641152)

  val param = EvalParams(
      heightMaxCoeff = -0.01874245677636222,
      heightMeanCoeff = -0.04085781504556378,
      heightVarCoeff = -0.008293916454396812,
      holeCoeff = -0.08156931832466235,
      holeDepthPenaltyCoeff = -0.018919118549830517,
      parityCoeff = -0.0026200600157692796,
      erasableLinesByIPieceCoeff = 0.07530264712132304
  )

  val sequence = SequenceGeneratorFinite(List(500) { random.nextInt(7) })
  val instance = InstanceBreadthFirst(sequence, param, 20, 2, Instance.mixedScoreFunctionExp, 100, Instance.mixedScoreFunctionExp)
  //val instance = InstanceBreadthFirst(sequence, param, 1, 1, Int.MAX_VALUE, Instance.evalScoreFunction)
  //val instance = InstanceExpectiMax(sequence, param, 2, 1, Instance.mixedScoreFunction)

  val writer = FileWriter("log/beam.log", false)
  val result = instance.run()
  val sequenceList = sequence.toList()

  writer.write("Instance with parameter: ${param}\n")
  writer.write("Sequence: " + sequenceList.map { Data.minoName[it] }.joinToString("") + "\n")
  instance.getHistory().forEachIndexed { i, history ->
    writer.write("Mino: ${Data.minoName[sequenceList[i]]}, Score: ${history.gameScore}, Field: ${history.evalScore}\n")
    writer.write(history.field.prettify())
    writer.write("\n")
  }
  writer.close()
}


