package com.example.routetracker.data.remote

/**
 * Lector mínimo del formato binario de Protocol Buffers.
 * Solo entiende lo necesario para leer un feed GTFS-Realtime,
 * sin depender de ninguna librería externa de protobuf.
 */
class ProtoReader(private val data: ByteArray, private var pos: Int = 0, private val end: Int = data.size) {

    fun hasNext(): Boolean = pos < end

    private fun readByte(): Int = data[pos++].toInt() and 0xFF

    fun readVarint(): Long {
        var result = 0L
        var shift = 0
        while (true) {
            val b = readByte()
            result = result or ((b.toLong() and 0x7F) shl shift)
            if (b and 0x80 == 0) break
            shift += 7
        }
        return result
    }

    private fun readTag(): Pair<Int, Int> {
        val tag = readVarint()
        return (tag shr 3).toInt() to (tag and 0x7).toInt()
    }

    private fun readLengthDelimited(): ByteArray {
        val len = readVarint().toInt()
        val bytes = data.copyOfRange(pos, pos + len)
        pos += len
        return bytes
    }

    private fun skip(wireType: Int) {
        when (wireType) {
            0 -> readVarint()
            1 -> pos += 8
            2 -> pos += readVarint().toInt()
            5 -> pos += 4
        }
    }

    /** Devuelve, para un número de campo dado, todas sus apariciones como bytes (mensajes/strings) o como número (varint). */
    fun readAllFields(): Map<Int, MutableList<Any>> {
        val fields = mutableMapOf<Int, MutableList<Any>>()
        while (hasNext()) {
            val (fieldNumber, wireType) = readTag()
            val value: Any = when (wireType) {
                0 -> readVarint()
                2 -> readLengthDelimited()
                1 -> { pos += 8; continue }
                5 -> { pos += 4; continue }
                else -> continue
            }
            fields.getOrPut(fieldNumber) { mutableListOf() }.add(value)
        }
        return fields
    }
}

fun ByteArray.asProtoText(): String = String(this, Charsets.UTF_8)