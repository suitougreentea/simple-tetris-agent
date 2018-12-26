package tetris

import java.io.FileWriter

data class InstanceResult(val success: Boolean, val score: Int, val instance: Instance)

// An instance runs game automatically with specified mino sequence and evaluation parameters
class Instance(val sequence: Array<Int>, val params: EvalParams) {
  // Game field is initialized empty
  val field = emptyField()
  var score = 0

  fun run(writer: FileWriter?): InstanceResult {
    // Output status if FileWriter is given
    writer?.let {
      it.write("Instance with parameter: ${params}\n")
      it.write("Sequence: " + sequence.map { Data.minoName[it] }.joinToString("") + "\n")
    }

    // Main loop
    for (minoId in sequence) {
      var bestPoint: Double? = null
      var bestState = -1
      var bestX = -1
      var bestY = -1

      // Iterate all rotation state / position and find the best
      Data.mino[minoId].forEachIndexed { state, _ ->
        val boundary = Data.boundary[minoId][state]
        (-boundary.xMin .. 9-boundary.xMax).forEach { x ->
          val candidateField = field.cloneDeep()
          val y = candidateField.findDropY(minoId, state, x)
          val success = candidateField.setMino(minoId, state, x, y)
          candidateField.eraseLine()

          val point = candidateField.evaluate(params)

          val max = bestPoint // Since mutable property is trapped by null-check
          if (success && (max == null || max < point)) {
            bestPoint = point
            bestState = state
            bestX = x
            bestY = y
          }
        }
      }

      // If no candidate found, the game cannot be run more
      if (bestPoint == null) return InstanceResult(false, score, this)

      // Set mino on the best position; calculate score
      field.setMino(minoId, bestState, bestX, bestY)
      val lines = field.eraseLine()
      score += Data.score[lines]

      // Output status if FileWriter is given
      writer?.let {
        it.write("Mino: ${Data.minoName[minoId]}, Score: ${score}, Field: ${bestPoint}\n")
        it.write(field.prettify())
        it.write("\n")
      }
    }
    // When all minoes are placed
    return InstanceResult(true, score, this)
  }

}

