package Adapter

import Fragments.PostDetailsFragment
import Fragments.ProfileFragment
import Model.Post
import Model.User
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.grindergramapp.CommentsActivity
import com.example.grindergramapp.MainActivity
import com.example.grindergramapp.R
import com.example.grindergramapp.showUsersActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_account_settings.*
import kotlinx.android.synthetic.main.activity_comments.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.posts_layout.view.*
import org.jetbrains.annotations.NotNull

class PostAdapter(private val mContext: Context ,
                  private val mPost :List<Post>):RecyclerView.Adapter<PostAdapter.ViewHolder>()
{
    private var firebaseUser:FirebaseUser?=null

   inner class ViewHolder(@NotNull itemView: View):RecyclerView.ViewHolder(itemView)
   {
       var profileImage :CircleImageView
       var postImage :ImageView
       var likeButton: ImageView
       var commentButton:ImageView
       var saveButton:ImageView
       var username :TextView
       var likes:TextView
       var comments :TextView
       var publisher :TextView
       var description :TextView

       init {
           profileImage = itemView.findViewById(R.id.user_profile_image_post)
           postImage = itemView.findViewById(R.id.post_image_home)
           likeButton = itemView.findViewById(R.id.post_image_like_btn)
           commentButton = itemView.findViewById(R.id.post_save_comment_btn)
           saveButton = itemView.findViewById(R.id.post_save_comment_btn)
           username = itemView.findViewById(R.id.user_name_post)
           likes = itemView.findViewById(R.id.post_image_like_btn)
           publisher = itemView.findViewById(R.id.publisher)
           description = itemView.findViewById(R.id.description)
           comments = itemView.findViewById(R.id.comments)

       }

   }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(mContext).inflate(R.layout.posts_layout,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mPost.size
    }


//===========================code to take user whenever he or she clicked on the user image or name ============================
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        firebaseUser = FirebaseAuth.getInstance().currentUser

    val post = mPost[position]
    Picasso.get().load(post.getPostimage()).into(holder.postImage)

    if(post.getDescription().equals(""))
    {
        holder.description.visibility = View.GONE
    }
    else
    {
        holder.description.visibility = View.VISIBLE
        holder.description.setText(post.getDescription())
    }


    publisherInfo(holder.profileImage, holder.username, holder.publisher, post.getPublisher())


    isLikes(post.getPostid() , holder.likeButton)
    numberOflikes(holder.likes , post.getPostid())
    numberOfcomments(holder.comments, post.getPostid())
    checkStatus(post.getPostid() , holder.saveButton)




    holder.postImage.setOnClickListener {
        val editor = mContext.getSharedPreferences("PRESS",Context.MODE_PRIVATE).edit()

        editor.putString("postId",post.getPostid())

        editor.apply()

        (mContext as FragmentActivity).getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.fragment_container, PostDetailsFragment()).commit()
    }



    holder.profileImage.setOnClickListener {
        val editor = mContext.getSharedPreferences("PRESS",Context.MODE_PRIVATE).edit()

        editor.putString("profileId",post.getPublisher())

        editor.apply()

        (mContext as FragmentActivity).getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.fragment_container, ProfileFragment()).commit()
    }



    holder.publisher.setOnClickListener {
        val editor = mContext.getSharedPreferences("PRESS",Context.MODE_PRIVATE).edit()

        editor.putString("profileId",post.getPublisher())

        editor.apply()

        (mContext as FragmentActivity).getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.fragment_container, ProfileFragment()).commit()
    }


    holder.likeButton.setOnClickListener {
        if (holder.likeButton.tag=="Like")
        {
            FirebaseDatabase.getInstance()
                .reference.child("Likes")
                .child(post.getPostid())
                .child(firebaseUser!!.uid)
                .setValue(true)

            addNotification(post.getPublisher(), post.getPostid())
        }

        else
        {
            FirebaseDatabase.getInstance()
                .reference.child("Likes")
                .child(post.getPostid())
                .child(firebaseUser!!.uid)
                .removeValue()

            val intent= Intent(mContext, MainActivity::class.java)
            mContext.startActivity(intent)
        }
    }


    holder.likes.setOnClickListener {
        val intent = Intent(mContext , showUsersActivity::class.java)
        intent.putExtra("id",post.getPostid())
        intent.putExtra("title","followers")
        mContext.startActivity(intent)
    }


    holder.commentButton.setOnClickListener {
        val intentComment = Intent(mContext, CommentsActivity::class.java)
        intentComment.putExtra("postId",post.getPostid())
        intentComment.putExtra("publisherId",post.getPublisher())
        mContext.startActivity(intentComment)
    }

    holder.comments.setOnClickListener {
        val intentComment = Intent(mContext, CommentsActivity::class.java)
        intentComment.putExtra("postId",post.getPostid())
        intentComment.putExtra("publisherId",post.getPublisher())
        mContext.startActivity(intentComment)
    }

    holder.saveButton.setOnClickListener {
       if(holder.saveButton.tag =="Save")
       {
           FirebaseDatabase.getInstance().reference
               .child("Saves")
               .child(firebaseUser!!.uid)
               .child(post.getPostid())
               .setValue(true)

       }
        else
       {
           FirebaseDatabase.getInstance().reference
               .child("Saves")
               .child(firebaseUser!!.uid)
               .child(post.getPostid())
               .removeValue()
       }

    }

}

    private fun numberOflikes(likes: TextView, postid: String) {
        val likeRef =  FirebaseDatabase.getInstance()
            .reference.child("Likes")
            .child(postid)


        likeRef.addValueEventListener(object :ValueEventListener
        {

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists())
                {
                    likes.text = p0.childrenCount.toString() + " likes"
                }

            }
            override fun onCancelled(p0: DatabaseError) {

            }
    })
    }





    private fun numberOfcomments(commets: TextView, postid: String) {
        val commetsRef =  FirebaseDatabase.getInstance()
            .reference.child("Likes")
            .child(postid)


        commetsRef.addValueEventListener(object :ValueEventListener
        {

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists())
                {
                    commets.text = "Comments" + p0.childrenCount.toString() + " comments"
                }

            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }





    private fun isLikes(postid: String, likeButton: ImageView) {

        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val likeRef =  FirebaseDatabase.getInstance()
            .reference.child("Likes")
            .child(postid)


        likeRef.addValueEventListener(object :ValueEventListener
        {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.child(firebaseUser!!.uid).exists())
                {
                    likeButton.setImageResource(R.drawable.heart_clicked)
                    likeButton.tag ="Liked"
                }
                else
                {
                    likeButton.setImageResource(R.drawable.heart_not_clicked)
                    likeButton.tag ="Like"
                }
            }
        })
    }


    private fun publisherInfo(profileImage: CircleImageView, username: TextView, publisher: TextView, publisherID: String)
    {
        val userRef = FirebaseDatabase.getInstance().reference.child("Users").child(publisherID)

        userRef.addValueEventListener(object : ValueEventListener {


            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists())
                {
                    val user = p0.getValue<User>(User::class.java)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(profileImage)
                    username.text=user!!.getUsername()
                    publisher.text=user!!.getFullname()

                }
            }


            override fun onCancelled(p0: DatabaseError) {

            }
            
        })
    }

    private fun checkStatus(postid: String , imageView: ImageView)
    {
        val saveRef =FirebaseDatabase.getInstance().reference
            .child("Saves")
            .child(firebaseUser!!.uid)


        saveRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                if(p0.child(postid).exists())
                {
                    imageView.setImageResource(R.drawable.save_large_icon)
                    imageView.tag = "Saved"
                }
                else
                {
                    imageView.setImageResource(R.drawable.save_unfilled_large_icon)
                    imageView.tag = "Save"
                }
            }

            override fun onCancelled(p0: DatabaseError)
            {

            }
        })
    }


    private fun addNotification(userId:String, postId: String)
    {
        val notiRef = FirebaseDatabase.getInstance()
            .reference
            .child("Notifications")
            .child(userId)
        val notiMap = HashMap<String , Any>()
        notiMap["userid"] = firebaseUser!!.uid
        notiMap["comment"] = "liked your post"
        notiMap["postid"] = postId
        notiMap["ispost"] = true

        notiRef.push().setValue(notiMap)
    }

}