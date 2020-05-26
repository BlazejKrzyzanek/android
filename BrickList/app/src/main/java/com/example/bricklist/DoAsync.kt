package com.example.bricklist

import android.os.AsyncTask

class DoAsync(val handler: () -> Unit, val postHandler: () -> Unit) :
    AsyncTask<Void, Void, Void>() {
    init {
        execute()
    }

    override fun doInBackground(vararg params: Void?): Void? {
        handler()
        return null
    }

    override fun onPostExecute(result: Void?) {
        super.onPostExecute(result)
        postHandler()
    }
}