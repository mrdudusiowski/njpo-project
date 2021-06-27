package com.cpma.app.model.request

import com.google.gson.annotations.SerializedName

data class BatteryRequest(@SerializedName("batteryLevel")var batteryLevel: Float,
                          @SerializedName("androidID")var androidID: String)