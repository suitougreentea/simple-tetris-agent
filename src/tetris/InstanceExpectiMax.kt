package tetris

import java.lang.IllegalArgumentException
import java.util.*

data class InstanceExpectiMaxResult(val success: Boolean, val score: Int, val instance: InstanceExpectiMax)

class InstanceExpectiMax(val sequence: Array<Int>, val params: EvalParams, val visibleMinos: Int, val minoBranchDepth: Int) {
  // Game field is initialized empty
  val field = emptyField()
  var score = 0
  val random = Random(641152)
  val history = mutableListOf<HistoryMean>()

  fun run(): InstanceExpectiMaxResult {
    // Main loop
    for (i in 0..sequence.size-10) {
      println(i)

      fun dfs(field: Field, visibleSequence: List<Int>, branchDepth: Int): SearchResult? =
        if (visibleSequence.isNotEmpty()) {
          //println("${visibleSequence} ${branchDepth}")
          val minoId = visibleSequence[0]
          val candidates = Data.mino[minoId].mapIndexed { state, _ ->
            val boundary = Data.boundary[minoId][state]
            (-boundary.xMin..9 - boundary.xMax).mapNotNull { x ->
              val candidateField = field.cloneDeep()
              val y = candidateField.findDropY(minoId, state, x)
              val success = candidateField.setMino(minoId, state, x, y)
              val lines = candidateField.eraseLine()
              if (success) {
                val data = PlacementData(minoId, state, x, y)
                if (branchDepth == 0) {
                  val evalScore = candidateField.evaluate(params)
                  Pair(SearchResult(evalScore, null), data)
                } else dfs(candidateField, visibleSequence.drop(1), branchDepth)?.let { Pair(it, data) }
              }
              else null
            }
          }.flatten()
          if (visibleSequence.size == visibleMinos) {
            // Top level
            println(candidates)
            candidates.maxBy { it.first.score }?.let {
              SearchResult(Double.NEGATIVE_INFINITY, it.second)
            }
          } else {
            candidates.maxBy { it.first.score }?.first
          }
        } else {
          //println("${visibleSequence} ${branchDepth}")
          if (branchDepth == 0) {
            throw IllegalArgumentException()
          } else {
            val candidates = (0..6).mapNotNull { minoId ->
              dfs(field, listOf(minoId), branchDepth - 1)
            }
            if (candidates.isEmpty()) null
            else SearchResult(candidates.map { it.score }.average(), null)
          }
        }

      val result = dfs(field, sequence.slice(i until i+visibleMinos), minoBranchDepth)
      if (result == null) return InstanceExpectiMaxResult(false, score, this)
      val placement = result.placement!!

      // Set mino on the best position; calculate score
      field.setMino(placement.minoId, placement.state, placement.x, placement.y)
      val lines = field.eraseLine()
      score += Data.score[lines]

      val evalScore = field.evaluate(params)
      history.add(HistoryMean(field.cloneDeep(), evalScore, score))
    }
    // When all minoes are placed
    return InstanceExpectiMaxResult(true, score, this)
  }
}

data class MinoNode(val parent: PlacementNode?, val minoId: Int)

data class PlacementNode(val parent: Node?, val minoId: Int, val x: Int, val y: Int, val state: Int, val field: Field, val evalScore: Double, val gameScore: Int) {
  //var children: List<Node> = emptyList()
}
data class PlacementData(val minoId: Int, val state: Int, val x: Int, val y: Int)
data class SearchResult(val score: Double, val placement: PlacementData?)

data class HistoryMean(val field: Field, val evalScore: Double, val gameScore: Int)