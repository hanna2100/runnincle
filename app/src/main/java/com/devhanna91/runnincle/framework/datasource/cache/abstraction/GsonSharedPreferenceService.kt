package com.devhanna91.runnincle.framework.datasource.cache.abstraction

import org.json.JSONObject

interface GsonSharedPreferenceService {

    suspend fun saveObject(obj: Any)

    suspend fun saveObject(obj: Any, values: JSONObject)

    suspend fun getObject(obj: Any): Any?

    suspend fun getJsonObject(obj: Any): JSONObject?

}