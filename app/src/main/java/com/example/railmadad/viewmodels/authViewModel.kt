package com.example.railmadad.viewmodels

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.railmadad.Admin.adminDashboardActivity
import com.example.railmadad.MainActivity
import com.example.railmadad.loginActivity
import com.example.railmadad.repos.authRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class authViewModel: ViewModel() {

    val repo = authRepository()
    val loginSuccess = MutableLiveData<FirebaseUser?>()
    val loginError = MutableLiveData<String?>()
    val isLoading = MutableLiveData<Boolean>()


    private val _roleLoading = MutableLiveData<Boolean>(false)
    val roleLoading : LiveData<Boolean> get() = _roleLoading



    val db = Firebase.firestore

    fun handleSignInResult(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful) {
            val account = task.result
            if (account != null) {
                signInWithGoogle(account)
            } else {
                loginError.value = "Account is null"
            }
        } else {
            loginError.value = task.exception?.message
        }
    }

    private fun signInWithGoogle(account: GoogleSignInAccount) {
        viewModelScope.launch {
            isLoading.value = true
            val result = repo.firebaseLoginWithGoogle(account)
            isLoading.value = false

            if (result != null) {
                loginSuccess.value = result
            } else {
                loginError.value = "Login failed."
            }
        }
    }
    suspend fun cus_login(email:String,pass: String,context: Context){
            repo.custom_login(email, pass).addOnSuccessListener {
                CoroutineScope(Dispatchers.Main).launch {
                    val userId = FirebaseAuth.getInstance().currentUser?.uid
                    sendTokenToFirestore(userId!!)
                    val role = checkUserRoleAndNavigate(userId!!)
                    if (role == null) {
                        Toast.makeText(context, "User role not found", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Login Successfully!!", Toast.LENGTH_SHORT).show()
                        navigateBasedOnRole(role,context)
                    }
                }
//                if (email == "admin@gmail.com" && pass == "admin123") {
//                    val intent = Intent(context, adminDashboardActivity::class.java)
//                    startActivity(context,intent, bundleOf())
//                } else {
//                    val intent = Intent(context, MainActivity::class.java)
//                    startActivity(context,intent, bundleOf())
//                }

            }.addOnFailureListener {
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            }

    }

     suspend fun checkUserRoleAndNavigate(userId: String): String? {
        return try {
            db.collection("roles").document(userId).get().await()
                .let { document ->
                    if (document.exists()) {
                        val role = document.getString("role")
                        return role
                        role

                    } else {
                        return null
                    }
                }
        } catch (e: Exception) {
            return null
        }
    }

     fun navigateBasedOnRole(role: String?,context: Context) {
        when (role) {
            "admin" -> {
                val intent = Intent(context, adminDashboardActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(context,intent, bundleOf())

            }
            "user" -> {
                val intent = Intent(context, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(context,intent, bundleOf())
            }
            else -> {
                Toast.makeText(context, "Invalid user role", Toast.LENGTH_SHORT).show()
            }
        }
    }

    suspend fun cus_signup (email:String,pass: String,context: Context, role: String ){

            repo.custom_signUp(email, pass).addOnSuccessListener {
                Log.d("error signupActivity", "User type selected: $role")
                sendTokenToFirestore(FirebaseAuth.getInstance().currentUser?.uid!!)
                saveUserToFirestore(FirebaseAuth.getInstance().currentUser?.uid!!, email, role,context)

            }.addOnFailureListener {
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            }

    }

     private fun saveUserToFirestore(userId: String, email: String, role: String,context: Context) {
        val user = hashMapOf(
            "email" to email,
            "role" to role
        )
        Log.d("error signupActivity", "User type selected: $role")

        db.collection("roles").document(userId)
            .set(user)
            .addOnSuccessListener {
            Log.d("error signupActivity", "User type saved: $role")
                Toast.makeText(context, "Account created successfully!", Toast.LENGTH_SHORT).show()
                // Navigate based on role
                val intent = Intent(context, loginActivity::class.java)
                startActivity(context,intent, bundleOf())
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error saving user data: ${e.message}", Toast.LENGTH_LONG).show()

            }
    }

    fun gLoginToFirestore(userId: String, email: String, role: String,context: Context) {
        val user = hashMapOf(
            "email" to email,
            "role" to role
        )

        db.collection("roles").document(userId)
            .set(user)
            .addOnSuccessListener {
                _roleLoading.value = true
                Log.d("error signupActivity", "User type saved: $role")
                Toast.makeText(context, "Account created successfully!", Toast.LENGTH_SHORT).show()
                // Navigate based on role
                navigateBasedOnRole(role,context)
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error saving user data: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }



    fun forgot (email:String,context: Context){
        viewModelScope.launch {
            repo.forgot_pass(email).addOnSuccessListener {
                Toast.makeText(context, "Reset Password link has been sent to your registered Email", Toast.LENGTH_SHORT).show()
                val intent = Intent(context, loginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(context,intent, bundleOf())

            }.addOnFailureListener {
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    suspend fun getuser(context: Context){
        if (repo.getuser() != null) {
            val role = checkUserRoleAndNavigate(repo.getuser()!!)
            navigateBasedOnRole(role, context)
        }

    }

    fun isUserLoggedIn(): String? {
        return FirebaseAuth.getInstance().currentUser?.uid
    }

    fun signOut(context: Context) {
        viewModelScope.launch {
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(context, "Signed out successfully", Toast.LENGTH_SHORT).show()
            val intent = Intent(context, loginActivity::class.java)
            startActivity(context,intent, bundleOf())


        }
    }

    fun changePassword(oldPass: String, newPass: String, context: Context) {
        viewModelScope.launch {
            val result = repo.changePassword(oldPass, newPass)
            result.onSuccess {
                Toast.makeText(context, "Password changed successfully", Toast.LENGTH_SHORT).show()
            }.onFailure { exception ->
                Toast.makeText(context, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun sendTokenToFirestore(userId: String) {
        viewModelScope.launch {
            try {
                FirebaseMessaging.getInstance().token.addOnSuccessListener {
                    val userRef = db.collection("tokens").document(userId)
                    val data = mapOf(
                        "fcmToken" to it
                    )
                    userRef.set(data,SetOptions.merge())
                        .addOnSuccessListener {
                            Log.d("authViewModel", "Token updated successfully")
                        }
                        .addOnFailureListener { e ->
                            Log.e("authViewModel", "Error updating token: ${e.message}")
                        }
                }
            } catch (e: Exception) {
                Log.e("authViewModel", "Error updating token: ${e.message}")
            }
        }
    }







}