package com.android.upchaar.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.upchaar.Constants
import com.android.upchaar.GlideLoader
import com.android.upchaar.R
import com.android.upchaar.firestore.FirestoreClass
import com.android.upchaar.model.User
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_register.et_email
import kotlinx.android.synthetic.main.activity_register.et_first_name
import kotlinx.android.synthetic.main.activity_register.et_last_name
import kotlinx.android.synthetic.main.activity_user_profile.*
import java.io.IOException
import java.util.jar.Manifest

class UserProfileActivity : BaseActivity() , View.OnClickListener {
    private lateinit  var mUserDetails: User
    private  var mSelectedImageFileUri: Uri? =null
    private  var mUserProfileImageURL: String =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)


        if(intent.hasExtra(Constants.EXTRA_USER_DETAILS)) {
            //get the user details from intent as a ParcelableExtra
            mUserDetails = intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS)!!
        }

        et_first_name.isEnabled = false
        et_first_name.setText(mUserDetails.firstName)

        et_last_name.isEnabled = false
        et_last_name.setText(mUserDetails.lastName)

        et_email.isEnabled = false
        et_email.setText(mUserDetails.email)

        iv_user_photo.setOnClickListener(this@UserProfileActivity)

        btn_save.setOnClickListener(this@UserProfileActivity)
    }

    override fun onClick(v: View?) {
        if(v!=null) {
            when(v.id) {
                R.id.iv_user_photo -> {

                    // here we are checking permission
                    if(ContextCompat.checkSelfPermission(
                                    this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED)
                    {
                        //showErrorSnackBar("you have already storage permission",false)
                        Constants.showImageChooser(this)
                    } else {
                        // request permission to be granted to this application

                        ActivityCompat.requestPermissions(
                                this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),Constants.READ_STORAGE_PERMISSION_CODE
                        )
                    }
                }

                R.id.btn_save -> {


                    if (validateUserProfileDetails()) {

                        showProgressDialog(resources.getString(R.string.please_wait))

                        if(mSelectedImageFileUri!=null) {
                            FirestoreClass().uploadImageToCloudStorage(this, mSelectedImageFileUri)
                        } else {

                            updateUserProfileDetails()
                        }

                    }
                }
            }
        }
    }

    fun userProfileUpdateSuccess() {
        hideProgressDialog()
        Toast.makeText(this,"Your profile is updated successfully",Toast.LENGTH_SHORT).show()
        startActivity(Intent(this,MainActivity::class.java))
        finish()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode== Constants.READ_STORAGE_PERMISSION_CODE) {
            //if permission is granted
            if(grantResults.isNotEmpty()&& grantResults[0]== PackageManager.PERMISSION_GRANTED) {
               // showErrorSnackBar("the storage permission is granted",false)
                Constants.showImageChooser(this)
            } else {
                // Displaying toast if permission is not granted
                Toast.makeText(this,"Permission is denied",Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK) {
            if(requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {
                if(data!= null) {
                    try {
                        //the  uri of selected image from phone storage
                        mSelectedImageFileUri =data.data!!

                        //iv_user_photo.setImageURI(Uri.parse(selctedImageFileUri.toString()))
                        GlideLoader(this).loadUserPicture(mSelectedImageFileUri!!, iv_user_photo)

                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(this,"Image selection failed",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }


    fun validateUserProfileDetails(): Boolean {
        return when {
            TextUtils.isEmpty(et_mobile_number.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar("please enter mobile number", true)
                false
            }
            else -> {
                //showErrorSnackBar("Your details are valid",false)
                true

            }
        }
    }

    fun imageUploadSuccess(imageURL: String) {

        mUserProfileImageURL = imageURL
        updateUserProfileDetails()
    }

    private fun updateUserProfileDetails() {
        val userHashMap = HashMap<String, Any>()
        val mobileNumber = et_mobile_number.text.toString().trim() { it <= ' ' }

        val gender = if (rb_male.isChecked) {
            Constants.MALE
        } else {
            Constants.FEMALE
        }

        if(mUserProfileImageURL.isNotEmpty()){
            userHashMap[Constants.IMAGE] = mUserProfileImageURL
        }
        if(mobileNumber.isNotEmpty()) {
            userHashMap[Constants.MOBILE]= mobileNumber.toLong()
        }
        userHashMap[Constants.GENDER] = gender
        userHashMap[Constants.COMPLETE_PROFILE] = 1
       // showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().updateUserProfileData(this,userHashMap)
        // showErrorSnackBar("your details are valid",false)
    }
}