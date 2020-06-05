package Fragments

import Adapter.MyImagesAdapter
import Model.Post
import Model.User
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.grindergramapp.AccountSettingsActivity

import com.example.grindergramapp.R
import com.example.grindergramapp.showUsersActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment() {

    private lateinit var profileId :String
    private lateinit var firebaseUser :FirebaseUser
    var postList : List<Post>? = null
    var myImagesAdapter : MyImagesAdapter? = null
    var myImagesAdapterSavedPics :MyImagesAdapter? = null
    var postListSaved : List<Post>? = null
    var mySaveImg : List<String>? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // ========================================Inflate the layout for this fragment==========================
        val view =inflater.inflate(R.layout.fragment_profile, container, false)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        val pref =context?.getSharedPreferences("PRESS",Context.MODE_PRIVATE)

            if(pref!=null)
            {
                this.profileId =pref.getString("profileId","none")
            }

        if(profileId == firebaseUser.uid)
        {
            view.edit_acc_settings_btn.text = "EDIT PROFILE"
        }

        else if(profileId != firebaseUser.uid)
        {
            checkFollowAndFollowingandButtonStatus()
        }

//============================to display uploaded pictures in grid view horizontal=========================================
        var recyclerViewUploadImages:RecyclerView
        recyclerViewUploadImages= view.findViewById(R.id.grid_view_uploaded_pics)
        recyclerViewUploadImages.setHasFixedSize(true)
        val linearLayoutManager :LinearLayoutManager = GridLayoutManager(context, 3)
        recyclerViewUploadImages.layoutManager=linearLayoutManager


        postList = ArrayList()
        myImagesAdapter = context?.let { MyImagesAdapter(it ,postList as ArrayList<Post>) }
        recyclerViewUploadImages.adapter = myImagesAdapter



        //============================to display Save pictures in grid view horizontal=========================================

        var recyclerViewSavedImages:RecyclerView
        recyclerViewSavedImages= view.findViewById(R.id.grid_view_save_pics)
        recyclerViewSavedImages.setHasFixedSize(true)
        val linearLayoutManager2 :LinearLayoutManager = GridLayoutManager(context, 3)
        recyclerViewUploadImages.layoutManager = linearLayoutManager2


        postListSaved = ArrayList()
        myImagesAdapterSavedPics = context?.let { MyImagesAdapter(it ,postListSaved as ArrayList<Post>) }
        recyclerViewUploadImages.adapter = myImagesAdapterSavedPics



        recyclerViewSavedImages.visibility = View.GONE
        recyclerViewSavedImages.visibility = View.VISIBLE




        var uploadImageBtn :ImageButton
        uploadImageBtn = view .findViewById(R.id.images_grid_view_btn)
        uploadImageBtn.setOnClickListener {
            recyclerViewSavedImages.visibility = View.GONE
            recyclerViewSavedImages.visibility = View.VISIBLE
        }




        var saveImageBtn :ImageButton
        saveImageBtn = view .findViewById(R.id.images_save_btn)
        saveImageBtn.setOnClickListener {
            recyclerViewSavedImages.visibility = View.VISIBLE
            recyclerViewSavedImages.visibility = View.GONE
        }

//=================================sending userto show user actiiy whenever she r he click on followers or follwoing===================
        view.total_followers.setOnClickListener {
            val intent = Intent(context , showUsersActivity::class.java)
            intent.putExtra("id",profileId)
            intent.putExtra("title","followers")
            startActivity(intent)
        }


        view.total_following.setOnClickListener {
            val intent = Intent(context , showUsersActivity::class.java)
            intent.putExtra("id",profileId)
            intent.putExtra("title","following  ")
            startActivity(intent)
        }



        //==============================to send the view to account setting.java classs=============================
        view.edit_acc_settings_btn.setOnClickListener {

            val getButtonText= view.edit_acc_settings_btn.text.toString()


            //====================to change following and followers count whensomone click on it============================
            when
            {
                getButtonText == "EDIT PROFILE" -> startActivity(Intent(context,AccountSettingsActivity::class.java))

                getButtonText =="Follow" -> {
                    firebaseUser?.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(it1.toString())
                            .child("Following").child(profileId).setValue(true)
                    }


                    firebaseUser?.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follower").child(profileId)
                            .child("Following").child(it1.toString()).setValue(true)
                    }

                    addNotification()
                }

                getButtonText =="Following" -> {
                    firebaseUser?.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow")
                            .child(it1.toString())
                            .child("Following")
                            .child(profileId)
                            .removeValue()
                    }


                    firebaseUser?.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follower")
                            .child(profileId)
                            .child("Following")
                            .child(it1.toString())
                            .removeValue()
                    }
                }

            }
        }

        getFollowers()
        getFollowings()
        userInfo()
        getPics()
        getTotalPosts()
        mySave()

        return view
    }

    private fun checkFollowAndFollowingandButtonStatus()
    {
        val followingRef = firebaseUser?.uid.let { it1 ->
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(it1.toString())
                .child("Following")
        }

        if(followingRef != null)
        {
            followingRef.addValueEventListener(object : ValueEventListener
            {
                override fun onDataChange(p0: DataSnapshot) {
                    if(p0.child(profileId).exists())
                    {
                        view?.edit_acc_settings_btn?.text = "Following"
                    }
                    else
                    {
                        view?.edit_acc_settings_btn?.text = "Follow"
                    }
                }

                override fun onCancelled(p0: DatabaseError)
                {

                }
            })
        }
    }


    private fun getFollowers()
    {
        val followersRef = FirebaseDatabase.getInstance().reference
                .child("Follow").child(profileId)
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
                    view?.total_followers?.text = p0.childrenCount.toString()
                }
            }
        })
    }




    private fun getFollowings()
    {
        val followersRef = FirebaseDatabase.getInstance().reference
                .child("Follow").child(profileId)
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
                    view?.total_following?.text = p0.childrenCount.toString()
                }
            }
        })
    }


    private fun getPics()
    {
        val picRef = FirebaseDatabase.getInstance().reference.child("Posts")

        picRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists())
                {
                    (postList as ArrayList<Post>).clear()
                    for(snapshot in p0.children)
                    {
                        val post = snapshot.getValue(Post::class.java)!!
                        if(post.getPublisher().equals(profileId))
                        {

                            (postList as ArrayList<Post>).add(post)
                        }
                        Collections.reverse(postList)
                        myImagesAdapter!!.notifyDataSetChanged()
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }



    private fun userInfo()
    {
        val userRef = FirebaseDatabase.getInstance().reference.child("Users").child(profileId)
        userRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {

                if(p0.exists())
                {
                    val user = p0.getValue<User>(User::class.java)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(view?.pro_image_profile_frag)
                    view?.profile_fragment_username?.text =user!!.getUsername()
                    view?.full_name_profile_frag?.text =user!!.getFullname()
                    view?.profile_bio?.text =user!!.getBio()
                }}

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    override fun onStop() {
        super.onStop()

        val pref = context?.getSharedPreferences("PRESS",Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()

    }

    override fun onPause() {
        super.onPause()

        val pref = context?.getSharedPreferences("PRESS",Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.getUid())
        pref?.apply()

    }

    override fun onDestroy() {
        super.onDestroy()

        val pref = context?.getSharedPreferences("PRESS",Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.getUid())
        pref?.apply()
    }


    private fun getTotalPosts()
    {
        val postRef = FirebaseDatabase.getInstance().reference.child("Posts")

        postRef.addValueEventListener(object : ValueEventListener{

            override fun onDataChange(dataSnapshot: DataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    var postCounter =0
                    for(snapshot in dataSnapshot.children)
                    {
                        val post = snapshot.getValue(Post::class.java)!!
                        if(post.getPublisher() == profileId)
                        {
                            postCounter++
                        }
                    }
                    total_post.text = " "+postCounter
                }

            }

            override fun onCancelled(p0: DatabaseError)
            {

            }
        })
    }

    private fun mySave()
    {
        mySaveImg = ArrayList()
        val saveRef = FirebaseDatabase.getInstance()
            .reference
            .child("Saves")
            .child(firebaseUser!!.uid)
        saveRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(dataSnapshot: DataSnapshot)
            {
                if(dataSnapshot.exists())
                    for(snapshot in dataSnapshot.children)
                    {
                        (mySaveImg as ArrayList<String>).add(snapshot.key!!)
                    }
                readmySaveImgData()
            }


            override fun onCancelled(p0: DatabaseError)
            {

            }
        })
    }

    private fun readmySaveImgData()
    {
        val postsRef = FirebaseDatabase.getInstance().reference.child("Posts")

        postsRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(dataSnapshot: DataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    (postListSaved as ArrayList<Post>).clear()
                    for(snapshot in dataSnapshot.children)
                    {
                        val post =snapshot.getValue(Post::class.java)

                        for(key in mySaveImg!! )


                            if(post!!.getPostid()==key)
                            {
                                (postListSaved as ArrayList<Post>).add(post!!)

                            }
                    }
                }
                myImagesAdapterSavedPics!!.notifyDataSetChanged()
            }

            override fun onCancelled(p0: DatabaseError)
            {

            }
        })
    }

    private fun addNotification()
    {
        val notiRef = FirebaseDatabase.getInstance()
            .reference
            .child("Notifications")
            .child(profileId)
        val notiMap = HashMap<String , Any>()
        notiMap["userid"] = firebaseUser!!.uid
        notiMap["comment"] = "started following you"
        notiMap["postid"] = ""
        notiMap["ispost"] = false

        notiRef.push().setValue(notiMap)
    }

}
