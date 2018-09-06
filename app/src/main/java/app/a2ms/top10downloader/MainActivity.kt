package app.a2ms.top10downloader

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import kotlinx.android.synthetic.main.activity_main.*
import java.net.URL
import kotlin.properties.Delegates

class FeedEntry {
    var name: String = ""
    var artist: String = ""
    var releaseDate: String = ""
    var summary: String = ""
    var imageURL: String = ""
    override fun toString(): String {
        return """
            name=$name
            artist =$artist
            releaseDate =$releaseDate
            summary =$summary
            imageURL =$imageURL
            """.trimIndent()
    }
}

class MainActivity : AppCompatActivity() {
    private val mTAG = "MainActivity"
    private var downloadData: DownloadData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        downloadUrl("http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=25/xml")
        Log.d(mTAG, "onCreate: done")

    }

    private fun downloadUrl(feedURL: String) {
        Log.d(mTAG, "downloadUrl starting AsyncTAsk")
        downloadData = DownloadData(this, xmlListView)
        downloadData?.execute(feedURL)
        Log.d(mTAG, "onCreate done")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.feeds_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val feedUrl: String
        when (item?.itemId) {
            R.id.mnuFree ->
                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=10/xml"
            R.id.mnuPaid ->
                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/toppaidapplications/limit=10/xml"
            R.id.mnuSongs ->
                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=10/xml"
            else ->
                return super.onOptionsItemSelected(item)
        }
        downloadUrl(feedUrl)
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        downloadData?.cancel(true)
    }

    //inner class as Async requests
    companion object {
        private class DownloadData(context: Context, listView: ListView) : AsyncTask<String, Void, String>() {

            var propContext: Context by Delegates.notNull()
            var propListView: ListView by Delegates.notNull()

            init {
                propContext = context
                propListView = listView
            }

            override fun onPostExecute(result: String) {
                super.onPostExecute(result)
                val parseApplications = ParseApplication()
                parseApplications.parse(result)

                //Set up the adapter
                val feedAdapter = FeedAdapter(propContext, R.layout.list_record,
                        parseApplications.applications)
                propListView.adapter = feedAdapter
            }

            override fun doInBackground(vararg url: String?): String {
//                Log.d(mTAG, "doInBackground: starts with ${url[0]}")
                val rssFeed = downloadXML(url[0])
                if (rssFeed.isEmpty()) {
                }
                return rssFeed
            }

            private fun downloadXML(urlPath: String?): String {
                return URL(urlPath).readText()
            }
        }

    }

}
