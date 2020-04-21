package com.google.firebase.ml.md.kotlin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.google.firebase.ml.md.kotlin.LiveBarcodeScanningActivity.Companion.BARCODE
import com.google.firebase.ml.md.kotlin.barcodedetection.UserBarcodeField
import com.google.firebase.ml.md.kotlin.utils.MySharedPref
import kotlinx.android.synthetic.main.activity_history.*
import java.util.*
import com.google.firebase.ml.md.R
import kotlinx.android.parcel.RawValue
import kotlin.collections.ArrayList


class History : AppCompatActivity() {
    private var adapter: HistoryAdapter? = null
    private var mRequests: ArrayList<UserBarcodeField.Barcode> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        adapter = HistoryAdapter()

        rc_barcode.layoutManager = LinearLayoutManager(this@History)
        rc_barcode.adapter = adapter


        val reference = FirebaseDatabase.getInstance().reference
        val userId: String = MySharedPref.getInstance(this)!!.userId!!

        val query = reference.child(BARCODE).orderByChild("userId").equalTo(userId)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // do with your result
                    val userBarcodeField: UserBarcodeField = dataSnapshot.children.first().getValue(UserBarcodeField::class.java)!!
                    tv_total.text = "Total Points:  ${userBarcodeField.total}"
                    for (key in userBarcodeField.barcodes!!.keys) {
                        mRequests.add(userBarcodeField.barcodes!!.getValue(key))
                    }
                    adapter!!.updateRequests(mRequests)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }
}