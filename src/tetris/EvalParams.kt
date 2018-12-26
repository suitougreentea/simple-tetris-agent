package tetris

import java.util.*

// Parameters for evaluation function (= genes)
// For meaning of each parameter, see Instance::evaluate()
data class EvalParams(
    val heightMaxCoeff: Double,
    val heightMeanCoeff: Double,
    val heightVarCoeff: Double,
    val holeCoeff: Double,
    val holeDepthPenaltyCoeff: Double,
    val parityCoeff: Double,
    val erasableLinesByIPieceCoeff: Double
)

// Generate parameters randomly
fun EvalParamsRandom(random: Random) = EvalParams(
    random.nextDouble() / -20.0,
    random.nextDouble() / -20.0,
    random.nextDouble() / -10.0,
    random.nextDouble() / -20.0,
    random.nextDouble() / -5.0,
    random.nextDouble() / -10.0,
    random.nextDouble() / 4.0
)

// For debug purpose
fun EvalParamsManualGood() = EvalParams(-0.7, -0.4, -0.05, -2.0, -0.7, -0.5, 1.0)

// Blend all parameters
fun blend(a: EvalParams, b: EvalParams, r: Random) = EvalParams(
    blxAlpha(a.heightMaxCoeff,              b.heightMaxCoeff,               r),
    blxAlpha(a.heightMeanCoeff,             b.heightMeanCoeff,              r),
    blxAlpha(a.heightVarCoeff,              b.heightVarCoeff,               r),
    blxAlpha(a.holeCoeff,                    b.holeCoeff,                     r),
    blxAlpha(a.holeDepthPenaltyCoeff,      b.holeDepthPenaltyCoeff,       r),
    blxAlpha(a.parityCoeff,                  b.parityCoeff,                  r),
    blxAlpha(a.erasableLinesByIPieceCoeff, b.erasableLinesByIPieceCoeff, r)
)

// Blend each parameter using BLX-alpha method
// Alpha = 0.5
fun blxAlpha(a: Double, b: Double, random: Random): Double {
  return a + (b - a) * (random.nextDouble() * 2.0 - 0.5)
}
