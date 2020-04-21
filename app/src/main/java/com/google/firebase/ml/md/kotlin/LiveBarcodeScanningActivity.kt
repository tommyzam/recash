/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.firebase.ml.md.kotlin

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Intent
import android.hardware.Camera
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.libraries.barhopper.Barcode
import com.google.android.material.chip.Chip
import com.google.common.base.Objects
import com.google.firebase.database.*
import com.google.firebase.ml.md.R
import com.google.firebase.ml.md.kotlin.barcodedetection.BarcodeProcessor
import com.google.firebase.ml.md.kotlin.barcodedetection.BarcodeResultFragment
import com.google.firebase.ml.md.kotlin.barcodedetection.UserBarcodeField
import com.google.firebase.ml.md.kotlin.camera.CameraSource
import com.google.firebase.ml.md.kotlin.camera.CameraSourcePreview
import com.google.firebase.ml.md.kotlin.camera.GraphicOverlay
import com.google.firebase.ml.md.kotlin.camera.WorkflowModel
import com.google.firebase.ml.md.kotlin.camera.WorkflowModel.WorkflowState
import com.google.firebase.ml.md.kotlin.settings.SettingsActivity
import com.google.firebase.ml.md.kotlin.utils.MySharedPref.Companion.getInstance
import java.io.IOException

/** Demonstrates the barcode scanning workflow using camera preview.  */
class LiveBarcodeScanningActivity : AppCompatActivity(), OnClickListener {

    private var mCurrentKey: String? = null
    private lateinit var userId: String
    private var mUserbarCode: UserBarcodeField? = null
    private var cameraSource: CameraSource? = null
    private var preview: CameraSourcePreview? = null
    private var graphicOverlay: GraphicOverlay? = null
    private var settingsButton: View? = null
    private var flashButton: View? = null
    private var promptChip: Chip? = null
    private var promptChipAnimator: AnimatorSet? = null
    private var workflowModel: WorkflowModel? = null
    private var currentWorkflowState: WorkflowState? = null

