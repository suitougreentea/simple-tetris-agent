package tetris

import kotlin.math.*

typealias Field = Array<Array<Boolean>>

fun emptyField() = Field(20) { Array(10) { false } }
fun Field.cloneDeep() = Field(20) { iy -> Array(10) { ix -> this[iy][ix] } }
fun Field.prettify() = this.reversedArray().map { it.map { if (it) "[]" else ".." }.joinToString("") }.joinToString("\n")

// Evaluate state of the field
fun Field.evaluate(params: EvalParams): Double {
  // Highest position of block placed for each column
  val heightData = Array(10) { 0 }
  // Used to calculate number of holes
  val holesData = Array(10) { 0 }
  // Number of holes
  var hole = 0
  // Parity (see text for further detail)
  var parity = 0

  // Traverse field from bottom
  for (iy in (0..19)) {
    for (ix in (0..9)) {
      if (this[iy][ix]) {
        heightData[ix] = iy + 1

        // If there is a block and holesData[ix] > 0, there is a "hole"
        hole += holesData[ix]
        holesData[ix] = 0

        parity += if ((ix + iy) % 2 == 0) 1 else -1
      } else {
        holesData[ix]++
      }
    }
  }

  val heightMin = heightData.min() ?: 0
  val heightMax = heightData.max() ?: 0
  val heightMean = heightData.average()
  val heightVar = heightData.map { (it - heightMean) * (it - heightMean) }.average()

  // Index of column which has the lowest height
  val heightMinIndex = heightData.indexOf(heightMin)
  var erasableLinesByIPiece = 0
  (heightMin..min(heightMin + 3, 19)).forEach { iy ->
    var success = true // Remains true if this line is erasable by inserting I piece
    (0..9).forEach { ix ->
      if (ix != heightMinIndex && !this[iy][ix]) success = false
    }
    if (success) erasableLinesByIPiece++
  }

  /* With this field:
 * ..........[][]......
 * ..........[][]..[]..
 * ..[][][]..[][]..[]..
 * ..[][][]..[][][][][]
 * ..[][][][][][][][][]
 * holeDepthData =
 * [3 0 0 0 2 0 0 2 0 2]
 */
  val holeDepthData = Array(10) { 0 }
  holeDepthData[0] = heightData[1] - heightData[0]
  (1..8).forEach { holeDepthData[it] = min(heightData[it - 1], heightData[it + 1]) - heightData[it] }
  holeDepthData[9] = heightData[8] - heightData[9]

  // Considered as bad if hole depth are equal to or more than 2
  // but one 4-depth hole is better than 2 2-depth holes
  val holeDepthPenalty = holeDepthData.filter { it >= 2 }.map { sqrt(it.toDouble()) }.sum()

  return heightMax * params.heightMaxCoeff +
      heightMean * params.heightMeanCoeff +
      heightVar * params.heightVarCoeff +
      hole * params.holeCoeff +
      holeDepthPenalty * params.holeDepthPenaltyCoeff +
      abs(parity) * params.parityCoeff +
      erasableLinesByIPiece * params.erasableLinesByIPieceCoeff
}

// Returns Y-coordinate of mino dropped with X-coordinate and rotation state specified
fun Field.findDropY(minoId: Int, minoState: Int, minoX: Int): Int {
  var minoY = 20 - Data.boundary[minoId][minoState].yMin
  while (true) {
    Data.mino[minoId][minoState].forEach {
      val x = it.x + minoX
      val y = it.y + minoY
      if (y < 0) return minoY + 1
      if (y < 20 && this[y][x]) return minoY + 1
    }
    minoY --
  }
}

// Returns true if succeeds
fun Field.setMino(minoId: Int, minoState: Int, minoX: Int, minoY: Int): Boolean {
  Data.mino[minoId][minoState].forEach {
    val x = it.x + minoX
    val y = it.y + minoY
    // If blocks are out of bounds, treated as fail
    if (x < 0 || 10 <= x) return false
    if (y < 0 || 20 <= y) return false
    this[y][x] = true
  }
  return true
}

// Erases filled lines and drop blocks above them
// Returns number of lines erased
fun Field.eraseLine(): Int {
  var lines = 0
  for (i in 19.downTo(0)) {
    if (this[i].all { it }) {
      for (jy in i..18) {
        for (jx in 0..9) {
          this[jy][jx] = this[jy + 1][jx]
        }
      }
      for (jx in 0..9) this[19][jx] = false
      lines ++
    }
  }
  return lines
}
