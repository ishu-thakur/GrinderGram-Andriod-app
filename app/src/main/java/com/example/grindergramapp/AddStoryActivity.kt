package com.example.grindergramapp

import Adapter.StoryAdapter
import Model.User
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_add_post.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.android.synthetic.main.story_item.*
import java.text.FieldPosition

class AddStoryActivity : AppCompatActivity()
{

    private var myUri = ""
    private var imageUri: Uri? = null
    private  var storageStoryPicRef: StorageReference?=null


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_story)

        storageStoryPicRef = FirebaseStorage.getInstance().reference.child("Story Pictures")


        CropImage.activity()
            .setAspectRatio(9, 16)
            .start(this@AddStoryActivity)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null)
        {
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
            image_post.setImageURI(imageUri)

            uploadStory()
        }

    }

    private fun uploadStory()
    {
        when
        {
            imageUri ==null -> Toast.makeText(this,"Please select the image first.", Toast.LENGTH_LONG).show()

            else->
            {
                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Adding Story")
                progressDialog.setMessage("Almost done , Please Wait")
                progressDialog.show()

                val fileRef = storageStoryPicRef!!.child(System.currentTimeMillis().toString() + ".jpg")

                var uploadTask: StorageTask<*>
                uploadTask = fileRef.putFile(imageUri!!)


                uploadTask.continueWith(com.google.android.gms.tasks.Continuation <com.google.firebase.storage.UploadTask.TaskSnapshot, com.google.android.gms.tasks.Task<android.net.Uri>>{ task ->
                    if(task.isSuccessful)
                    {
                        task.exception?.let {
                            throw it
                            progressDialog.dismiss()
                        }
                    }
                    return@Continuation fileRef.downloadUrl
                }).addOnCompleteListener (OnCompleteListener { task ->

                    if(task.isSuccessful)
                    {
                        val downloadUrl = task.result
                        myUri=downloadUrl.toString()
                        val ref = FirebaseDatabase.getInstance().reference.child("Story").child(FirebaseAuth.getInstance().currentUser!!.uid)
                        val storyId = ref.push().key

                        val timeEnd = System.currentTimeMillis() + 86400000 //one day adding

                        val storyMap = HashMap<String, Any>()
                        storyMap["userid"] = FirebaseAuth.getInstance().currentUser!!.uid
                        storyMap["timestart"] = ServerValue.TIMESTAMP
                        storyMap["timened"] = timeEnd
                        storyMap["imageUrl"] = myUri
                        storyMap["storyid"] = storyId.toString()
                        ref.child(storyId.toString()).updateChildren(storyMap)


                        Toast.makeText(
                            this,
                            "Story has been uploaded successfully",
                            Toast.LENGTH_LONG
                        ).show()
                        val intent = Intent(this@AddStoryActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                        progressDialog.dismiss()
                    }
                    else
                    {
                        progressDialog.dismiss()
                    }

                })
            }
        }
    }

}
