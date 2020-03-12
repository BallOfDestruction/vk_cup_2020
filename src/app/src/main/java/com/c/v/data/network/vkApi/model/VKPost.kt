package com.c.v.data.network.vkApi.model

import org.json.JSONObject
import java.util.*

data class VKPost(val date: Calendar) {

    companion object {

        fun parse(jsonObject: JSONObject) : VKPost {
            return VKPost(
                getCalendarFromMilliSec(jsonObject.optLong("date") * 1000)
            )
        }

        private fun getCalendarFromMilliSec(milliSec: Long): Calendar {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = milliSec

            return calendar
        }
    }
}