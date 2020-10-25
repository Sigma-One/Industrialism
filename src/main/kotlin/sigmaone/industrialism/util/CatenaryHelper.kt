package sigmaone.industrialism.util

import net.minecraft.util.math.BlockPos
import kotlin.math.*

object CatenaryHelper {
    // Numerical Analysis by Benjamin Peyrille

    private val EPSILON = 0.001

    private fun solveTranslation(A: DoubleArray, B: DoubleArray, coef: Double): DoubleArray {
        var x: DoubleArray = doubleArrayOf(1.0, 1.0)
        var fx: DoubleArray = doubleArrayOf(
                coef * cosh((A[0]-x[1])/coef) + x[0] - A[1],
                coef * cosh((B[0]-x[1])/coef) + x[0] - B[1]
        )
        var nfx = abs(fx[0]) + abs(fx[1])

        while (nfx > EPSILON) {
            val mb = -1 * sinh((A[0] - x[1])/coef)
            val md = -1 * sinh((B[0] - x[1])/coef)
            val det = 1 / (md - mb)

            x = doubleArrayOf(x[0] - det * (md * fx[0] - mb * fx[1]),
                    x[1] - det * (fx[1] - fx[0]))
            fx = doubleArrayOf(
                    coef * cosh((A[0]-x[1])/coef) + x[0] - A[1],
                    coef * cosh((B[0]-x[1])/coef) + x[0] - B[1]
            )
            nfx = abs(fx[0]) + abs(fx[1])
        }

        return x
    }

    private fun solveShape(hDiff: Double, vDiff: Double, length: Double, guess: Double): Double {
        var x: Double = guess
        var fx: Double = sqrt(
                length*length - vDiff*vDiff
        ) - 2*x * sinh(hDiff/(2*x))

        var lowerBound = 0.0
        var upperBound = Double.POSITIVE_INFINITY
        while (abs(fx) > EPSILON) {
            if (fx == Double.NEGATIVE_INFINITY) {
                lowerBound = abs(x)
                if (upperBound == Double.POSITIVE_INFINITY) {
                    x *= 2
                }
                else {
                    x = (upperBound + lowerBound) / 2
                }
            }
            else if (fx > 0) {
                upperBound = abs(x)
                x = (upperBound + lowerBound) / 2
            }
            else {
                x -= (fx / (hDiff / x * cosh(hDiff / (2 * x)) - 2 * sinh(hDiff / (2 * x))))
            }
            fx = sqrt(
                    length*length - vDiff*vDiff
            ) - 2*x * sinh(hDiff/(2*x))
        }

        return abs(x)
    }

    fun solveCatenary(a: BlockPos, b: BlockPos, wireLengthMultiplier: Float): FloatArray {
        val ax = a.x.toDouble()
        val ay = a.y.toDouble()
        val az = a.z.toDouble()
        val bx = b.x.toDouble()
        val by = b.y.toDouble()
        val bz = b.z.toDouble()
        val hDiff = sqrt(
                (bx - ax).pow(2) + (bz-az).pow(2)
        )
        val vDiff = by - ay
        val diff = sqrt(vDiff.pow(2) + hDiff.pow(2))
        val wireLength = diff * wireLengthMultiplier

        val A = doubleArrayOf(0.0, ay)
        val B = doubleArrayOf(hDiff, by)

        val shapeGuess = 3.0
        val shapeCoef: Double = solveShape(hDiff, vDiff, wireLength, shapeGuess)

        // TODO: check if not valid; if not valid, safe procedure plz

        val yxTrans = solveTranslation(A, B, shapeCoef)
        val xTrans = yxTrans[1]
        val yTrans = yxTrans[0]

        return floatArrayOf(xTrans.toFloat(), yTrans.toFloat(), shapeCoef.toFloat())
    }

    fun calculateHeights(xShift: Float, yShift: Float, hDiff: Float, coef: Float, segments: Int): FloatArray {
        val heights = FloatArray(segments)
        for (i in 0 until segments) {
            val y = coef * cosh((((hDiff/segments)*i) - xShift) / coef) + yShift
            heights[i] = y
        }
        return heights
    }
}