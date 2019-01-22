package tetris.entrypoint

import com.beust.klaxon.Klaxon
import tetris.*
import java.io.FileWriter

// For analysis of evaluation function
// ./gradlew run_sampler_infinite
// All logs output to log/d1-infinite/
fun main(args: Array<String>) {
  val param = EvalParams(
      heightMaxCoeff = -0.01874245677636222,
      heightMeanCoeff = -0.04085781504556378,
      heightVarCoeff = -0.008293916454396812,
      holeCoeff = -0.08156931832466235,
      holeDepthPenaltyCoeff = -0.018919118549830517,
      parityCoeff = -0.0026200600157692796,
      erasableLinesByIPieceCoeff = 0.07530264712132304
  )

  val klaxon = Klaxon()
  val writer = FileWriter("log/d1-infinite/summary.csv", false)

  repeat(1000) {
    println(it)
    val sequence = SequenceGeneratorInfinite(System.currentTimeMillis())
    val instance = InstanceBreadthFirst(sequence, param, 1, 1, Instance.evalScoreFunction, Int.MAX_VALUE, Instance.evalScoreFunction)
    val result = instance.run()
    val history = instance.getHistory()
    val sampleMin = 100
    val sampleForward = 0
    val sampleMax = history.size - sampleForward
    if (sampleMin < sampleMax) {
      val iSample = (Math.random() * (sampleMax - sampleMin) + sampleMin).toInt()
      writer.write(
          listOf(
              iSample,
              history.size,
              history[iSample].evalScore,
              history[iSample+sampleForward].evalScore,
              history[history.size-1].evalScore,
              history[iSample].gameScore,
              history[iSample+sampleForward].gameScore,
              history[history.size-1].gameScore,
              history[iSample].erasedLines,
              history[iSample+sampleForward].erasedLines,
              history[history.size-1].erasedLines
              ).joinToString(", ") + "\n"
      )
    }
    FileWriter("log/d1-infinite/${it+1}.json", false).let {
      it.write(klaxon.toJsonString(InstanceLog(sequence.toList(), param, history)))
      it.close()
    }
  }
  writer.close()
}

data class InstanceLog(val sequence: List<Int>, val param: EvalParams, val history: List<InstanceHistory>)

