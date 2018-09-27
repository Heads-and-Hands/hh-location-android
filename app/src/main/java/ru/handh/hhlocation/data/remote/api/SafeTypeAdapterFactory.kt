package ru.handh.hhlocation.data.remote.api

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.io.IOException

class SafeTypeAdapterFactory : TypeAdapterFactory {
    override fun <T> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T> {
        val delegate = gson.getDelegateAdapter(this, type)
        return object : TypeAdapter<T>() {
            @Throws(IOException::class)
            override fun write(out: JsonWriter, value: T) {
                try {
                    delegate.write(out, value)
                } catch (e: IOException) {
                    delegate.write(out, null)
                }

            }

            @Throws(IOException::class)
            override fun read(`in`: JsonReader): T? {
                return try {
                    delegate.read(`in`)
                } catch (e: IOException) {
                    `in`.skipValue()
                    null
                } catch (e: IllegalStateException) {
                    `in`.skipValue()
                    null
                } catch (e: JsonSyntaxException) {
                    `in`.skipValue()
                    null
                }

            }
        }
    }
}