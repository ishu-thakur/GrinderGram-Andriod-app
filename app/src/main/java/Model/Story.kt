package Model

import com.google.firebase.auth.ktx.userProfileChangeRequest

class Story
{
    private var imageUrl :String = ""
    private var timestart :Long =0
    private var timeend :Long =0
    private var storyid :String = ""
    private var userid :String = ""

    constructor()
    constructor(imageUrl: String, timestart: Long, timeend: Long, storyid: String, userid: String) {
        this.imageUrl = imageUrl
        this.timestart = timestart
        this.timeend = timeend
        this.storyid = storyid
        this.userid = userid
    }

    fun getImageUrl():String
    {
        return imageUrl
    }
    fun setImageUrl(imageUrl: String)
    {
        this.imageUrl = imageUrl
    }


    fun getTimeStart():Long
    {
        return timestart
    }
    fun setTimeStart(timeend: Long)
    {
        this.timestart = timestart
    }


    fun getTimeEnd():Long
    {
        return timeend
    }
    fun setTimeEnd(timeend: Long)
    {
        this.imageUrl = imageUrl
    }


    fun getstoryid():String
    {
        return storyid
    }
    fun setStoryid(storyid: String)
    {
        this.storyid = storyid
    }


    fun getuserid():String
    {
        return userid
    }
    fun setUserid(userid: String)
    {
        this.userid = userid
    }
}