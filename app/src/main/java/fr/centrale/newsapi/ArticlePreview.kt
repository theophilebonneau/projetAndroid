package fr.centrale.newsapi

import org.json.JSONObject

class ArticlePreview {
    var title: String = ""
    var author: String = ""
    var date: String = ""
    var sourceName: String = ""
    var description: String = ""
    var link: String = ""
    var urlToImage: String? = ""

    constructor() {}

    constructor(article: JSONObject) {
        this.title = article.getString("title")
        this.author = article.getString("author")
        this.date = article.getString("publishedAt").subSequence(0, 10).toString()
        this.sourceName = article.getJSONObject("source").getString("name")
        this.description = article.getString("description")
        this.link = article.getString("url")
        this.urlToImage = article.getString("urlToImage")
    }
}