import java.awt.Dimension
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionListener
import java.awt.image.BufferedImage
import java.awt.image.DataBufferInt
import java.awt.image.PixelGrabber
import java.util.concurrent.Executors
import java.util.concurrent.ForkJoinPool
import javax.imageio.ImageIO
import javax.swing.JPanel
import kotlin.math.pow

class DemoPanel : JPanel() {
    private val picImage = ImageIO.read(DemoPanel::class.java.classLoader.getResource("cherries.jpg"))
    private val image = BufferedImage(picImage.width, picImage.height, BufferedImage.TYPE_INT_ARGB_PRE)
    private val srcPixels = grabPixels(picImage)
    private val dstPixels = (image.raster.dataBuffer as DataBufferInt).data
    private var light = Vector3(0.0, 256.0, 0.0)
    private var lastY = 0

    @Override
    override fun paintComponent(g: Graphics) {
        ForkJoinPool.commonPool().invokeAll((0 until image.height).map {
            Executors.callable {
                for (x in 0 until image.width) {
                    val intensity = computeIntensity(x, it, image.width, image.height)
                    val specular = 255.0 * intensity.pow(15.0)
                    val rgb = srcPixels[it * image.width + x]
                    val red = specular + Color.red(rgb) * intensity
                    val grn = specular + Color.grn(rgb) * intensity
                    val blu = specular + Color.blu(rgb) * intensity
                    dstPixels[it * image.width + x] = Color.argb(red, grn, blu)
                }
            }
        })
        (g as Graphics2D).drawImage(image, null, 0, 0)
    }

    private fun computeIntensity(sx: Int, sy: Int, width: Int, height: Int): Double {
        val gl = Color.gray(srcPixels[sy * width + (sx - 1).coerceIn(0, width - 1)])
        val gr = Color.gray(srcPixels[sy * width + (sx + 1).coerceIn(0, width - 1)])
        val gu = Color.gray(srcPixels[(sy - 1).coerceIn(0, height - 1) * width + sx])
        val gd = Color.gray(srcPixels[(sy + 1).coerceIn(0, height - 1) * width + sx])
        val normal = Vector3.normalize(Vector3(256.0 * (gl - gr), 25.0, 256.0 * (gu - gd)))
        val toLight = Vector3.normalize(Vector3(light.x + width / 2.0 - sx, light.y, light.z + height / 2.0 - sy))
        return Vector3.dotProduct(normal, toLight)
    }

    private fun grabPixels(image: BufferedImage): IntArray {
        val pixels = IntArray(image.width * image.height)
        PixelGrabber(image, 0, 0, image.width, image.height, pixels, 0, image.width).grabPixels()
        return pixels
    }

    init {
        size = Dimension(image.width, image.height)
        preferredSize = size

        addMouseMotionListener(object : MouseMotionListener {
            override fun mouseDragged(e: MouseEvent) {
                val ny = if (e.y < lastY) light.y - 10.0 else light.y + 10.0
                lastY = e.y
                light = Vector3(light.x, ny.coerceIn(30.0, 800.0), light.z)
                repaint()
            }

            override fun mouseMoved(e: MouseEvent) {
                light = Vector3(e.x - width / 2.0, light.y, e.y - height / 2.0)
                repaint()
            }
        })
    }
}
