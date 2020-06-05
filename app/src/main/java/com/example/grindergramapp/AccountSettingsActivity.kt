package com.example.grindergramapp

import Model.User
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_account_settings.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.android.synthetic.main.fragment_profile.view.full_name_profile_frag
import kotlin.coroutines.Continuation

class AccountSettingsActivity : AppCompatActivity() {

    private lateinit var firebaseUser: FirebaseUser
    private var checker = ""
    private var myUri = ""
    private var imageUri: Uri? = null
    private  var storageProfilePicRef:StorageReference?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_settings)


        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        storageProfilePicRef = FirebaseStorage.getInstance().reference.child("Profile Pictures")

        logout_btn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            val intent = Intent(this@AccountSettingsActivity, SignInActivity::class.java)

            //adding flag so the user cannot go to login page until or unless he clicks on logout
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        change_img_text_btn.setOnClickListener {

            checker = "clicked"
            CropImage.activity()
                .setAspectRatio(1, 1)
                .start(this@AccountSettingsActivity)
        }
        save_info_profile_btn.setOnClickListener {
            if (checker == "clicked") {
                uploadImageAndUpdateInfo()
            } else {
                updateUserInfoOnly()
            }
        }


        userInfo()
    }

    //===================to get a crop image =======================================
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null)
        {
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
            profile_img_view.setImageURI(imageUri)
        }
    }


    private fun updateUserInfoOnly() {

        when {
            full_name_profile_frag.text.toString() == "" -> {
                Toast.makeText(this, "Field cant be empty", Toast.LENGTH_LONG).show()
            }
            username_profile_frag.text.toString() == "" -> {
                Toast.makeText(this, "Field cant be empty", Toast.LENGTH_LONG).show()
            }
            bio_profile_frag.text.toString() == "" -> {
                Toast.makeText(this, "Field cant be empty", Toast.LENGTH_LONG).show()
            }
            else -> {
                val userRef =
                    FirebaseDatabase.getInstance().reference.child("Users").child("Users")
                val usersMap = HashMap<String, Any>()
                usersMap["fullname"] = full_name_profile_frag.text.toString().toLowerCase()
                usersMap["username"] = username_profile_frag.text.toString().toLowerCase()
                usersMap["bio"] = bio_profile_frag.text.toString().toLowerCase()

                userRef.child(firebaseUser.uid).updateChildren(usersMap)

                Toast.makeText(
                    this,
                    "Account have been updated successfully created",
                    Toast.LENGTH_LONG
                ).show()
                val intent = Intent(this@AccountSettingsActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun userInfo() {
        val userRef = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser.uid)
        userRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot) {

                if (p0.exists())
                {
                    val user = p0.getValue<User>(User::class.java)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(profile_img_view)
                    username_profile_frag.setText(user!!.getUsername())
                    full_name_profile_btn.setText(user!!.getFullname())
                    bio_profile_frag.setText(user!!.getBio())
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }


    private fun uploadImageAndUpdateInfo() {

        when {
            full_name_profile_frag.text.toString() == "" -> {
                Toast.makeText(this, "Field cant be empty", Toast.LENGTH_LONG).show()
            }
            username_profile_frag.text.toString() == "" -> {
                Toast.makeText(this, "Field cant be empty", Toast.LENGTH_LONG).show()
            }
            bio_profile_frag.text.toString() == "" -> {
                Toast.makeText(this, "Field cant be empty", Toast.LENGTH_LONG).show()
            }
            imageUri == null -> {
                Toast.makeText(this, "Select image first", Toast.LENGTH_LONG).show()
            }

            else ->
            {
                val progressDialog =ProgressDialog(this)
                progressDialog.setTitle("Account Settings")
                progressDialog.setMessage("Almost done , Please Wait")
                progressDialog.show()

                val fileRef = storageProfilePicRef!!.child(firebaseUser!!.uid + ".jpg")
                var uploadTask:StorageTask<*>
                uploadTask = fileRef.putFile(imageUri!!)
                uploadTask.continueWith(com.google.android.gms.tasks.Continuation <UploadTask.TaskSnapshot, Task<Uri>>{  task ->
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
                        val ref = FirebaseDatabase.getInstance().reference.child("Users")

                        val usersMap = HashMap<String, Any>()
                        usersMap["fullname"] = full_name_profile_frag.text.toString().toLowerCase()
                        usersMap["username"] = username_profile_frag.text.toString().toLowerCase()
                        usersMap["bio"] = bio_profile_frag.text.toString().toLowerCase()
                        usersMap["image"] = myUri
                        ref.child(firebaseUser.uid).updateChildren(usersMap)


                        Toast.makeText(
                            this,
                            "Account have been updated successfully created",
                            Toast.LENGTH_LONG
                        ).show()
                        val intent = Intent(this@AccountSettingsActivity, MainActivity::class.java)
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
