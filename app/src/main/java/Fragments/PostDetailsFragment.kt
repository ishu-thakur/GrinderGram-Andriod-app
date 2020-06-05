package Fragments

import Adapter.PostAdapter
import Model.Post
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.grindergramapp.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PostDetailsFragment : Fragment() {

    private var  postAdapter: PostAdapter?=null
    private var  postList :MutableList<Post>?=null
    private  var postId :String=""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_post_details,container,false)


        val preferences =context?.getSharedPreferences("PRESS", Context.MODE_PRIVATE)
        if(preferences!=null)
        {
            postId =preferences.getString("postId","none")
        }

        var recyclerView :RecyclerView
        recyclerView = view .findViewById(R.id.recycler_view_post_details)
        recyclerView.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = linearLayoutManager

        postList =ArrayList()
        postAdapter=context?.let { PostAdapter(it, postList as ArrayList<Post>) }
        recyclerView.adapter =postAdapter


        retrievePosts()

        return view
    }


    private fun retrievePosts() {
        val postRef = FirebaseDatabase.getInstance().reference
            .child("Posts")
            .child(postId)

        postRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot) {
                postList?.clear()


                val post = p0.getValue(Post::class.java)
                postList!!.add(post!!)
                postAdapter!!.notifyDataSetChanged()

            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

}