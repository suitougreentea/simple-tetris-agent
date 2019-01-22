package tetris

// Abstraction of players with various methods
interface Instance {
  // How to mix evaluation score and game score
  companion object {
    val gameScoreFunction = { eval: Double, score: Int -> score.toDouble() }
    val evalScoreFunction = { eval: Double, score: Int -> eval }
    val mixedScoreFunction = { eval: Double, score: Int -> score + 6581 * eval }
    val mixedScoreFunctionExp = { eval: Double, score: Int -> score + Math.exp(1.396 * eval + 6.157) * 27.98 }
  }
  fun run(): InstanceResult
  fun getHistory(): List<InstanceHistory>
}

data class InstanceResult(val success: Boolean)
data class InstanceHistory(val field: Field, val evalScore: Double, val gameScore: Int, val erasedLines: Int)

