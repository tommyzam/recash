package com.google.firebase.ml.md.kotlin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.ml.md.R
import com.google.firebase.ml.md.kotlin.barcodedetection.UserBarcodeField
import java.util.*


class HistoryAdapter : RecyclerView.Adapter<HistoryAdapter.LiftsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LiftsViewHolder {
        return LiftsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.history_row, parent, false))
    }

    override fun onBindViewHolder(holder: LiftsViewHolder, position: Int) {
        val model: UserBarcodeField.Barcode = mList[position]
        if (model != null) {
            holder.barcode.text = "Barcode Value: " + model.value
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun updateRequests(list: ArrayList<UserBarcodeField.Barcode>) {
        mList.clear()
        mList.addAll(list)
        notifyDataSetChanged()
    }

    class LiftsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
         val barcode: TextView = itemView.findViewById(R.id.tv_barcode)

    }

    companion object {
        private var mList: ArrayList<UserBarcodeField.Barcode> = ArrayList()
    }
}
