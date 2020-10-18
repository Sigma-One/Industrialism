package sigmaone.industrialism.util

import kotlin.math.*

object CatenaryHelper {
    // Numerical Analysis by Benjamin Peyrille

    val EPSILON = 0.001

    private fun solveTranslation(A: FloatArray, B: FloatArray, coef: Float): FloatArray {
        var x: FloatArray = floatArrayOf(1f, 1f)
        var fx: FloatArray = floatArrayOf(
                coef * cosh((A[0]-x[1])/coef) + x[0] - A[1],
                coef * cosh((B[0]-x[1])/coef) + x[0] - B[1]
        )
        var nfx = abs(fx[0]) + abs(fx[1])

        while (nfx > EPSILON) {
            val mb: Float = -1 * sinh((A[0] - x[1])/coef)
            val md: Float = -1 * sinh((B[0] - x[1])/coef)
            val det: Float = 1 / (md - mb)

            x = floatArrayOf(x[0] - det * (md * fx[0] - mb * fx[1]),
                    x[1] - det * (fx[1] - fx[0]))
            fx = floatArrayOf(
                    coef * cosh((A[0]-x[1])/coef) + x[0] - A[1],
                    coef * cosh((B[0]-x[1])/coef) + x[0] - B[1]
            )
            nfx = abs(fx[0]) + abs(fx[1])
        }

        return x
    }

    private fun solveShape(hDiff: Float, vDiff: Float, length: Float, guess: Float): Float {
        var x: Float = guess
        var fx: Float = sqrt(
                length*length - vDiff*vDiff
        ) - 2*x * sinh(hDiff/(2*x))

        while (abs(fx) > EPSILON) {
            x = x - (fx / (hDiff/x * cosh(hDiff/(2*x)) - 2*sinh(hDiff/(2*x))))
            fx = sqrt(
                    length*length - vDiff*vDiff
            ) - 2*x * sinh(hDiff/(2*x))
        }

        return abs(x)
    }

    fun catenaryCoefs(
            ax: Float, ay: Float, az: Float,
            bx: Float, by: Float, bz: Float,
            wireLength: Float
    ): FloatArray {
        val hDiff = sqrt(
                (bx - ax).pow(2) + (bz-az).pow(2)
        )
        val vDiff = by - ay

        val A = floatArrayOf(0f, ay)
        val B = floatArrayOf(hDiff, by)

        var shapeGuess = 0.25f
        var shapeIterations = 0
        var shapeCoef: Float = solveShape(hDiff, vDiff, wireLength, shapeGuess)
        while (!shapeCoef.isFinite() && shapeIterations < 5) {
            shapeIterations++
            if (shapeCoef.isNaN()) {
                shapeGuess = shapeGuess * 2
            }
            else {
                shapeGuess = shapeGuess / 2
            }
            shapeCoef = solveShape(hDiff, vDiff, wireLength, shapeGuess)
        }
        // TODO: check if not valid; if not valid, safe procedure plz

        val yxTrans = solveTranslation(A, B, shapeCoef)
        val xTrans = yxTrans[1]
        val yTrans = yxTrans[0]

        return floatArrayOf(xTrans, yTrans, shapeCoef)
    }
}