    private var databaseReference: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_live_barcode_kotlin)
        preview = findViewById(R.id.camera_preview)
        graphicOverlay = findViewById<GraphicOverlay>(R.id.camera_preview_graphic_overlay).apply {
            setOnClickListener(this@LiveBarcodeScanningActivity)
            cameraSource = CameraSource(this)
        }

        promptChip = findViewById(R.id.bottom_prompt_chip)
        promptChipAnimator =
                (AnimatorInflater.loadAnimator(this, R.animator.bottom_prompt_chip_enter) as AnimatorSet).apply {
                    setTarget(promptChip)
                }

        findViewById<View>(R.id.close_button).setOnClickListener(this)
        flashButton = findViewById<View>(R.id.flash_button).apply {
            setOnClickListener(this@LiveBarcodeScanningActivity)
        }
        settingsButton = findViewById<View>(R.id.settings_button).apply {
            setOnClickListener(this@LiveBarcodeScanningActivity)
        }

        databaseReference = FirebaseDatabase.getInstance().reference.child(BARCODE)
        userId = getInstance(this)!!.userId!!

        databaseReference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {

                    for (barcode in dataSnapshot.children) { // do with your result
                        val userBarcodeField: UserBarcodeField = barcode.getValue(UserBarcodeField::class.java)!!
                        if (userBarcodeField.userId == userId) {
                            mUserbarCode = userBarcodeField
                            mCurrentKey = barcode.key
                            break
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })


        setUpWorkflowModel()
    }

    override fun onResume() {
        super.onResume()

        workflowModel?.markCameraFrozen()
        settingsButton?.isEnabled = true
        currentWorkflowState = WorkflowState.NOT_STARTED
        cameraSource?.setFrameProcessor(BarcodeProcessor(graphicOverlay!!, workflowModel!!))
        workflowModel?.setWorkflowState(WorkflowState.DETECTING)
    }

    override fun onPostResume() {
        super.onPostResume()
        BarcodeResultFragment.dismiss(supportFragmentManager)
    }

    override fun onPause() {
        super.onPause()
        currentWorkflowState = WorkflowState.NOT_STARTED
        stopCameraPreview()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraSource?.release()
        cameraSource = null
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.close_button -> onBackPressed()
            R.id.flash_button -> {
                flashButton?.let {
                    if (it.isSelected) {
                        it.isSelected = false
                        cameraSource?.updateFlashMode(Camera.Parameters.FLASH_MODE_OFF)
                    } else {
                        it.isSelected = true
                        cameraSource!!.updateFlashMode(Camera.Parameters.FLASH_MODE_TORCH)
                    }
                }
            }
            R.id.settings_button -> {
                settingsButton?.isEnabled = false
                startActivity(Intent(this, SettingsActivity::class.java))
            }
        }
    }

    private fun startCameraPreview() {
        val workflowModel = this.workflowModel ?: return
        val cameraSource = this.cameraSource ?: return
        if (!workflowModel.isCameraLive) {
            try {
                workflowModel.markCameraLive()
                preview?.start(cameraSource)
            } catch (e: IOException) {
                Log.e(TAG, "Failed to start camera preview!", e)
                cameraSource.release()
                this.cameraSource = null
            }
        }
    }

    private fun stopCameraPreview() {
        val workflowModel = this.workflowModel ?: return
        if (workflowModel.isCameraLive) {
            workflowModel.markCameraFrozen()
            flashButton?.isSelected = false
            preview?.stop()
        }
    }

    private fun setUpWorkflowModel() {
        workflowModel = ViewModelProviders.of(this).get(WorkflowModel::class.java)

        // Observes the workflow state changes, if happens, update the overlay view indicators and
        // camera preview state.
        workflowModel!!.workflowState.observe(this, Observer { workflowState ->
            if (workflowState == null || Objects.equal(currentWorkflowState, workflowState)) {
                return@Observer
            }

            currentWorkflowState = workflowState
            Log.d(TAG, "Current workflow state: ${currentWorkflowState!!.name}")

            val wasPromptChipGone = promptChip?.visibility == View.GONE

            when (workflowState) {
                WorkflowState.DETECTING -> {
                    promptChip?.visibility = View.VISIBLE
                    promptChip?.setText(R.string.prompt_point_at_a_barcode)
                    startCameraPreview()
                }
                WorkflowState.CONFIRMING -> {
                    promptChip?.visibility = View.VISIBLE
                    promptChip?.setText(R.string.prompt_move_camera_closer)
                    startCameraPreview()
                }
                WorkflowState.SEARCHING -> {
                    promptChip?.visibility = View.VISIBLE
                    promptChip?.setText(R.string.prompt_searching)
                    stopCameraPreview()
                }
                WorkflowState.DETECTED, WorkflowState.SEARCHED -> {
                    promptChip?.visibility = View.GONE
                    stopCameraPreview()
                }
                else -> promptChip?.visibility = View.GONE
            }

            val shouldPlayPromptChipEnteringAnimation = wasPromptChipGone && promptChip?.visibility == View.VISIBLE
            promptChipAnimator?.let {
                if (shouldPlayPromptChipEnteringAnimation && !it.isRunning) it.start()
            }
        })

        workflowModel?.detectedBarcode?.observe(this, Observer { barcode ->
            if (barcode != null) {
                val value: Int = barcode!!.rawValue!!.toInt()
                if (mUserbarCode == null) {
                    pushToFirebase(value)
                } else {
                    updateData(value);
                }
            }
        })

    }

    private fun updateData(value: Int) {
        mUserbarCode!!.total += value

        val query = databaseReference!!.child(mCurrentKey!!).child("total").setValue(mUserbarCode!!.total)
        query.addOnSuccessListener { aVoid: Void? ->
            Toast.makeText(this@LiveBarcodeScanningActivity, "Data Updated successfully !", Toast.LENGTH_LONG).show()
        }
                .addOnFailureListener { e: Exception ->
                    Toast.makeText(this@LiveBarcodeScanningActivity, "request Fail", Toast.LENGTH_LONG).show()
                }

        val arrayQuery = databaseReference!!.child(mCurrentKey!!).child("barcodes").push()
        val barcode = UserBarcodeField.Barcode()
        barcode.value = value
        arrayQuery.setValue(barcode).addOnSuccessListener { aVoid: Void? ->
            Toast.makeText(this@LiveBarcodeScanningActivity, "Data Updated successfully !", Toast.LENGTH_LONG).show()
        }
                .addOnFailureListener { e: Exception ->
                    Toast.makeText(this@LiveBarcodeScanningActivity, "request Fail", Toast.LENGTH_LONG).show()
                }


    }

    private fun pushToFirebase(value: Int) {

        val ref = databaseReference!!.push()
        mCurrentKey = ref.key

        val barcodes: HashMap<String, UserBarcodeField.Barcode> = HashMap()

        mUserbarCode = UserBarcodeField(value, userId, barcodes)
        ref.setValue(mUserbarCode).addOnSuccessListener { aVoid: Void? ->
            val reference = databaseReference!!.child(mCurrentKey!!).child("barcodes").push()
            val barcode = UserBarcodeField.Barcode()
            barcode.value = value
            reference.setValue(barcode).addOnSuccessListener {
                Toast.makeText(this@LiveBarcodeScanningActivity, "Data pushed successfully !", Toast.LENGTH_LONG).show()
            }
        }
                .addOnFailureListener { e: Exception ->
                    Toast.makeText(this@LiveBarcodeScanningActivity, "request Fail", Toast.LENGTH_LONG).show()
                }
    }

    companion object {
        private const val TAG = "LiveBarcodeActivity"
        val BARCODE = "barCodes"

    }
}
