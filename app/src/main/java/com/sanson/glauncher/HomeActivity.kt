package com.sanson.glauncher

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.sanson.glauncher.fragments.GestureDrawerFragment
import com.sanson.glauncher.io.database.GestureDBHandler
import com.sanson.glauncher.io.file.FileWriter
import com.sanson.glauncher.processing.ClosestGestureFinder
import com.sanson.glauncher.processing.GestureNormalizer
import com.sanson.glauncher.processing.data.Gesture

class HomeActivity : FragmentActivity(),
    GestureDrawerFragment.GesturePanelFragmentListener {

    private val TAG = "Main"

    private lateinit var dbHandler: GestureDBHandler
    private lateinit var appLauncher: AppLauncher
    private lateinit var gestureFinder: ClosestGestureFinder
    private lateinit var fileWriter: FileWriter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        dbHandler = GestureDBHandler(applicationContext)
        appLauncher = AppLauncher(this)
        gestureFinder = ClosestGestureFinder(dbHandler)
        fileWriter = FileWriter()
    }

    private fun launchGestureManager() {
        val intent = Intent(this, GestureManagerActivity::class.java)
        startActivity(intent)
    }

    private fun logGesture(drawn: Gesture, name: String? = null) {
        val ts = System.currentTimeMillis().toString()
        val filename = if (name == null) ts else "$name-$ts"
        fileWriter.write(drawn, getExternalFilesDir(null)!!.absolutePath, filename)
    }

    override fun onGestureDrawn(gesture: Gesture) {
        val normalizer = GestureNormalizer()
        val normalized = normalizer.normalize(gesture)
        if (gestureFinder.isLaunchGestureManagerGesture(normalized)) {
            launchGestureManager()
            return
        }
        val closest = gestureFinder.closestGesture(normalized)
        logGesture(gesture, closest?.packageName ?: "unidentified")
        if (closest == null) {
            Toast.makeText(applicationContext, "Not found", Toast.LENGTH_SHORT).show()
            return
        }
        appLauncher.launch(closest)
    }
}
