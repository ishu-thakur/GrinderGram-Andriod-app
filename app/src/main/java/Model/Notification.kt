package Model

class Notification
{
    private var userid :String = ""
    private var text :String = ""
    private var postid :String = ""
    private var ispost  = false

    constructor()
    constructor(userid: String, text: String, postid: String, ispost: Boolean) {
        this.userid = userid
        this.text = text
        this.postid = postid
        this.ispost = ispost
    }

    fun getPostid(): String
    {
        return postid
    }

    fun setPostid(postid: String)
    {
        this.postid = postid
    }

    fun getuserId(): String
    {
        return userid
    }
    fun setuserId(userid: String)
    {
        this.userid = userid
    }

    fun getText(): String
    {
        return text
    }
    fun setText(text: String)
    {
        this.text = text
    }

    fun getisPost(): Boolean
    {
        return ispost
    }
    fun setisPost(ispost: Boolean)
    {
        this.ispost = ispost
    }

}