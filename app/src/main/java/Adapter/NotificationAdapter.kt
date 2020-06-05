package Adapter

import Fragments.PostDetailsFragment
import Fragments.ProfileFragment
import Model.Notification
import Model.Post
import Model.User
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.grindergramapp.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_comments.*
import kotlinx.android.synthetic.main.fragment_profile.view.*

class NotificationAdapter( private val mContext: Context,
                           private val mNotification:List<Notification>)
                            :RecyclerView.Adapter<NotificationAdapter.ViewHolder>()
{

    inner class ViewHolder(@NonNull itemView : View):RecyclerView.ViewHolder(itemView)
    {
        var imageView :ImageView
        var profileImage : CircleImageView
        var username : TextView
        var comment: TextView

        init {
            imageView= itemView.findViewById(R.id.notification_post_image)
            profileImage = itemView.findViewById(R.id.notification_profile_image)
            username = itemView.findViewById(R.id.username_notification)
            comment = itemView.findViewById(R.id.comment_notification)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        val view = LayoutInflater.from(mContext).inflate(R.layout.notification_item_layout,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int
    {
        return mNotification.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        val notification = mNotification[position]


        if(notification.getText().equals("started following you"))
        {
            holder.comment.text="started following you"
        }
        else if (notification.getText().equals("liked your post"))
        {
            holder.comment.text="liked your post"
        }
        else if (notification.getText().contains("commented:"))
        {
            holder.comment.text=notification.getText().replace("commented:","commented: ")
        }
        else
        {
            holder.comment.text=notification.getText()
        }



        userInfo(holder.profileImage, holder.username, notification.getuserId())



        if(notification.getisPost())
        {
            holder.profileImage.visibility=View.GONE
            getpostImage(holder.profileImage , notification.getPostid())
        }
        else
        {
            holder.profileImage.visibility = View.GONE
        }




        holder.itemView.setOnClickListener {
            if(notification.getisPost())
            {
                val editor = mContext.getSharedPreferences("PRESS",Context.MODE_PRIVATE).edit()

                editor.putString("postId",notification.getPostid())

                editor.apply()

                (mContext as FragmentActivity).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, PostDetailsFragment()).commit()
            }
            else
            {
                val editor = mContext.getSharedPreferences("PRESS",Context.MODE_PRIVATE).edit()

                editor.putString("profileId",notification.getuserId())

                editor.apply()

                (mContext as FragmentActivity).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, ProfileFragment()).commit()
            }
        }


    }



    private fun userInfo(imageView: ImageView, userName :TextView , publisherId :String)
    {
        val userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(publisherId)
        userRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {

                if(p0.exists())
                {
                    val user = p0.getValue<User>(User::class.java)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(imageView)
                    userName.text =user!!.getUsername()

                }}

            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }


    private fun getpostImage(imageView: ImageView, postId:String) {
        val postRef = FirebaseDatabase.getInstance()
            .reference.child("Posts")
            .child(postId)

        postRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {

                if (p0.exists())
                {
                    val post = p0.getValue<Post>(Post::class.java)

                    val image = p0.value.toString()

                    Picasso.get().load (post!!.getPostimage()).placeholder(R.drawable.profile).into(imageView)

                }
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

}
