package com.example.osps

import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var word: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun checkWord(v: View) {
        val task = BgTask()
        task.execute("Check")
    }

    fun doAnagrams(v: View) {
        val task = BgTask()
        task.execute("Anagrams")
    }

    private inner class BgTask : AsyncTask<String, Int, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
            word = editWord.text.toString().trim()
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            txtResult.text = result
        }

        override fun doInBackground(vararg params: String?): String {
            if (word != null) {
                val w = word!!
                if (w.length > 1) {
                    if (params[0] == "Check") {
                        try {
                            val url = "http://www.pfs.org.pl/files/php/osps_funkcje2.php"
                            val response = khttp.get(
                                url = url,
                                params = mapOf("s" to "spr", "slowo_arbiter2" to w)
                            )
                            val r = response.content[0].toInt()
                            if (r == 49)
                                return "Słowo jest poprawne"
                            else
                                return "Słowo jest niepoprawne"
                        } catch (e: Exception) {
                            return e.message.toString()
                        }
                    } else {
                        try {
                            val url = "http://www.pfs.org.pl/files/php/osps_funkcje2.php"
                            val response = khttp.get(
                                url = url,
                                params = mapOf("s" to "ana", "slowo2" to w)
                            )
                            val r = String(response.content)
                            val items = r.split("|")
                            val sb = StringBuilder()

                            for (i in items) if (i != "") {
                                sb.append("\n")
                                sb.append(i)
                            }
                            return sb.toString()
                        } catch (e: Exception) {
                            return e.message.toString()
                        }
                    }
                } else {
                    return "Wprowadź słowo mające 2 lub więcej liter."
                }
            }
            return "Wprowadź słowo mające 2 lub więcej liter."
        }

    }
}
