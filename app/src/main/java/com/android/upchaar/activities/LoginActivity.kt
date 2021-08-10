package com.android.upchaar.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.android.upchaar.Constants
import com.android.upchaar.R
import com.android.upchaar.firestore.FirestoreClass
import com.android.upchaar.model.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*

class LoginActivity : BaseActivity(), View.OnClickListener{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

       tv_forgot_password.setOnClickListener(this)
        btn_login.setOnClickListener(this)
        tv_register.setOnClickListener(this)
    }

    fun userLoggedInSuccess(user: User) {
        hideProgressDialog()

        if(user.profileCompleted==0) {
           val intent= Intent(this@LoginActivity,UserProfileActivity::class.java)
            intent.putExtra(Constants.EXTRA_USER_DETAILS, user)
            startActivity(intent)
        } else {
            startActivity(Intent(this@LoginActivity,MainActivity::class.java))
        }
        finish()
    }
    override fun onClick(view: View?) {
        if(view!=null) {
            when(view.id) {
                R.id.tv_forgot_password -> {
                    val intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
                    startActivity(intent)
                }
                R.id.btn_login -> {
                    logInRegisteredUser()

                }

                R.id.tv_register -> {
                    val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                    startActivity(intent)

                }
            }
        }
    }
    private fun validateLoginDetails():Boolean {
        return when {
            TextUtils.isEmpty(etl_email.text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar("Please enter email.",true)
                false
            }

            TextUtils.isEmpty(etl_password.text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar("Please enter password",true)
                false
            }
            else -> {
                //showErrorSnackBar("Your details are valid",false)
                true

            }
        }
    }

    private fun logInRegisteredUser() {
        if(validateLoginDetails()) {
            showProgressDialog(resources.getString(R.string.please_wait))

            val email: String = etl_email.text.toString().trim {it <= ' '}
            val password: String= etl_password.text.toString().trim {it <= ' '}

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener{ task ->

                    if(task.isSuccessful) {
                        
                        //showErrorSnackBar("you are logged in successfully", false)
                        FirestoreClass().getUserDetails(this@LoginActivity)
                    }else {
                        hideProgressDialog()
                        //if the registration is not successful then show error message.
                        showErrorSnackBar(task.exception!!.message.toString(),true)
                    }
                }

        }
    }



}