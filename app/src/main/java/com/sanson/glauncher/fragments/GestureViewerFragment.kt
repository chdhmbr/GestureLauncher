package com.sanson.glauncher.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.sanson.glauncher.GestureCanvas

import com.sanson.glauncher.R
import com.sanson.glauncher.processing.data.Gesture

class GestureViewerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_gesture_viewer, container, false)
    }

    fun display(appName: String, gesture: Gesture) {
        view?.findViewById<TextView>(R.id.txtAppName)?.text = appName
        drawGesture(gesture)
    }

    private fun drawGesture(gesture: Gesture) {
        val imageView = view?.findViewById<ImageView>(R.id.imageView)
        val drawnGesture = GestureCanvas(gesture)
        imageView?.setImageDrawable(drawnGesture)
    }
}
