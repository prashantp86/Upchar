package com.android.upchaar.firestore

import android.app.Activity
import android.net.Uri
import android.util.Log
import com.android.upchaar.Constants
import com.android.upchaar.activities.LoginActivity
import com.android.upchaar.activities.RegisterActivity
import com.android.upchaar.activities.UserProfileActivity
import com.android.upchaar.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class FirestoreClass {
    private val mFireStore = FirebaseFirestore.getInstance()

    fun registerUser(activity: RegisterActivity, userInfo: User) {
        mFireStore.collection("doctors")
            .document(userInfo.id)
            .set(userInfo, SetOptions.merge()).addOnSuccessListener {
                activity.userRegistrationSuccess()
            }
            .addOnFailureListener{ e->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,"Error while registering user",e)

            }
    }

    fun getCurrentUserID(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser

        var currenUserID = ""
        if(currentUser!=null) {
            currenUserID =currentUser.uid
        }
        return currenUserID
    }

    fun getUserDetails(activity: Activity) {
        mFireStore.collection("doctors")
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName,document.toString())

                val user = document.toObject(User::class.java)!!

                when(activity) {
                    is LoginActivity -> {
                        activity.userLoggedInSuccess(user)
                    }
                }
            }
            .addOnFailureListener{ e->
                when(activity) {
                    is LoginActivity -> {
                        activity.hideProgressDialog()
                    }
                }
            }
    }

    fun updateUserProfileData(activity: Activity, userHashMap: HashMap<String, Any>) {

        mFireStore.collection(Constants.DOCTORS).document(getCurrentUserID())
                .update(userHashMap)
                .addOnSuccessListener {
                    when(activity) {
                        is UserProfileActivity -> {
                            activity.userProfileUpdateSuccess()

                        }
                    }
                }
                .addOnFailureListener{e->
                    when(activity) {
                        is UserProfileActivity -> {
                            activity.hideProgressDialog()
                        }
                    }

                    Log.e(activity.javaClass.simpleName,"error while updating user details",e)
                }
    }

    fun uploadImageToCloudStorage(activity: Activity, imageFileURI: Uri?) {
        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            Constants.USER_PROFILE_IMAGE + System.currentTimeMillis() +"."
                            + Constants.getFileExtension(activity, imageFileURI)
        )
        sRef.putFile(imageFileURI!!).addOnSuccessListener { taskSnapshot ->

            Log.e("Firebase Image URL", taskSnapshot.metadata!!.reference!!.downloadUrl.toString())
            //get the downloadable url from task snapshot

            taskSnapshot.metadata!!.reference!!.downloadUrl
                .addOnSuccessListener { uri ->
                    Log.e("Downloadable image url", uri.toString())
                    when(activity) {
                        is UserProfileActivity ->
                            activity.imageUploadSuccess(uri.toString())
                    }
                }
        }
            .addOnFailureListener { exception ->
                when(activity) {
                    is UserProfileActivity ->
                        activity.hideProgressDialog()
                }

                Log.e(activity.javaClass.simpleName, exception.message, exception)

            }
    }
}