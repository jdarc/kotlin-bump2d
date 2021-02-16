import kotlin.math.floor

object Color {

    fun red(argb: Int) = argb shr 16 and 255

    fun grn(argb: Int) = argb shr 8 and 255

    fun blu(argb: Int) = argb and 255

    fun argb(red: Double, grn: Double, blu: Double): Int {
        val r = floor(red).toInt().coerceIn(0, 255)
        val g = floor(grn).toInt().coerceIn(0, 255)
        val b = floor(blu).toInt().coerceIn(0, 255)
        return 255 shl 24 or (r shl 16) or (g shl 8) or b
    }

    fun gray(argb: Int) = 0.2126 * red(argb) / 255.0 + 0.7152 * grn(argb) / 255.0 + 0.0722 * blu(argb) / 255.0
}
