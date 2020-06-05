package Adapter

import Model.Story
import Model.User
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat.getSystemServiceName
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.grindergramapp.AddStoryActivity
import com.example.grindergramapp.MainActivity
import com.example.grindergramapp.R
import com.example.grindergramapp.StoryActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import org.jetbrains.annotations.NotNull
import java.time.temporal.UnsupportedTemporalTypeException

class StoryAdapter(private val mContext: Context,private val mStory:List<Story>)
    :RecyclerView.Adapter<StoryAdapter.ViewHolder>()
{
    inner class ViewHolder(@NonNull itemView: View):RecyclerView.ViewHolder(itemView)
    {

        //StoryItem
        var story_image_seen : CircleImageView?=null
        var story_image : CircleImageView?=null
        var story_username : TextView?=null

        //AddStoryItem
        var add_story_plus_btn : CircleImageView?=null
        var add_story_text : TextView?=null

        init {
            //StoryItem
            story_image_seen = itemView.findViewById(R.id.story_image_seen)
            story_image = itemView.findViewById(R.id.story_image)
            story_username = itemView.findViewById(R.id.story_username)


            //AddStoryItem
            add_story_plus_btn=itemView.findViewById(R.id.story_image_add)
            add_story_text=itemView.findViewById(R.id.add_story_text)
        }

    }

    override fun getItemViewType(position: Int): Int {
        if(position==0)
        {
            return 0
        }
        return 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        if(viewType==0)
        {
            val view = LayoutInflater.from(mContext).inflate(R.layout.add_story_item,parent,false)
            return ViewHolder(view)
        }
        else
        {
             val view = LayoutInflater.from(mContext).inflate(R.layout.story_item,parent,false)
            return ViewHolder(view)
        }
    }

    override fun getItemCount(): Int
    {
        return mStory.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {

        val story =  mStory[position]

        userInfo(holder, story.getuserid(), position)


        if(holder.adapterPosition!==0 )
        {
            seenStory(holder , story.getuserid())
        }

        if(holder.adapterPosition === 0)
        {
            myStory(holder.add_story_text!! , holder.add_story_plus_btn!! , false)
        }
        holder.itemView.setOnClickListener {
            if(holder.adapterPosition ===0)
            {
                myStory(holder.add_story_text!! , holder.add_story_plus_btn!! , true)
            }
            else
            {
                val intent = Intent(mContext, StoryActivity::class.java)
                intent.putExtra("userid", story.getuserid())
                mContext.startActivity(intent)
            }
        }
    }

    private fun userInfo(viewHolder: ViewHolder , userId :String , position: Int)
    {
        val userRef = FirebaseDatabase.getInstance().reference.child("Users").child(userId)
        userRef.addValueEventListener( object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {

                if(p0.exists())
                {
                    val user = p0.getValue<User>(User::class.java)

//                    if (user != null) {
//                        user.setImage("https://firebasestorage.googleapis.com/v0/b/grindergram-ffffe.appspot.com/o/Default%20Images%2Fprofile.png?alt=media&token=d3bfb692-ad7c-4441-9cde-65a722f1ce2e");
//                        Log.e("pathis","pathis" + user.getImage())
  //                  };
                    if (user != null) {
                        Picasso.get().load(user.getImage()).placeholder(R.drawable.profile).into(viewHolder.story_image)
                       // Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/grindergram-ffffe.appspot.com/o/Default%20Images%2Fprofile.png?alt=media&token=d3bfb692-ad7c-4441-9cde-65a722f1ce2e").placeholder(R.drawable.profile).into(viewHolder.story_image)

                    }

                    if(position!==0)
                    {
                        if (user != null) {
                            Picasso.get().load(user.getImage()).placeholder(R.drawable.profile).into(viewHolder.story_image_seen)
                            //Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/grindergram-ffffe.appspot.com/o/Default%20Images%2Fprofile.png?alt=media&token=d3bfb692-ad7c-4441-9cde-65a722f1ce2e").placeholder(R.drawable.profile).into(viewHolder.story_image)

                        }
                        if (user != null) {
                            viewHolder.story_username!!.text = user.getUsername()
                        }
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }


    private fun seenStory(viewHolder: ViewHolder , userId: String)
    {
        val storyRef = FirebaseDatabase.getInstance().reference
            .child("Story").child(userId)

        storyRef.addValueEventListener(object :ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot) {
                var i = 0
                for (snapshot in p0.children)
                {
                    if (!snapshot.child("views")
                            .child(FirebaseAuth.getInstance().currentUser!!.uid)
                            .exists() && System.currentTimeMillis() < snapshot.getValue(Story::class.java)!!.getTimeEnd())
                    {
                        i++

                    }
                }

                if(i>0)
                {
                    viewHolder.story_image!!.visibility = View.VISIBLE
                    viewHolder.story_image_seen!!.visibility = View.GONE
                }
                else
                {
                    viewHolder.story_image!!.visibility = View.GONE
                    viewHolder.story_image_seen!!.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(p0: DatabaseError)
            {

            }
        })
    }



    private fun myStory(textView :TextView , imageView: ImageView , click: Boolean)
    {
        val storyRef = FirebaseDatabase.getInstance().reference
            .child("Story").child(FirebaseAuth.getInstance().currentUser!!.uid)

        storyRef.addListenerForSingleValueEvent(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                var counter =0
                val timeCurrent = System.currentTimeMillis()

                for(snapshot in p0.children)
                {
                    val story = snapshot.getValue(Story::class.java)


                    if(timeCurrent>story!!.getTimeStart() && timeCurrent<story!!.getTimeEnd())
                    {
                        counter++
                    }
                }
                if(click)
                {
                    if(counter>0)
                    {
                        val alertDialog = AlertDialog.Builder(mContext).create()
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "View Story")
                        {
                            dialogInterface,which ->

                            val intent = Intent(mContext , StoryActivity::class.java)
                            intent.putExtra("userId",FirebaseAuth.getInstance().currentUser!!.uid)
                            mContext.startActivity(intent)
                            dialogInterface.dismiss()
                        }


                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Add Story")
                        {
                                dialogInterface,which ->

                            val intent = Intent(mContext , AddStoryActivity::class.java)
                            intent.putExtra("userId",FirebaseAuth.getInstance().currentUser!!.uid)
                            mContext.startActivity(intent)
                            dialogInterface.dismiss()
                        }
                        alertDialog.show()

                    }
                    else
                    {
                        val intent = Intent(mContext , AddStoryActivity::class.java)
                        intent.putExtra("userid",FirebaseAuth.getInstance().currentUser!!.uid)
                        mContext.startActivity(intent)
                    }

                }
                else
                {
                    if(counter>0)
                    {
                        textView.text = "My Story"
                        imageView.visibility = View.GONE
                    }
                    else
                    {
                        textView.text = "Add Story"
                        imageView.visibility = View.VISIBLE
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError)
            {

            }
        })
    }
}