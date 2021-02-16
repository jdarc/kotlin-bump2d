import kotlin.math.sqrt

data class Vector3(val x: Double, val y: Double, val z: Double) {

    val length get() = sqrt(Math.fma(x, x, Math.fma(y, y, z * z)))

    companion object {
        fun normalize(v: Vector3): Vector3 {
            val invLen = 1.0 / v.length
            return Vector3(v.x * invLen, v.y * invLen, v.z * invLen)
        }

        fun dotProduct(a: Vector3, b: Vector3) = Math.fma(a.x, b.x, Math.fma(a.y, b.y, a.z * b.z))
    }
}
