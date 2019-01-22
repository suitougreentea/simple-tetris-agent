package tetris

import java.lang.IllegalArgumentException
import java.util.*

class InstanceExpectiMax(val sequence: SequenceGenerator, val params: EvalParams, val visibleMinos: Int, val minoBranchDepth: Int, val scoreFunction: (Double, Int) -> Double): Instance {
  // Game field is initialized empty
  val field = emptyField()
  var score = 0
  var lines = 0
  val _history = mutableListOf<InstanceHistory>()
  override fun getHistory() = _history

  override fun run(): InstanceResult {
    // Main loop
    var i = 0
    while (true) {
      //println(i)

      fun dfs(field: Field, currentScore: Int, visibleSequence: List<Int>, branchDepth: Int): SearchResult? =
        if (visibleSequence.isNotEmpty()) {
          //println("${visibleSequence} ${branchDepth}")
          val minoId = visibleSequence[0]
          val candidates = Data.mino[minoId].mapIndexed { state, _ ->
            val boundary = Data.boundary[minoId][state]
            (-boundary.xMin..9 - boundary.xMax).mapNotNull { x ->
              val candidateField = field.cloneDeep()
              val y = candidateField.findDropY(minoId, state, x)
              val success = candidateField.setMino(minoId, state, x, y)
              val justErasedLines = candidateField.eraseLine()
              val newScore = currentScore + Data.score[justErasedLines]
              if (success) {
                val data = PlacementData(minoId, state, x, y)
                if (branchDepth == 0) {
                  val evalScore = candidateField.evaluate(params)
                  Pair(SearchResult(evalScore, null), data)
                } else dfs(candidateField, newScore, visibleSequence.drop(1), branchDepth)?.let { Pair(it, data) }
              }
              else null
            }
          }.flatten()
          if (visibleSequence.size == visibleMinos) {
            // Top level
            candidates.maxBy { scoreFunction(it.first.score, currentScore) }?.let {
              SearchResult(Double.NEGATIVE_INFINITY, it.second)
            }
          } else {
            candidates.maxBy { scoreFunction(it.first.score, currentScore) }?.first
          }
        } else {
          //println("${visibleSequence} ${branchDepth}")
          if (branchDepth == 0) {
            throw IllegalArgumentException()
          } else {
            val candidates = (0..6).mapNotNull { minoId ->
              dfs(field, currentScore, listOf(minoId), branchDepth - 1)
            }
            if (candidates.isEmpty()) null
            else SearchResult(candidates.map { it.score }.average(), null)
          }
        }

      val visibleSequence = sequence.slice(i until i+visibleMinos)
      if (visibleSequence.any { it == null }) {
        // When all minoes are placed
        return InstanceResult(true)
      }
      visibleSequence as List<Int> // Guaranteed no null element
      val result = dfs(field, score, visibleSequence, minoBranchDepth)
      if (result == null) return InstanceResult(false)
      val placement = result.placement!!

      // Set mino on the best position; calculate score
      field.setMino(placement.minoId, placement.state, placement.x, placement.y)
      val justErasedLines = field.eraseLine()
      score += Data.score[justErasedLines]
      lines += justErasedLines

      val evalScore = field.evaluate(params)
      _history.add(InstanceHistory(field.cloneDeep(), evalScore, score, lines))
      i++
    }
  }
}

data class PlacementData(val minoId: Int, val state: Int, val x: Int, val y: Int)
data class SearchResult(val score: Double, val placement: PlacementData?)
