package com.example.runnincle.framework.datasource.cache.implementation

import android.content.Context
import com.example.runnincle.framework.datasource.cache.abstraction.GsonSharedPreferenceService
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import org.json.JSONException
import org.json.JSONObject
import java.lang.Exception


class GsonSharedPreferencesServiceImpl constructor(context: Context): GsonSharedPreferenceService {

    companion object {
        const val GSON_SHARED_PREFERENCE = "GSON_SHARED_PREFERENCE"
    }

    private var sharedPreferences = context.getSharedPreferences(GSON_SHARED_PREFERENCE, Context.MODE_PRIVATE)
    private var mEditor = sharedPreferences.edit()

    private fun commit() {
        mEditor.commit()
    }

    override suspend fun saveObject(obj: Any) {
        val jsonString: String = Gson().toJson(obj)
        mEditor.putString(obj.javaClass.canonicalName, jsonString)
        commit()
    }

    override suspend fun saveObject(obj: Any, values: JSONObject) {
        mEditor.putString(obj.javaClass.canonicalName, values.toString())
        commit()
    }

    override suspend fun getObject(obj: Any): Any? {
        var obj: Any? = obj
        try {
            val jsonString = sharedPreferences.getString(obj?.javaClass?.canonicalName, "")
            obj = obj?.javaClass
            obj = Gson().fromJson(jsonString, obj)
        } catch (exception: JsonSyntaxException) {
            throw Exception(exception.message)
        }
        return obj
    }

    override suspend fun getJsonObject(obj: Any): JSONObject? {
        var obj = obj
        val jsonString = sharedPreferences.getString(obj.javaClass.canonicalName, "")
        var jsonObject: JSONObject? = null
        obj = obj.javaClass
        jsonObject = try {
            JSONObject(jsonString)
        } catch (e: JSONException) {
            e.printStackTrace()
            throw Exception(e.message)
        }
        return jsonObject
    }
}