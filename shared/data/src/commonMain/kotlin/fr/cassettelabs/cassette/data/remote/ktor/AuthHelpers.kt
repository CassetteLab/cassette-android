package fr.cassettelabs.cassette.data.remote.ktor

internal expect fun currentTimeMillis(): Long

internal fun md5(value: String): String {
    val message = value.encodeToByteArray()
    val bitLength = message.size.toLong() * 8
    val paddedSize = (((message.size + 8) / 64) + 1) * 64
    val padded = ByteArray(paddedSize)
    message.copyInto(padded)
    padded[message.size] = 0x80.toByte()
    for (index in 0 until 8) {
        padded[paddedSize - 8 + index] = (bitLength ushr (8 * index)).toByte()
    }

    var a0 = 0x67452301
    var b0 = -0x10325477
    var c0 = -0x67452302
    var d0 = 0x10325476

    val shifts = intArrayOf(
        7, 12, 17, 22, 7, 12, 17, 22, 7, 12, 17, 22, 7, 12, 17, 22,
        5, 9, 14, 20, 5, 9, 14, 20, 5, 9, 14, 20, 5, 9, 14, 20,
        4, 11, 16, 23, 4, 11, 16, 23, 4, 11, 16, 23, 4, 11, 16, 23,
        6, 10, 15, 21, 6, 10, 15, 21, 6, 10, 15, 21, 6, 10, 15, 21,
    )
    val constants = IntArray(64) { index ->
        (kotlin.math.abs(kotlin.math.sin(index + 1.0)) * 4294967296.0).toLong().toInt()
    }

    for (chunkOffset in padded.indices step 64) {
        val words = IntArray(16) { index ->
            val offset = chunkOffset + index * 4
            (padded[offset].toInt() and 0xff) or
                ((padded[offset + 1].toInt() and 0xff) shl 8) or
                ((padded[offset + 2].toInt() and 0xff) shl 16) or
                ((padded[offset + 3].toInt() and 0xff) shl 24)
        }
        var a = a0
        var b = b0
        var c = c0
        var d = d0

        for (index in 0 until 64) {
            val f: Int
            val g: Int
            when (index) {
                in 0..15 -> {
                    f = (b and c) or (b.inv() and d)
                    g = index
                }
                in 16..31 -> {
                    f = (d and b) or (d.inv() and c)
                    g = (5 * index + 1) % 16
                }
                in 32..47 -> {
                    f = b xor c xor d
                    g = (3 * index + 5) % 16
                }
                else -> {
                    f = c xor (b or d.inv())
                    g = (7 * index) % 16
                }
            }
            val temp = d
            d = c
            c = b
            b += (a + f + constants[index] + words[g]).rotateLeft(shifts[index])
            a = temp
        }

        a0 += a
        b0 += b
        c0 += c
        d0 += d
    }

    return intArrayOf(a0, b0, c0, d0).joinToString("") { word ->
        (0 until 4).joinToString("") { index ->
            ((word ushr (8 * index)) and 0xff).toString(16).padStart(2, '0')
        }
    }
}
