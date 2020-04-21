package com.google.firebase.ml.md.kotlin.barcodedetection

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue


@Parcelize
data class UserBarcodeField(var total: Int = 0, val userId: String = "", var barcodes: @RawValue HashMap<String, Barcode>? = null) : Parcelable {

    data class Barcode(var value: Int = 0)
}