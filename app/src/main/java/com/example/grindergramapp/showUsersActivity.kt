package com.example.grindergramapp

import Adapter.UserAdapter
import Model.User
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.RecoverySystem
import android.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.android.synthetic.main.fragment_search.view.*
import kotlinx.android.synthetic.main.posts_layout.*

class showUsersActivity : AppCompatActivity() {

    var id :String =""
    var title :String = ""

    var userAdapter:UserAdapter?=null
    var userList:List<User>?=null
    var idList:List<String>?=null


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_users)


        val intent = intent
        id = intent.getStringExtra("id")
        title = intent.getStringExtra("title")


        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = title
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener(){
            finish()
        }

        var recyclerView : RecyclerView
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        userList = ArrayList()
        userAdapter= UserAdapter(this, userList as ArrayList<User>,false)
        recyclerView.adapter= userAdapter

        idList= ArrayList()


        when(title)
        {
            "likes" -> getLikes()
            "following"-> getFollowings()
            "followers"-> getFollowers()
            "views"-> getViews()
        }
    }

    private fun getViews()
    {
        val Ref = FirebaseDatabase.getInstance().reference
            .child("Story")
            .child(id)
            .child(intent.getStringExtra("storyid"))
            .child("views")

        Ref.addValueEventListener(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError)
            {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot)
            {
                if(p0.exists())
                {
                    (idList as ArrayList<String>).clear()

                    for(snapshot in p0.children)
                    {
                        (idList as ArrayList<String>).add(snapshot.key!!)
                    }
                    showUsers()
                }
            }
        })
    }

    private fun getFollowers()
    {
        val followersRef = FirebaseDatabase.getInstance().reference
            .child("Follow").child(id)
            .child("Followers")

        followersRef.addValueEventListener(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError)
            {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot)
            {
                if(p0.exists())
                {
                    (idList as ArrayList<String>).clear()

                    for(snapshot in p0.children)
                    {
                        (idList as ArrayList<String>).add(snapshot.key!!)
                    }
                    showUsers()
                }
            }
        })
    }

    private fun getFollowings()
    {
        val followersRef = FirebaseDatabase.getInstance().reference
            .child("Follow").child(id)
            .child("Following")

        followersRef.addValueEventListener(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError)
            {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot)
            {
                if(p0.exists())
                {
                    (idList as ArrayList<String>).clear()

                    for(snapshot in p0.children)
                    {
                        (idList as ArrayList<String>).add(snapshot.key!!)
                    }
                    showUsers()
                }
            }
        })
    }

    private fun getLikes()
    {
        val likeRef =  FirebaseDatabase.getInstance()
            .reference.child("Likes")
            .child(id!!)


        likeRef.addValueEventListener(object : ValueEventListener
        {

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists())
                {
                    (idList as ArrayList<String>).clear()

                    for(snapshot in p0.children)
                    {
                        (idList as ArrayList<String>).add(snapshot.key!!)
                    }
                    showUsers()
                }

            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun showUsers()
    {
        val userRef = FirebaseDatabase.getInstance().getReference().child("Users")
        userRef.addValueEventListener(object :ValueEventListener
        {
            override fun onDataChange(dataSnapshot : DataSnapshot)
            {


                (userList as ArrayList<User>).clear()
                    //retireve the users in mmin time
                    for (snapshot in dataSnapshot.children) {
                        val user = snapshot.getValue(User::class.java)

                        for (id in idList!!) {
                            if (user!!.getUid() != null)
                            {
                                (userList as ArrayList<User>).add(user!!)
                            }
                        }
                    }
                    userAdapter?.notifyDataSetChanged()



            }

            override fun onCancelled(p0: DatabaseError)
            {

            }
        })
    }
}

