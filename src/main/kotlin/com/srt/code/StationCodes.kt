package com.srt.code

enum class StationCodes(val description: String, val code: String) {
    STATION_CODE_0551("수서", "0551"),
    STATION_CODE_0015("동대구", "0015"),
    STATION_CODE_0552("동탄", "0552"),
    ;

    companion object {
        fun fromCode(code: String): StationCodes {
            return entries.firstOrNull { it.code == code }
                ?: throw IllegalArgumentException("Invalid code: $code")
        }
    }
}
