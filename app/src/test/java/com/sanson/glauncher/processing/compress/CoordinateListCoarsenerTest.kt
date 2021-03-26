package com.sanson.glauncher.processing.compress

import com.sanson.glauncher.processing.data.Coordinate
import org.junit.Test

import org.junit.Assert.*

class CoordinateListCoarsenerTest {

    @Test
    fun coarsen() {
        val coarsener = CoordinateListCoarsener(3)
        val coords = listOf(
            Coordinate(1.0, 0.0),
            Coordinate(3.0, 6.0),
            Coordinate(5.0, 6.0)
        )
        val res = coarsener.coarsen(coords)
        val expected = listOf(
            listOf(
                Coordinate(2.0, 3.0),
                Coordinate(5.0, 6.0)
            ),
            coords
        )
        assertEquals(expected, res)
    }
}