package com.example.railmadad.viewmodels

import android.app.Activity
import android.app.Application
import android.app.DatePickerDialog
import android.app.Fragment
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.style.UpdateAppearance
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.railmadad.Data.Complaint
import com.example.railmadad.Data.Update
import com.example.railmadad.Data.User
import com.example.railmadad.complaintDetails
import com.example.railmadad.profileInfo
import com.example.railmadad.room.ComplaintDatabase
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import kotlin.collections.MutableList
import kotlin.coroutines.coroutineContext

class updateViewModel: ViewModel(){

    val database = FirebaseDatabase.getInstance()
    val fireStore = Firebase.firestore
    val userId = FirebaseAuth.getInstance().currentUser?.uid!!

    private val _listAdapter = MutableLiveData<MutableList<Update>>(mutableListOf<Update>())
    val listAdapter : LiveData<MutableList<Update>> get() = _listAdapter

    private val _selectedDate = MutableLiveData<String>()
    val selectDate : LiveData<String> get() = _selectedDate

    private val imgUrl = MutableLiveData<String>(" ")

     val _imgUpload = MutableLiveData<Boolean>(false)
    val imgUpload : LiveData<Boolean> get() = _imgUpload

     val _loadingItem= MutableLiveData<Boolean>(false)
    val loadingItem : LiveData<Boolean> get() =  _loadingItem

    private val _totalStat = MutableLiveData<String>()
    val totalStat : LiveData<String> get() = _totalStat

    private val _resolveStat = MutableLiveData<String>()
    val resolveStat : LiveData<String> get() = _resolveStat

    private val _pendingStat = MutableLiveData<String>()
    val pendingStat: LiveData<String> get() = _pendingStat


    init {

    }

    fun addComplaint(user: Update,application: Application){
        viewModelScope.launch(Dispatchers.IO) {
            val userDao = ComplaintDatabase.getDatabase(application).trackDao()
            userDao.addUser(user)
        }
    }

    fun getComplaintStats(context: Context) {
        val fireStore = FirebaseDatabase.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid!!
        fireStore.getReference("users").child(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var total =0 ; var resolve = 0; var pending = 0
                    // going through each complaintID
                    Log.d("error complaint","chal on data change toh hua")
                    for (snap in snapshot.children) {
                        Log.d("error complaint","in for loop")
                        val data = snap.getValue(Update::class.java)
                        data?.let {
                            total++
                            when (it.Status) {
                                "Resolved" -> resolve++
                                "Pending" -> pending++
                            }
                        }

                    }

                    _totalStat.value = total.toString()
                    _resolveStat.value = resolve.toString()
                    _pendingStat.value = pending.toString()


                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()

                }
            })

    }



    suspend fun getcomplaintUpdateToUser(context: Context) {
        database.getReference("users").child(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // going through each complaintID
                    Log.d("error complaint","chal on data change toh hua")
                    for (snap in snapshot.children) {
                        Log.d("error complaint","in for loop")
                        val data = snap.getValue(Update::class.java)

                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
                }
            })

    }

    fun fetchComplaintById(complaintId: String, onResult: (DocumentSnapshot?) -> Unit) {
        val docRef = fireStore.collection("complaints").document(complaintId)

        docRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // Document exists, return the data
                    onResult(document)
                } else {
                    // Document doesn't exist
                    onResult(null)
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error fetching document", exception)
                onResult(null)
            }
    }

    suspend fun getUserName(): String {
        val user = FirebaseAuth.getInstance().currentUser
        val userId = user?.uid ?: return "User!"

        return try {
            val documentSnapshot = fireStore
                .collection("users")
                .document(userId)
                .get()
                .await()

            val userData = documentSnapshot.toObject(User::class.java)
            userData?.name?.takeIf { it.isNotBlank() }
                ?: user?.displayName?.takeIf { it.isNotBlank() }
                ?: "User!"
        } catch (e: Exception) {
            Log.e("Firestore", "Error fetching user data", e)
            user?.displayName?.takeIf { it.isNotBlank() } ?: "User!"
        }
    }



    fun passTOdetails(context: Context, data: Complaint) {
        val intent = Intent(context, complaintDetails::class.java)
        intent.putExtra("data",data)
        startActivity(context, intent, null)
    }

    fun showDatePicker(context: Context) {
        val calendar = Calendar.getInstance() // Make sure calendar is initialized

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            context,
            { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                _selectedDate.value = formattedDate
            },
            year,
            month,
            day
        )

        // 🔒 Prevent future dates
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()

        datePickerDialog.show()
    }

    fun saveUser( name: String, phone: String, dob: String,context: Context) {
        val user = User(
            name = name,
            phone = phone,
            dob = dob,
            profileImageUrl = imgUrl.value
        )
        fireStore.collection("users").document(userId).set(user)
            .addOnSuccessListener {
                _imgUpload.value = false
                Toast.makeText(context, "User data saved successfully", Toast.LENGTH_SHORT).show()
                Log.d("Firebase", "User data saved successfully")
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error saving user data: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("Firebase", "Error saving user data", e)
            }
    }

    fun fetchUserData(onResult: (User?) -> Unit) {
        fireStore.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val user = document.toObject(User::class.java)
                    onResult(user)
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Error fetching user data", e)
                onResult(null)
            }
    }

    fun uploadImageToCloudinary(uri: Uri,context: Context){
        MediaManager.get().upload(uri).callback(object : UploadCallback {
            override fun onStart(requestId: String) {}

            override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                Log.d("Cloudinary", "Upload progress: $bytes/$totalBytes")
            }

            override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                Log.d("Cloudinary", "Upload successful: $resultData")
                val url = resultData["secure_url"] as? String
                if (url != null) {
                    Log.d("Cloudinary", "Upload successful. URL: $url")
                    Toast.makeText(context, "Image Uploaded successfully", Toast.LENGTH_SHORT).show()
                    imgUrl.value = url
                    _imgUpload.value = true
                } else {
                    Toast.makeText(context, "Upload is unsuccessful", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onError(requestId: String, error: ErrorInfo) {
                Log.e("Cloudinary", "Upload failed: ${error.description}")
            }

            override fun onReschedule(requestId: String, error: ErrorInfo) {
                Log.w("Cloudinary", "Upload rescheduled: ${error.description}")
            }
        }).dispatch()

    }

    fun getEmail(): String{
        val user = FirebaseAuth.getInstance().currentUser
        return user?.email ?: "No email found"
    }





}