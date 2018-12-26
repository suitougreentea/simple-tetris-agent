package tetris

import java.io.FileWriter
import java.lang.IllegalStateException
import java.util.*
import kotlin.math.min

data class InstanceResultBeam(val success: Boolean, val score: Int, val instance: InstanceBeam)

class InstanceBeam(val sequence: Array<Int>, val params: EvalParams, val maxDepth: Int, val visibleMinos: Int, val beamWidth: Int, val useGameScore: Boolean) {
  // Game field is initialized empty
  val field = emptyField()
  var score = 0
  val random = Random(641152)
  val history = mutableListOf<HistoryBeam>()

  fun run(): InstanceResultBeam {
    // Main loop
    for (i in 0..sequence.size-10) {
      //println(i)
      val searchMinoIds = sequence.slice(i..i+visibleMinos-1) + List(maxDepth - visibleMinos) { random.nextInt(7) }
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
              val lines = candidateField.eraseLine()
              val evalScore = candidateField.evaluate(params)
              if (success) Node(parent, x, y, state, candidateField, evalScore, parent.gameScore + Data.score[lines])
              else null
            }
          }.flatten()
        }.flatten()
        nodes[depth] = candidates.sortedByDescending { it.evalScore }.subList(0, Math.min(beamWidth, candidates.size))
      }

      /*val nodes = Array(maxDepth+1) { PriorityQueue<Node> { a, b -> when {
        a.evalScore < b.evalScore -> 1
        a.evalScore > b.evalScore -> -1
        else -> 0
      } } }

      nodes[0].add(Node(null, -1, -1, -1, field, .0, 0))

      for (depth in 1..maxDepth) {
        val minoId = searchMinoIds[depth-1]

        for (ci in 0 until min(nodes[depth-1].size, beamWidth)) {
          val parent = nodes[depth-1].poll()

          Data.mino[minoId].forEachIndexed { state, _ ->
            val boundary = Data.boundary[minoId][state]
            (-boundary.xMin .. 9-boundary.xMax).forEach { x ->
              val candidateField = parent.field.cloneDeep()
              val y = candidateField.findDropY(minoId, state, x)
              val success = candidateField.setMino(minoId, state, x, y)
              val lines = candidateField.eraseLine()
              val evalScore = candidateField.evaluate(params)
              if (success) {
                nodes[depth].add(Node(parent, x, y, state, candidateField, evalScore, parent.gameScore + Data.score[lines]))
              }
            }
          }
        }
      }*/

      //println(nodes[maxDepth].size)
      val maxNode =
          if (useGameScore)
            nodes[maxDepth].maxBy { it.gameScore }
          else
            nodes[maxDepth].maxBy { it.evalScore }

      /*var maxNode: Node? = null
      var maxScore = 0
      while (!nodes[maxDepth].isEmpty()) {
        val node = nodes[maxDepth].poll()
        if (maxScore < node.gameScore) {
          maxNode = node
          maxScore = node.gameScore
        }
      }*/

      if (maxNode == null) return InstanceResultBeam(false, score, this)
      var chooseNode_ = maxNode
      repeat(maxDepth - 1) { chooseNode_ = chooseNode_?.parent ?: throw IllegalStateException("Null parent") }
      val chooseNode = chooseNode_ ?: throw IllegalStateException("Null chosen node")

      // Set mino on the best position; calculate score
      field.setMino(searchMinoIds[0], chooseNode.state, chooseNode.x, chooseNode.y)
      val lines = field.eraseLine()
      score += Data.score[lines]

      // Output status if FileWriter is given
      /*writer?.let {
        it.write("Mino: ${Data.minoName[searchMinoIds[0]]}, Score: ${score}, Field: ${chooseNode.evalScore}\n")
        it.write(field.prettify())
        it.write("\n")
      }*/
      history.add(HistoryBeam(field.cloneDeep(), chooseNode.evalScore, score))
    }
    // When all minoes are placed
    return InstanceResultBeam(true, score, this)
  }
}

data class Node(val parent: Node?, val x: Int, val y: Int, val state: Int, val field: Field, val evalScore: Double, val gameScore: Int) {
  //var children: List<Node> = emptyList()
}

data class HistoryBeam(val field: Field, val evalScore: Double, val gameScore: Int)