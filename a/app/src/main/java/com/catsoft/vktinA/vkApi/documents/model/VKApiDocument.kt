package com.catsoft.vktinA.vkApi.documents.model

import android.os.Parcel
import android.os.Parcelable
import org.json.JSONObject
import java.util.*

data class VKApiDocument(
    val id : Int,
    val ownerId : Int,
    val title : String,
    val size : Int,
    val ext : String,
    val url : String,
    internal val calendar: Calendar,
    val type : DocumentType
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        getCalendarFromMilliSec(
            parcel.readLong()
        ),
        DocumentType.valueOf(parcel.readInt())
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeInt(ownerId)
        parcel.writeString(title)
        parcel.writeInt(size)
        parcel.writeString(ext)
        parcel.writeString(url)
        parcel.writeLong(calendar.timeInMillis)
        parcel.writeInt(type.value)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VKApiDocument> {
        override fun createFromParcel(parcel: Parcel): VKApiDocument {
            return VKApiDocument(parcel)
        }

        override fun newArray(size: Int): Array<VKApiDocument?> {
            return arrayOfNulls(size)
        }

        fun getCalendarFromMilliSec(milliSec: Long): Calendar {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = milliSec

            return calendar
        }

        fun parse(json: JSONObject): VKApiDocument {
            return VKApiDocument(
                id = json.optInt("id", 0),
                ownerId = json.optInt("owner_id", 0),
                title = json.optString("title", ""),
                size = json.optInt("size", 0),
                ext = json.optString("ext", ""),
                url = json.optString("url", ""),
                calendar = getCalendarFromMilliSec(
                    json.optLong("date", 0) * 1000
                ),
                type = DocumentType.valueOf(
                    json.optInt("type", 0)
                )
            )
        }
    }
}

