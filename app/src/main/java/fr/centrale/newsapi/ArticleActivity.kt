package fr.centrale.newsapi

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso

class ArticleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article)

        setUpTextViews()

        findViewById<Button>(R.id.linkButton).setOnClickListener{
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Not implemented yet")
            builder.show()
        }

        Picasso.get().load(intent.getStringExtra("urlToImage")).into(findViewById<ImageView>(R.id.imageView))
    }

    private fun setUpTextViews() {
        findViewById<TextView>(R.id.titleTextView).text = intent.getStringExtra("title")
        findViewById<TextView>(R.id.authorTextView).text = intent.getStringExtra("author")
        findViewById<TextView>(R.id.dateTextView).text = intent.getStringExtra("date")
        findViewById<TextView>(R.id.sourceTextView).text = intent.getStringExtra("sourceName")
        findViewById<TextView>(R.id.desciptionTextView).text = intent.getStringExtra("description")
    }
}