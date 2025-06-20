package com.example.railmadad.repos

import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class authRepository {

    val firebaseAuth = FirebaseAuth.getInstance()

    suspend fun custom_login(email: String, pass: String): Task<AuthResult?> {
        return firebaseAuth.signInWithEmailAndPassword(email,pass)
    }

    suspend fun custom_signUp(email: String, pass: String): Task<AuthResult?> {
        return firebaseAuth.createUserWithEmailAndPassword(email,pass)
        Log.d("error authRepository", "custom_signUp: $email $pass")
    }

    suspend fun forgot_pass(email: String): Task<Void?> {
        return firebaseAuth.sendPasswordResetEmail(email)
    }

     fun getuser(): String? {
        return firebaseAuth.currentUser?.uid
    }

    suspend fun firebaseLoginWithGoogle(account: GoogleSignInAccount): FirebaseUser? {
        return try {
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            val result = withContext(Dispatchers.IO) {
                firebaseAuth.signInWithCredential(credential).await()
            }

            result.user
        } catch (e: Exception) {
            null
        }
    }
// we can't use callback functions in suspend function , because ye callback kr liye nhi rukega
    // isko kaha rukna hai wo batane ke liye hum await() use karte hain
    // first await successfull jhalyavar ch next code run hoil , fail jhala tar catch run hoil
    suspend fun changePassword(oldPass: String, newPass: String): Result<Any> {
        return try {
            val user = firebaseAuth.currentUser
            if (user != null) {
                val credential = EmailAuthProvider.getCredential(user.email!!, oldPass)
                user.reauthenticate(credential).await()
                user.updatePassword(newPass).await()
                Result.success("Password changed successfully")
            } else {
                Result.failure(Exception("User not authenticated"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }

    }


}