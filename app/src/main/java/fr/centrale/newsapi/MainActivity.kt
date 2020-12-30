package fr.centrale.newsapi

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray

class MainActivity : AppCompatActivity(), CustomAdapter.OnArticleListener, CustomAdapter.OnBottomReachedListener {


    val TOKEN = "d15f50a6ab8447f9a932a1b565259af2"
    val SOURCES_URL = "https://newsapi.org/v2/sources?apiKey=$TOKEN&language=fr"
    val BASE_ARTICLES_URL = "https://newsapi.org/v2/everything?apiKey=$TOKEN&language=fr"
    var sources = JSONArray()
    var articlesData = ArrayList<ArticlePreview>()
    var currentSourceId = ""
    var currentPage = 1
    lateinit var queue: RequestQueue
    lateinit var preferences: SharedPreferences
    lateinit var recyclerView: RecyclerView
    lateinit var viewManager: LinearLayoutManager
    lateinit var viewAdapter: CustomAdapter
    lateinit var progressBar: ProgressBar
    lateinit var alertDialogBuilder: AlertDialog.Builder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        progressBar = findViewById<ProgressBar>(R.id.progressBar)
        preferences = getPreferences(MODE_PRIVATE)
        queue = Volley.newRequestQueue(this)
        setUpAlertDialogBuilder()

        getSources(preferences.getString("sourceId", null))
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        super.onPrepareOptionsMenu(menu)
        if (sources.length() > 0) {
            menu?.clear()
        }
        for (index in 0 until sources.length()) {
            menu?.add(0, index, index, sources.getJSONObject(index).getString("name"))
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        currentSourceId = sources.getJSONObject(item.itemId).getString("id")
        currentPage = 1
        getArticles(currentSourceId, currentPage)
        return true
    }

    override fun onArticleClick(position: Int) {
        val article = articlesData[position]
        val articleIntent = Intent(this, ArticleActivity::class.java)
        articleIntent.putExtra("title", article.title)
        articleIntent.putExtra("author", article.author)
        articleIntent.putExtra("date", article.date)
        articleIntent.putExtra("sourceName", article.sourceName)
        articleIntent.putExtra("description", article.description)
        articleIntent.putExtra("link", article.link)
        articleIntent.putExtra("urlToImage", article.urlToImage)
        startActivity(articleIntent)
    }

    override fun onBottomReached(position: Int) {
        getArticles(currentSourceId, currentPage + 1)
    }

    private fun setUpAlertDialogBuilder() {
        alertDialogBuilder = this.let {
            AlertDialog.Builder(it)
        }
        alertDialogBuilder.setTitle("Something went wrong")
            .apply{
                setPositiveButton("Retry") { _, _ ->
                    getSources(currentSourceId)
                }
                setNegativeButton("Cancel") { _, _ ->
                    progressBar.isVisible = false
                }
            }
    }

    private fun showAlertDialog(message: String) {
        alertDialogBuilder.setMessage(message)
        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun getSources(savedSourceId: String?) {
        progressBar.isVisible = true
        val sourcesRequest = object: JsonObjectRequest(
            Method.GET, SOURCES_URL, null,
            { response ->
                sources = response.getJSONArray("sources")
                if (sources.length() == 0) {
                    showAlertDialog("No source")
                } else {
                    if (savedSourceId != null) {
                        currentSourceId = savedSourceId
                        progressBar.isVisible = false
                        getArticles(currentSourceId, currentPage)
                    } else {
                        currentSourceId = sources.getJSONObject(0).getString("id")
                        getArticles(currentSourceId, currentPage)
                    }
                }
            },
            { error ->
                showAlertDialog(error.toString())
            })
        {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["User-Agent"] = "Mozilla/5.0"
                return headers
            }
        }

        queue.add(sourcesRequest)
    }

    private fun getArticles(sourceId: String, page: Int) {
        val url = "$BASE_ARTICLES_URL&sources=$sourceId&page=$page"
        saveCurrentSourceId()
        progressBar.isVisible = true
        val articlesRequest = object: JsonObjectRequest(
            Method.GET, url, null,
            { response ->
                if (response.getJSONArray("articles").length() == 0) {
                    showAlertDialog("Didn't find any article")
                } else {
                    if (page == currentPage) {
                        articlesData = formatDataSet(response.getJSONArray("articles"))
                        setUpRecyclerView()
                    } else {
                        currentPage = page
                        val newArticleData = formatDataSet(response.getJSONArray("articles"))
                        viewAdapter.addArticles(newArticleData)
                        for (article in newArticleData) {
                            articlesData.add(article)
                        }
                    }
                    progressBar.isVisible = false
                }
            },
            { error ->
                showAlertDialog(error.toString())
            })
        {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["User-Agent"] = "Mozilla/5.0"
                return headers
            }
        }

        queue.add(articlesRequest)
    }

    private fun saveCurrentSourceId() {
        super.onPause()
        val editor = preferences.edit()
        editor.putString("sourceId", currentSourceId)
        editor.apply()
    }

    private fun formatDataSet(articles: JSONArray): ArrayList<ArticlePreview> {
        val newArticlesData = ArrayList<ArticlePreview>()
        for (index in 0 until articles.length()) {
            val article = articles.getJSONObject(index)
            val articlePreview = ArticlePreview(article)
            newArticlesData.add(articlePreview)
        }
        return newArticlesData
    }

    private fun setUpRecyclerView() {
        viewManager = LinearLayoutManager(this)
        viewAdapter = CustomAdapter(articlesData, this, this)

        recyclerView = findViewById<RecyclerView>(R.id.recyclerView).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }
}