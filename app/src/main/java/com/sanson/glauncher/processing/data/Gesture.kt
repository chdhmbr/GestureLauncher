package com.sanson.glauncher.processing.data

import java.io.Serializable

data class Gesture(val pointers: List<Pointer>) : Serializable {

    fun toPointerMap(): Map<Int, List<Coordinate>> {
        return pointers.map {
            it.id to it.coords
        }.toMap()
    }

    companion object {

        fun fromPointerMap(map: Map<Int, List<Coordinate>>): Gesture {
            val pointers = map.map {
                val id = it.key
                val coords = it.value
                Pointer(id, coords)
            }
            return Gesture(pointers)
        }
    }
}