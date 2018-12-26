package tetris

import java.io.FileWriter
import java.util.Random
import kotlin.math.min

// Entry point
fun main(args: Array<String>) {
  val param = EvalParams(
      heightMaxCoeff=-0.01874245677636222,
      heightMeanCoeff=-0.04085781504556378,
      heightVarCoeff=-0.008293916454396812,
      holeCoeff=-0.08156931832466235,
      holeDepthPenaltyCoeff=-0.018919118549830517,
      parityCoeff=-0.0026200600157692796,
      erasableLinesByIPieceCoeff=0.07530264712132304
  )

  val forward = 100

  val writer = FileWriter("sample.log", false)

  repeat(1000) {
    println(it)
    val random = Random(System.currentTimeMillis())
    val sequence = Array(510) { random.nextInt(7) }
    val instance = InstanceBeam(sequence, param, 1, 1, Int.MAX_VALUE, false)
    val result = instance.run()
    val iSample = (Math.random() * 200 + 100).toInt()
    //if (iSample + forward < instance.history.size) {
    /*if (iSample < instance.history.size) {
      val iEnd = min(iSample + forward, instance.history.size - 1)
      writer.write("${instance.history[iSample].evalScore}, ${instance.history[iEnd].gameScore - instance.history[iSample].gameScore}, ${instance.history[iEnd].evalScore}, ${iEnd - iSample}, ${instance.history.size}\n")
    }*/
    //}
    if (iSample < instance.history.size) {
      writer.write("${instance.history[iSample].evalScore}, ${instance.history.size - iSample}, ${instance.history.size}, ${if (instance.history.size == 501) 1 else 0}\n")
    }
  }
  writer.close()
}


