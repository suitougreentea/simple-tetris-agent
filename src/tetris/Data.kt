package tetris

data class Pos(val x: Int, val y: Int)
data class Boundary(val xMin: Int, val xMax: Int, val yMin: Int, val yMax: Int)

object Data {
  // All types/states of tetriminoes
  val mino = arrayOf(
      arrayOf( // I
          arrayOf(Pos(-1, 0), Pos(0, 0), Pos(1, 0), Pos(2, 0)), // horizontal
          arrayOf(Pos(0, -1), Pos(0, 0), Pos(0, 1), Pos(0, 2))  // vertical
      ),
      arrayOf( // J
          arrayOf(Pos(-1, 0), Pos(0, 0), Pos(1, 0), Pos(-1, 1)), // |_
          arrayOf(Pos(0, 1), Pos(0, 0), Pos(0, -1), Pos(1, 1)),  // |~
          arrayOf(Pos(1, 0), Pos(0, 0), Pos(-1, 0), Pos(1, -1)), // ~|
          arrayOf(Pos(0, -1), Pos(0, 0), Pos(0, 1), Pos(-1, -1)) // _|
      ),
      arrayOf( // L
          arrayOf(Pos(-1, 0), Pos(0, 0), Pos(1, 0), Pos(1, 1)),   // _|
          arrayOf(Pos(0, 1), Pos(0, 0), Pos(0, -1), Pos(1, -1)),  // |_
          arrayOf(Pos(1, 0), Pos(0, 0), Pos(-1, 0), Pos(-1, -1)), // |~
          arrayOf(Pos(0, -1), Pos(0, 0), Pos(0, 1), Pos(-1, 1))   // ~|
      ),
      arrayOf( // O
          arrayOf(Pos(0, 0), Pos(0, 1), Pos(1, 0), Pos(1, 1))
      ),
      arrayOf( // S
          arrayOf(Pos(-1, 0), Pos(0, 0), Pos(0, 1), Pos(1, 1)),  // horizontal
          arrayOf(Pos(0, -1), Pos(0, 0), Pos(-1, 0), Pos(-1, 1)) // vertical
      ),
      arrayOf( // T
          arrayOf(Pos(-1, 0), Pos(0, 0), Pos(1, 0), Pos(0, 1)),  // _|_
          arrayOf(Pos(0, 1), Pos(0, 0), Pos(0, -1), Pos(1, 0)),  // |-
          arrayOf(Pos(1, 0), Pos(0, 0), Pos(-1, 0), Pos(0, -1)), // ~|~
          arrayOf(Pos(0, -1), Pos(0, 0), Pos(0, 1), Pos(-1, 0))  // -|
      ),
      arrayOf( // Z
          arrayOf(Pos(1, 0), Pos(0, 0), Pos(0, 1), Pos(-1, 1)), // horizontal
          arrayOf(Pos(0, 1), Pos(0, 0), Pos(-1, 0), Pos(-1, -1)) // vertical
      )
  )

  val minoName = arrayOf("I", "J", "L", "O", "S", "T", "Z")

  // Boundaries for all types/states of tetriminoes
  val boundary = mino.map {
    it.map {
      val x = it.map { it.x }
      val y = it.map { it.y }
      Boundary(x.min() ?: 0, x.max() ?: 0, y.min() ?: 0, y.max() ?: 0)
    }
  }

  // Score increments for each lines
  //val score = arrayOf(0, 40, 100, 300, 1200)
  val score = arrayOf(0, 1, 2, 3, 4)
}