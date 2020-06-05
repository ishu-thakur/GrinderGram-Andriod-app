package com.example.grindergramapp

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.android.synthetic.main.activity_sign_in.sign_up_btn
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)


        sign_lin_up_btn.setOnClickListener {
            startActivity(Intent(this,SignInActivity::class.java))
        }

        signup_btn.setOnClickListener {
            CreateAccount()
        }
    }

    private fun CreateAccount()
    {
        val fullname =full_name_signup.text.toString()
        val username =username_sigmup.text.toString()
        val email    =email_signup.text.toString()
        val password =password_signup.text.toString()

       when
       {
           TextUtils.isEmpty(fullname) -> Toast.makeText(this,"Full name is required",Toast.LENGTH_LONG).show()
           TextUtils.isEmpty(username) -> Toast.makeText(this,"Username is required",Toast.LENGTH_LONG).show()
           TextUtils.isEmpty(email) -> Toast.makeText(this,"Email is required",Toast.LENGTH_LONG).show()
           TextUtils.isEmpty(password) -> Toast.makeText(this,"Password is required",Toast.LENGTH_LONG).show()

           else ->
           {
               //progress status
               val progressdialog = ProgressDialog(this@SignUpActivity)
               progressdialog.setTitle("SignUp")
               progressdialog.setMessage("Almost done, please wait")

               //so if user click on anywhere the proces doenst get cancelled
               progressdialog.setCanceledOnTouchOutside(false)
               progressdialog.show()

               //confirming the person
               val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

               mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener { task ->
                   if(task.isSuccessful)
                   {
                        saveUserInfo(fullname,username,email,progressdialog)

                   }
                   else
                   {
                       val message = task.exception!!.toString()
                       mAuth.signOut()
                       Toast.makeText(this,"Error: $message",Toast.LENGTH_LONG).show()
                       progressdialog.dismiss()
                   }
               }
           }
       }

    }

    private fun saveUserInfo(fullname: String, username: String, email: String , progressdialog : ProgressDialog) {

        val currentUSerId = FirebaseAuth.getInstance().currentUser!!.uid

        val usersRef = FirebaseDatabase.getInstance().reference.child("Users")

        val usersMap =HashMap<String,Any>()

        usersMap["uid"] = currentUSerId
        usersMap["fullname"] = fullname.toLowerCase()
        usersMap["username"] = username.toLowerCase()
        usersMap["email"] = email
        usersMap["bio"] = "Enter your Bio here."
        usersMap["image"] = "https://firebasestorage.googleapis.com/v0/b/grindergram-ffffe.appspot.com/o/Default%20Images%2Fprofile.png?alt=media&token=d3bfb692-ad7c-4441-9cde-65a722f1ce2e"

        usersRef.child(currentUSerId).setValue(usersMap).addOnCompleteListener { task ->
            if(task.isSuccessful)
            {
                progressdialog.dismiss()
                Toast.makeText(this,"Account successfully created",Toast.LENGTH_LONG).show()



                    FirebaseDatabase.getInstance().reference
                        .child("Follow").child(currentUSerId)
                        .child("Following").child(currentUSerId)
                        .setValue(true)




                val intent = Intent(this@SignUpActivity,MainActivity :: class.java)

                //adding flag so the user cannot go to login page until or unless he clicks on logout
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
            else
            {
                val message = task.exception!!.toString()
                FirebaseAuth.getInstance().signOut()
                Toast.makeText(this,"Error: $message",Toast.LENGTH_LONG).show()
                progressdialog.dismiss()
            }
        }
    }
}
