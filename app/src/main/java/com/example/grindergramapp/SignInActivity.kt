package com.example.grindergramapp

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.android.synthetic.main.activity_sign_in.sign_up_btn
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignInActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        sign_up_btn.setOnClickListener {
            startActivity(Intent(this,SignUpActivity::class.java))



            login_btn.setOnClickListener {
                loginUser()
            }
        }
    }

    private fun loginUser() {

        val email    = email_login.text.toString()
        val password = password_login.text.toString()


        when
        {
            TextUtils.isEmpty(email) -> Toast.makeText(this,"Email is required", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(password) -> Toast.makeText(this,"Password is required", Toast.LENGTH_LONG).show()

            else ->
            {
                val progressdialog = ProgressDialog(this@SignInActivity)
                progressdialog.setTitle("Login")
                progressdialog.setMessage("Almost done, please wait")

                //so if user click on anywhere the proces doenst get cancelled
                progressdialog.setCanceledOnTouchOutside(false)
                progressdialog.show()


                val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

                mAuth.signInWithEmailAndPassword(email , password).addOnCompleteListener {task->
                    if(task.isSuccessful)
                    {
                        progressdialog.dismiss()

                        val intent = Intent(this@SignInActivity,MainActivity :: class.java)

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
    }

    override fun onStart() {
        super.onStart()
        if (FirebaseAuth.getInstance().currentUser !=null)
        {
            val intent = Intent(this@SignInActivity,MainActivity :: class.java)

            //adding flag so the user cannot go to login page until or unless he clicks on logout
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }
}
