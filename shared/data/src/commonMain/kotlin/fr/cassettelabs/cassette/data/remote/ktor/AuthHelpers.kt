package fr.cassettelabs.cassette.data.remote.ktor

internal expect fun currentTimeMillis(): Long

internal fun sha256(value: String): String {
    val message = value.encodeToByteArray()
    val k = intArrayOf(
        0x428a2f98, 0x71374491, -0x4a3f0431, -0x164a245b,
        0x3956c25b, 0x59f111f1, -0x6dc07d5c, -0x54e3a12b,
        -0x27f85568, 0x12835b01, 0x243185be, 0x550c7dc3,
        0x72be5d74, -0x7f214e02, -0x6423f959, -0x3e640e8c,
        -0x1b64963f, -0x1041b87a, 0x0fc19dc6, 0x240ca1cc,
        0x2de92c6f, 0x4a7484aa, 0x5cb0a9dc, 0x76f988da,
        -0x67c1aeae, -0x57ce3993, -0x4ffcd838, -0x40a68039,
        -0x391ff40d, -0x2a586eb9, 0x06ca6351, 0x14292967,
        0x27b70a85, 0x2e1b2138, 0x4d2c6dfc, 0x53380d13,
        0x650a7354, 0x766a0abb, -0x7e3d36d2, -0x6d8dd37b,
        -0x5d40175f, -0x57e599b5, -0x3db47490, -0x3893ae5d,
        -0x2e6d17e7, -0x2966f9dc, -0x0bf1ca7b, 0x106aa070,
        0x19a4c116, 0x1e376c08, 0x2748774c, 0x34b0bcb5,
        0x391c0cb3, 0x4ed8aa4a, 0x5b9cca4f, 0x682e6ff3,
        0x748f82ee, 0x78a5636f, -0x7b3787ec, -0x7338fdf8,
        -0x6f410006, -0x5baf9315, -0x41065c09, -0x398e870e,
    )
    val bitLength = message.size.toLong() * 8
    val paddedSize = (((message.size + 8) / 64) + 1) * 64
    val padded = ByteArray(paddedSize)
    message.copyInto(padded)
    padded[message.size] = 0x80.toByte()
    for (index in 0 until 8) {
        padded[paddedSize - 1 - index] = (bitLength ushr (8 * index)).toByte()
    }

    var h0 = 0x6a09e667
    var h1 = -0x4498517b
    var h2 = 0x3c6ef372
    var h3 = -0x5ab00ac6
    var h4 = 0x510e527f
    var h5 = -0x64fa9774
    var h6 = 0x1f83d9ab
    var h7 = 0x5be0cd19

    for (chunkOffset in padded.indices step 64) {
        val w = IntArray(64)
        for (index in 0 until 16) {
            val offset = chunkOffset + index * 4
            w[index] = (padded[offset].toInt() and 0xff shl 24) or
                (padded[offset + 1].toInt() and 0xff shl 16) or
                (padded[offset + 2].toInt() and 0xff shl 8) or
                (padded[offset + 3].toInt() and 0xff)
        }
        for (index in 16 until 64) {
            val s0 = (w[index - 15] rotr 7) xor (w[index - 15] rotr 18) xor (w[index - 15] ushr 3)
            val s1 = (w[index - 2] rotr 17) xor (w[index - 2] rotr 19) xor (w[index - 2] ushr 10)
            w[index] = w[index - 16] + s0 + w[index - 7] + s1
        }

        var a = h0
        var b = h1
        var c = h2
        var d = h3
        var e = h4
        var f = h5
        var g = h6
        var h = h7

        for (index in 0 until 64) {
            val s1 = (e rotr 6) xor (e rotr 11) xor (e rotr 25)
            val ch = (e and f) xor (e.inv() and g)
            val temp1 = h + s1 + ch + k[index] + w[index]
            val s0 = (a rotr 2) xor (a rotr 13) xor (a rotr 22)
            val maj = (a and b) xor (a and c) xor (b and c)
            val temp2 = s0 + maj

            h = g
            g = f
            f = e
            e = d + temp1
            d = c
            c = b
            b = a
            a = temp1 + temp2
        }

        h0 += a
        h1 += b
        h2 += c
        h3 += d
        h4 += e
        h5 += f
        h6 += g
        h7 += h
    }

    return intArrayOf(h0, h1, h2, h3, h4, h5, h6, h7).joinToString("") { word ->
        (0 until 4).joinToString("") { index ->
            ((word ushr (24 - 8 * index)) and 0xff).toString(16).padStart(2, '0')
        }
    }
}

private infix fun Int.rotr(bits: Int): Int = (this ushr bits) or (this shl (32 - bits))

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
