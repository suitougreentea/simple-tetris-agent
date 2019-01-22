package tetris

import java.lang.IllegalStateException
import java.util.*

class InstanceBreadthFirst(val sequence: SequenceGenerator, val params: EvalParams, val maxDepth: Int, val visibleMinos: Int,
                           val scoreFunction: (Double, Int) -> Double, val beamWidth: Int, val beamScoreFunction: (Double, Int) -> Double): Instance {
  // Game field is initialized empty
  val field = emptyField()
  var score = 0
  var lines = 0
  val random = Random(641152)
  val _history = mutableListOf<InstanceHistory>()
  override fun getHistory() = _history

  override fun run(): InstanceResult {
    // Main loop
    var i = 0
    while (true) {
      //println(i)
      val searchMinoIds = sequence.slice(i..i+visibleMinos-1) + List(maxDepth - visibleMinos) { random.nextInt(7) }
      if (searchMinoIds.any { it == null }) {
        // When all minoes are placed
        return InstanceResult(true)
      }
      searchMinoIds as List<Int> // Guaranteed no null element

      val nodes = Array(maxDepth+1) { emptyList<Node>() }
      nodes[0] = listOf(Node(null, -1, -1, -1, field, .0, 0))

      for (depth in 1..maxDepth) {
        val minoId = searchMinoIds[depth-1]

        val candidates = nodes[depth-1].map { parent ->
          Data.mino[minoId].mapIndexed { state, _ ->
            val boundary = Data.boundary[minoId][state]
            (-boundary.xMin .. 9-boundary.xMax).mapNotNull { x ->
              val candidateField = parent.field.cloneDeep()
              val y = candidateField.findDropY(minoId, state, x)
              val success = candidateField.setMino(minoId, state, x, y)
              val justErasedLines = candidateField.eraseLine()
              val evalScore = candidateField.evaluate(params)
              if (success) Node(parent, x, y, state, candidateField, evalScore, parent.gameScore + Data.score[justErasedLines])
              else null
            }
          }.flatten()
        }.flatten()
        nodes[depth] = candidates.sortedByDescending { beamScoreFunction(it.evalScore, it.gameScore) }.subList(0, Math.min(beamWidth, candidates.size))
      }

      val maxNode = nodes[maxDepth].maxBy { scoreFunction(it.evalScore, it.gameScore) }

      if (maxNode == null) return InstanceResult(false)
      var chooseNode_ = maxNode
      repeat(maxDepth - 1) { chooseNode_ = chooseNode_?.parent ?: throw IllegalStateException("Null parent") }
      val chooseNode = chooseNode_ ?: throw IllegalStateException("Null chosen node")

      // Set mino on the best position; calculate score
      field.setMino(searchMinoIds[0], chooseNode.state, chooseNode.x, chooseNode.y)
      val justErasedLines = field.eraseLine()
      score += Data.score[justErasedLines]
      lines += justErasedLines

      _history.add(InstanceHistory(field.cloneDeep(), chooseNode.evalScore, score, lines))
      i++
    }
  }
}

data class Node(val parent: Node?, val x: Int, val y: Int, val state: Int, val field: Field, val evalScore: Double, val gameScore: Int) {
  //var children: List<Node> = emptyList()
}
