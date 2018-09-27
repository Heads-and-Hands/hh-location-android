package ru.handh.hhlocation.data.model

data class RegisterDeviceResponse(val message: String) {
    companion object {
        const val MESSAGE_OK = "ok"
    }

    fun isOk(): Boolean {
        return message == MESSAGE_OK
    }
}