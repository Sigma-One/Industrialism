package sigmaone.industrialism.util

import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.Vec3d
import net.minecraft.world.RaycastContext
import net.minecraft.world.World
import sigmaone.industrialism.block.wiring.BlockEntityWireConnectorT0
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

    fun solveCatenary(a: Vec3d, b: Vec3d, wireLengthMultiplier: Float): FloatArray {
        val ax = a.x
        val ay = a.y
        val az = a.z
        val bx = b.x
        val by = b.y
        val bz = b.z
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

    fun raytraceCatenary(world: World, vertexA: Vec3d, vertexB: Vec3d, xShift: Float, yShift: Float, coef: Float, segments: Int): BlockHitResult? {
        // Calculate horizontal difference
        val hDiff = sqrt(((vertexB.x - vertexA.x).pow(2)) + ((vertexB.z - vertexA.z).pow(2)))
        // Calculate heights of each point in catenary
        val heights = calculateHeights(xShift, yShift, hDiff.toFloat(), coef, 10)

        // Calculate horizontal slope of connection
        val m = if ((vertexB.z - vertexA.z) != 0.0) {
            ((vertexB.x - vertexA.x) / (vertexB.z - vertexA.z)).toFloat()
        }
        else {
            0f
        }

        // Calculate horizontal shifts (X & Z)
        val ht = hDiff / heights.size
        var dz = (ht / (sqrt(m.pow(2) + 1))).toFloat()
        var dx = m * dz

        // Calculate angle and adjust shifts accordingly
        val angle = atan2(vertexB.x - vertexA.x, vertexB.z - vertexA.z) * (180 / PI)
        when {
            angle >= 91.0
        ||  angle <= -91.0        -> { dz = -dz; dx = -dx }
            angle in 89.0..91.0   -> { dx = dz;  dz = 0f  }
            angle in -91.0..-89.0 -> { dx = -dz; dz = 0f  }
        }

        for (i in 0 until segments-1) {
            val rayResult = world.raycast(RaycastContext(
                    Vec3d(vertexA.x + dx*i, heights[i].toDouble(), vertexA.z + dz * i),
                    Vec3d(vertexA.x + dx*(i+1), heights[i + 1].toDouble(), vertexA.z + dz * (i + 1)),
                    RaycastContext.ShapeType.OUTLINE,
                    RaycastContext.FluidHandling.NONE,
                    null
                )
            )
            if (rayResult.isInsideBlock && !(world.getBlockEntity(rayResult.blockPos) is BlockEntityWireConnectorT0)) {
                return rayResult
            }
        }
        return null
    }
}