package io.github.takusan23.onstop2finishandremovetask.tool

import android.content.Context
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onStart
import org.json.JSONArray

/** DataStore にある JSON を読み書きするクラス */
object RegisterAppListTool {

    fun realtimeReadApplicationIdList(context: Context): Flow<List<String>> = context.dataStore.data.mapNotNull { preferences ->
        preferences[PreferenceKeys.KEY_APPLICATION_ID_JSONARRAY]?.toJsonArray()
    }.onStart {
        emit(emptyList())
    }

    suspend fun readApplicationIdList(context: Context): List<String> {
        val preference = context.dataStore.data.first()
        val jsonArrayString = preference[PreferenceKeys.KEY_APPLICATION_ID_JSONARRAY]
        return jsonArrayString?.toJsonArray() ?: emptyList()
    }

    suspend fun saveApplicationIdList(context: Context, applicationIdList: List<String>) {
        val jsonArray = JSONArray().also { array ->
            applicationIdList.forEach { array.put(it) }
        }
        val jsonArrayString = jsonArray.toString()
        context.dataStore.edit { it[PreferenceKeys.KEY_APPLICATION_ID_JSONARRAY] = jsonArrayString }
    }

    private fun String.toJsonArray(): List<String> {
        val jsonArray = JSONArray(this)
        return (0 until jsonArray.length()).map { jsonArray.getString(it) }
    }

}