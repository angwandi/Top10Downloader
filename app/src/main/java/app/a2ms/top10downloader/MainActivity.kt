package app.a2ms.top10downloader

import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class MainActivity : AppCompatActivity() {
    private val mTAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(mTAG, "onCreate called")
        val downloadData = DownloadData()
        downloadData.execute("http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=10/xml")
        Log.d(mTAG, "onCreate done")
    }

    //inner class as Async requests
    companion object {
        private class DownloadData : AsyncTask<String, Void, String>() {
            private val mTAG = "DownloadData"

            override fun onPostExecute(result: String?) {
                super.onPostExecute(result)
                Log.d(mTAG, "onPostExecute: parameter is $result")
            }

            override fun doInBackground(vararg url: String?): String {
                Log.d(mTAG, "doInBackground: starts with ${url[0]}")
                val rssFeed = downloadXML(url[0])
                if (rssFeed.isEmpty()) {
                    Log.e(mTAG, "doInBackground: error downloading")
                }
                return rssFeed
            }

            private fun downloadXML(urlPath: String?): String {
                val xmlResult = StringBuilder()
                try {
                    val url = URL(urlPath)
                    val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
                    val response = connection.responseCode
                    Log.d(mTAG, "downloadXML: The response code was $response")


//                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
//
//                    val inputBuffer = CharArray(500)
//                    var charsRead = 0
//                    while (charsRead <= 0) {
//                        charsRead = reader.read(inputBuffer)
//                        if (charsRead > 0) {
//                            xmlResult.append(String(inputBuffer, 0, charsRead))
//                        }
//                    }
//                    reader.close()
                    //todo Idiomatic Kotlin
                    //val stream = connection.inputStream
                    connection.inputStream.reader().use {
                        xmlResult.append(it.readText())
                    }
                    Log.d(mTAG, "Received ${xmlResult.length} bytes")
                    return xmlResult.toString()
//                } catch (e: MalformedURLException) {
//                    Log.e(mTAG, "downloadXML: Invalid URL ${e.message}")
//                } catch (e: IOException) {
//                    Log.e(mTAG, "downloadXML: IO Exception reading data: $e")
//                } catch (e: SecurityException) {
//                    Log.e(mTAG, "downloadXML: Security exception: Need permissions? $e")
//                } catch (e: Exception) {
//                    Log.e(mTAG, "Unknown error: $e")
//                }
                } catch (e: Exception) {
                    val errorMessage: String = when (e) {
                        is MalformedURLException -> "downloadXML: Invalid URL ${e.message}"
                        is IOException -> "downloadXML: IO Exception reading data: ${e.message}"
                        is SecurityException -> {
                            e.printStackTrace()
                            "downloadXML: Security Exception. Needs permissions? ${e.message}"
                        }
                        else -> "Unknown error: ${e.message}"
                    }
                }
                return "" //If it gets to here, there's been a problem. Rerun the empty string
            }

        }


    }

}
