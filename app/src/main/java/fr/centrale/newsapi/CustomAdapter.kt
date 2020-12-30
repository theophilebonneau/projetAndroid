package fr.centrale.newsapi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class CustomAdapter(private val dataSet: ArrayList<ArticlePreview>,
                    private val mOnArticleListener: OnArticleListener,
                    private val onBottomReachedListener: OnBottomReachedListener)
    : RecyclerView.Adapter<CustomAdapter.ViewHolder>()   {
    private val imgPlaceholder = Picasso.get().load("https://www.radiobeton.com/www/wp-content/uploads/2017/01/arton17969.jpg")
    class ViewHolder(view: View, onArticleListener: OnArticleListener): RecyclerView.ViewHolder(view) {
        val textTitle: TextView
        val textAuthor: TextView
        val textDate: TextView
        val imagePreview: ImageView

        init {
            view.setOnClickListener{
                onArticleListener.onArticleClick(adapterPosition)
            }
            textTitle =  view.findViewById(R.id.textTitle)
            textAuthor = view.findViewById(R.id.textAuthor)
            textDate = view.findViewById(R.id.textDate)
            imagePreview = view.findViewById(R.id.imagePreview)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
        return ViewHolder(view.inflate(R.layout.article_preview_img, viewGroup, false), mOnArticleListener)
    }

    interface OnArticleListener{
        fun onArticleClick(position: Int)
    }

    interface OnBottomReachedListener {
        fun onBottomReached(position: Int)
    }



    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.textTitle.text = dataSet[position].title
        viewHolder.textAuthor.text = dataSet[position].author
        viewHolder.textDate.text = dataSet[position].date

        if(dataSet[position].urlToImage == "null") {
            imgPlaceholder.into(viewHolder.imagePreview)
        } else {
            Picasso.get().load(dataSet[position].urlToImage).into(viewHolder.imagePreview)
        }

        if (position == (itemCount - 1)){
            onBottomReachedListener.onBottomReached(position);
        }
    }

    override fun getItemCount() = dataSet.size

    override fun getItemViewType(position: Int): Int {
        return position % 2
    }

    fun addArticles(newDataSet: ArrayList<ArticlePreview>) {
        for (article in newDataSet) {
            dataSet.add(article)
        }
    }
}