package com.github.gotify.messages.provider

import android.app.Activity
import com.github.gotify.Utils
import com.github.gotify.api.ApiException
import com.github.gotify.api.Callback
import com.github.gotify.client.ApiClient
import com.github.gotify.client.api.ApplicationApi
import com.github.gotify.client.model.Application

class ApplicationHolder(private val activity: Activity, private val client: ApiClient) {
    private var state: List<Application> = listOf()
    private var onUpdate: Runnable? = null
    private var onUpdateFailed: Runnable? = null

    fun wasRequested(): Boolean {
        return state.isNotEmpty()
    }

    fun request() {
        client.createService(ApplicationApi::class.java)
            .apps
            .enqueue(
                Callback.callInUI(
                    activity,
                    { apps: List<Application> ->
                        onReceiveApps(
                            apps
                        )
                    }
                ) { e: ApiException -> onFailedApps(e) })
    }

    private fun onReceiveApps(apps: List<Application>) {
        state = apps
        if (onUpdate != null) onUpdate!!.run()
    }

    private fun onFailedApps(e: ApiException) {
        Utils.showSnackBar(activity, "Could not request applications, see logs.")
        if (onUpdateFailed != null) onUpdateFailed!!.run()
    }

    fun get() = state

    fun onUpdate(runnable: Runnable?) {
        onUpdate = runnable
    }

    fun onUpdateFailed(runnable: Runnable?) {
        onUpdateFailed = runnable
    }
}
