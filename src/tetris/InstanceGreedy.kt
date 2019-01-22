package tetris

import java.io.FileWriter

// Used by ParamOptimizerGA
// Equivalent to InstanceBreadthFirst(maxDepth = 1, beamWidth = Int.MAX_VALUE, beamScoreFunction = Instance.evalScoreFunction)
class InstanceGreedy(val sequence: Array<Int>, val params: EvalParams): Instance {
  // Game field is initialized empty
  val field = emptyField()
  var score = 0

  override fun run(): InstanceResult {
    // Output status if FileWriter is given

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
      if (bestPoint == null) return InstanceResult(false)

      // Set mino on the best position; calculate score
      field.setMino(minoId, bestState, bestX, bestY)
      val lines = field.eraseLine()
      score += Data.score[lines]

    }
    // When all minoes are placed
    return InstanceResult(true)
  }

  override fun getHistory(): List<InstanceHistory> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }
}

