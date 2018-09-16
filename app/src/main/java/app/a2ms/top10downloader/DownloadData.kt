package app.a2ms.top10downloader

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import android.widget.ListView
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL
import kotlin.properties.Delegates

private const val TAG = "DownloadData"

class DownloadData(private val callbacks: DownloaderCallBack) : AsyncTask<String, Void, String>() {
    var propContext: Context by Delegates.notNull()
    var propListView: ListView by Delegates.notNull()

    interface DownloaderCallBack {
        fun onDataAvailable(data: List<FeedEntry>)
    }

    override fun onPostExecute(result: String) {

        val parseApplications = ParseApplication()
        if (result.isNotEmpty()) {
            parseApplications.parse(result)
        }
        callbacks.onDataAvailable(parseApplications.applications)
    }

    override fun doInBackground(vararg url: String): String {
        Log.d(TAG, "doInBackground: starts with ${url[0]}")
        val rssFeed = downloadXML(url[0])
        if (rssFeed.isEmpty()) {
        }
        return rssFeed
    }

    private fun downloadXML(urlPath: String): String {
        try {
            return URL(urlPath).readText()
        } catch (e: MalformedURLException) {
            Log.d(TAG, "downloadXml: Invalid URL " + e.message)
        } catch (e: IOException) {
            Log.d(TAG, "downloadXml: IO Exception reading data " + e.message)
        } catch (e: SecurityException) {
            Log.d(TAG, "downloadXml: Security Exception. Needs permission?" + e.message)
//            e.printStackTrace()
        }
        return "" //return an empty string if there was an exception
    }
}
