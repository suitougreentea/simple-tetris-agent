package tetris.entrypoint

import tetris.Data
import tetris.EvalParams
import tetris.InstanceBeam
import tetris.prettify
import java.io.FileWriter
import java.util.Random

// Entry point
fun main(args: Array<String>) {
  val random = Random(8182019)

  val param = EvalParams(
      heightMaxCoeff = -0.01874245677636222,
      heightMeanCoeff = -0.04085781504556378,
      heightVarCoeff = -0.008293916454396812,
      holeCoeff = -0.08156931832466235,
      holeDepthPenaltyCoeff = -0.018919118549830517,
      parityCoeff = -0.0026200600157692796,
      erasableLinesByIPieceCoeff = 0.07530264712132304
  )

  val sequence = Array(510) { random.nextInt(7) }
  val instance = InstanceBeam(sequence, param, 1, 1, Int.MAX_VALUE, false)
  //val instance = InstanceExpectiMax(sequence, param, 2, 1)

  val writer = FileWriter("beam.log", false)
  val result = instance.run()

  writer.write("Instance with parameter: ${param}\n")
  writer.write("Sequence: " + sequence.map { Data.minoName[it] }.joinToString("") + "\n")
  instance.history.forEachIndexed { i, history ->
    writer.write("Mino: ${Data.minoName[sequence[i]]}, Score: ${history.gameScore}, Field: ${history.evalScore}\n")
    writer.write(history.field.prettify())
    writer.write("\n")
  }
  writer.close()
}


