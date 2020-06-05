package Adapter

import Model.Comment
import Model.User
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.example.grindergramapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.comments_item_layout.view.*
import java.math.MathContext

class CommentAdapter(private  val mContext : Context,
                     private val mComment : MutableList<Comment>?)
    :RecyclerView.Adapter<CommentAdapter.ViewHolder>()

{

    private var firebaseUser : FirebaseUser?= null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentAdapter.ViewHolder
    {
        val view = LayoutInflater.from(mContext).inflate(R.layout.comments_item_layout,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int
    {
        return mComment!!.size
    }

    override fun onBindViewHolder(holder: CommentAdapter.ViewHolder, position: Int)
    {
        firebaseUser = FirebaseAuth.getInstance().currentUser
        val comment = mComment!![position]
        holder.commentTv.text= comment.getComment()

        getUserInfo(holder.imageProfile, holder.usernameTv, comment.getPublisher())
    }



    private fun getUserInfo(imageProfile: CircleImageView, usernameTv: TextView, publisher: String)
    {
        val userRef = FirebaseDatabase.getInstance()
            .reference.child("Users")
            .child(publisher)

        userRef.addValueEventListener(object :ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists())
                {
                    val user = p0.getValue(User::class.java)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(imageProfile)

                    usernameTv.text = user!!.getFullname()
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }



    inner class ViewHolder(@NonNull itemView : View ):RecyclerView.ViewHolder(itemView)
    {
        var imageProfile :CircleImageView
        var usernameTv :TextView
        var commentTv:TextView

        init {
            imageProfile = itemView.findViewById(R.id.user_profile_image_comment)
            usernameTv = itemView.findViewById(R.id.user_name_comment)
            commentTv = itemView.findViewById(R.id.comment_comment)
        }
    }
}
