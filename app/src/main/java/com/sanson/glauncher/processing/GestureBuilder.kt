package com.sanson.glauncher.processing

import android.annotation.SuppressLint
import android.util.Log
import android.view.MotionEvent
import com.sanson.glauncher.processing.data.Coordinate
import com.sanson.glauncher.processing.data.Gesture
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.subjects.PublishSubject
import java.util.*

class GestureBuilder {

    private val TAG = "GestureBuilder"
    private val WAIT_FOR_NEXT_DOWN_MILLISECONDS: Long = 250

    private val currentGesture = mutableMapOf<Int, MutableList<Coordinate>>()
    private val internalToExternalPointerIds = mutableMapOf<Int, Int>()
    private val timer = Timer()
    private var timerTask: TimerTask? = null
    private val gesturesBuilt = PublishSubject.create<Gesture>()

    @SuppressLint("CheckResult")
    fun processMotionEvents(motionEvents: Flowable<MotionEvent>) {
        motionEvents
            .forEach { ev ->
                val pointerIndex = ev.actionIndex
                val pointerId = ev.getPointerId(pointerIndex)
                val actionMasked = ev.actionMasked
                when (actionMasked) {
                    MotionEvent.ACTION_POINTER_DOWN,
                    MotionEvent.ACTION_DOWN -> {
                        timerTask?.cancel()
                        val externalId = getNextPointerId()
                        internalToExternalPointerIds[pointerId] = externalId
                        addToGesture(externalId, ev.getX(pointerIndex).toDouble(), ev.getY(pointerIndex).toDouble())
                    }
                    MotionEvent.ACTION_POINTER_UP,
                    MotionEvent.ACTION_UP -> {
                        val externalId = internalToExternalPointerIds[pointerId]
                            ?: throw Exception("Pointer ID $pointerId not found")
                        addToGesture(externalId, ev.getX(pointerIndex).toDouble(), ev.getY(pointerIndex).toDouble())
                        internalToExternalPointerIds.remove(pointerId)
                        if (actionMasked == MotionEvent.ACTION_UP) {
                            timerTask = object : TimerTask() {
                                override fun run() {
                                    val gesture = Gesture.fromPointerMap(currentGesture)
                                    gesturesBuilt.onNext(gesture)
                                    clear()
                                }
                            }
                            timer.schedule(timerTask, WAIT_FOR_NEXT_DOWN_MILLISECONDS)
                        }
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val pointerCount = ev.pointerCount
                        val historySize = ev.historySize;
                        for (h in 0 until historySize) {
                            for (p in 0 until pointerCount) {
                                val pId = ev.getPointerId(p)
                                val externalId = internalToExternalPointerIds[pId]
                                    ?: throw Exception("Pointer ID $pId not found")
                                addToGesture(
                                    externalId,
                                    ev.getHistoricalX(p, h).toDouble(),
                                    ev.getHistoricalY(p, h).toDouble()
                                )
                            }
                        }
                        for (p in 0 until pointerCount) {
                            val pId = ev.getPointerId(p)
                            val externalId = internalToExternalPointerIds[pId]
                                ?: throw Exception("Pointer ID $pId not found")
                            addToGesture(externalId, ev.getX(p).toDouble(), ev.getY(p).toDouble())
                        }
                    }
                    MotionEvent.ACTION_CANCEL -> {
                        Log.d(TAG, "GESTURE CANCELLED")
                        clear()
                    }
                }
            }
    }

    private fun getNextPointerId(): Int {
        return currentGesture.size
    }

    private fun addToGesture(pointerId: Int, xCoord: Double, yCoord: Double) {
        val coord = Coordinate(xCoord, yCoord)
        val pointerCoords = currentGesture.getOrPut(pointerId) { mutableListOf() }
        val last = pointerCoords.lastOrNull()
        if (last == coord) return
        pointerCoords.add(coord)
    }

    private fun clear() {
        timerTask?.cancel()
        timerTask = null
        currentGesture.clear()
        internalToExternalPointerIds.clear()
    }

    public fun getGesturesObserver(): Flowable<Gesture> {
        return gesturesBuilt.toFlowable(BackpressureStrategy.ERROR)
    }
}