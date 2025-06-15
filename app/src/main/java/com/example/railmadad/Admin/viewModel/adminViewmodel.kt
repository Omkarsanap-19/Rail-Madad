package com.example.railmadad.Admin.viewModel


import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.constraintlayout.motion.widget.KeyPosition
import androidx.core.content.ContextCompat.startActivity
import androidx.core.os.bundleOf
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.railmadad.Admin.complaintEdit
import com.example.railmadad.Admin.complaintList
import com.example.railmadad.ApiClient
import com.example.railmadad.Data.Complaint
import com.example.railmadad.Data.Update
import com.example.railmadad.Data.upload_select
import com.example.railmadad.NotificationRequest
import com.example.railmadad.changePass
import com.example.railmadad.complaintDetails
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.collections.mutableListOf


class adminViewmodel: ViewModel() {

    private val database = FirebaseDatabase.getInstance()
    private val fireStore = Firebase.firestore

    val userId = FirebaseAuth.getInstance().currentUser?.uid!!

    private val _arrayPD = MutableLiveData<MutableList<Update>>(mutableListOf<Update>())
    val arrayPD : LiveData<MutableList<Update>> get() = _arrayPD

    private val _arrayFood = MutableLiveData<MutableList<Update>>(mutableListOf<Update>())
    val arrayFood : LiveData<MutableList<Update>> get() = _arrayFood

    private val _arrayClean = MutableLiveData<MutableList<Update>>(mutableListOf<Update>())
    val arrayClean : LiveData<MutableList<Update>> get() = _arrayClean

    private val _arrayStaff = MutableLiveData<MutableList<Update>>(mutableListOf<Update>())
    val arrayStaff : LiveData<MutableList<Update>> get() = _arrayStaff

    private val _arrayTech = MutableLiveData<MutableList<Update>>(mutableListOf<Update>())
    val arrayTech : LiveData<MutableList<Update>> get() = _arrayTech

    private val _arrayTicket = MutableLiveData<MutableList<Update>>(mutableListOf<Update>())
    val arrayTicket : LiveData<MutableList<Update>> get() = _arrayTicket

    private val _arraySafe = MutableLiveData<MutableList<Update>>(mutableListOf<Update>())
    val arraySafe : LiveData<MutableList<Update>> get() = _arraySafe

    val _loadingItem= MutableLiveData<Boolean>(false)
    val loadingItem : LiveData<Boolean> get() =  _loadingItem

    private val _totalStat = MutableLiveData<String>()
    val totalStat : LiveData<String> get() = _totalStat

    private val _resolvedStat = MutableLiveData<String>()
    val resolvedStat : LiveData<String> get() = _resolvedStat

    private val _pendingStat = MutableLiveData<String>()
    val pendingStat : LiveData<String> get() = _pendingStat

    private val _progressStat = MutableLiveData<String>()
    val progressStat : LiveData<String> get() = _progressStat

    val _loadingNotify= MutableLiveData<Boolean>(false)
    val loadingNotify : LiveData<Boolean> get() =  _loadingNotify

     val _notifySwitch = MutableLiveData<Boolean>(true)
    val notifySwitch : LiveData<Boolean> get() = _notifySwitch


//    init {
//        sortCategory()
//
//    }

//    fun sortCategory() {
//        fireStore.collection("complaints").get()
//            .addOnSuccessListener { snapshot ->
//                val pd = mutableListOf<Update>()
//                val food = mutableListOf<Update>()
//                val clean = mutableListOf<Update>()
//                val staff = mutableListOf<Update>()
//                val tech = mutableListOf<Update>()
//                val ticket = mutableListOf<Update>()
//                val safe = mutableListOf<Update>()
//
//                for (snap in snapshot.documents) {
//                    val update = snap.toObject(Update::class.java)
//                    update?.let {
//                        when (it.Category) {
//                            "Punctuality & Delays" -> pd.add(it)
//                            "Food & Catering" -> food.add(it)
//                            "Cleanliness & Hygiene" -> clean.add(it)
//                            "Staff Behavior & Service" -> staff.add(it)
//                            "Technical Issues" -> tech.add(it)
//                            "Ticketing & Seat Allocation" -> ticket.add(it)
//                            "Safety & Security" -> safe.add(it)
//                        }
//                    }
//                }
//
//                _arrayPD.value = pd
//                _arrayFood.value = food
//                _arrayClean.value = clean
//                _arrayStaff.value = staff
//                _arrayTech.value = tech
//                _arrayTicket.value = ticket
//                _arraySafe.value = safe
//            }
//            .addOnFailureListener {
//                Log.e("Error", "Failed to fetch complaints: ${it.message}")
//            }
//    }


    fun getComplaintByStats(context: Context){

        fireStore.collection("complaints").get()
            .addOnSuccessListener { snapshot ->
                 var resolved = 0 ; var pending = 0 ; var progress = 0
                for (snap in snapshot.documents) {
                    val update = snap.toObject(Update::class.java)
                    update?.let {
                        when (update.Status) {
                            "Pending" -> {
                                pending++
                            }
                            "Resolved" -> {
                                resolved++
                            }
                            "In Progress" -> {
                                progress++
                            }
                        }
                    }
                }

                _resolvedStat.value = resolved.toString()
                _pendingStat.value = pending.toString()
                _progressStat.value = progress.toString()
                _totalStat.value = (resolved + pending + progress).toString()

            }
            .addOnFailureListener {
                Log.e("Error", "Failed to fetch complaints: ${it.message}")
                Toast.makeText(context, "Error fetching complaints Stats", Toast.LENGTH_SHORT).show()
            }
    }



    fun sortCategory() {

        _arrayPD.value = mutableListOf()
        _arrayFood.value = mutableListOf()
        _arrayClean.value = mutableListOf()
        _arrayStaff.value = mutableListOf()
        _arrayTech.value = mutableListOf()
        _arrayTicket.value = mutableListOf()
        _arraySafe.value = mutableListOf()


        fireStore.collection("complaints").get()
            .addOnSuccessListener { snapshot->

            for (snap in snapshot.documents) {


                    val update = snap.toObject(Update::class.java)

                    update?.let {
                        when (update.Category) {
                            "Punctuality & Delays" -> {
                                val list = _arrayPD.value?.toMutableList() ?: mutableListOf()
                                list.add(update)
                                _arrayPD.value = list
                            }

                            "Food & Catering" -> {
                                val list = _arrayFood.value?.toMutableList() ?: mutableListOf()
                                list.add(update)
                                _arrayFood.value = list
                            }

                            "Cleanliness & Hygiene" -> {
                                val list = _arrayClean.value?.toMutableList() ?: mutableListOf()
                                list.add(update)
                                _arrayClean.value = list
                            }

                            "Staff Behavior & Service" -> {
                                val list = _arrayStaff.value?.toMutableList() ?: mutableListOf()
                                list.add(update)
                                _arrayStaff.value = list
                            }

                            "Technical Issues" -> {
                                val list = _arrayTech.value?.toMutableList() ?: mutableListOf()
                                list.add(update)
                                _arrayTech.value = list
                            }

                            "Ticketing & Seat Allocation" -> {
                                val list = _arrayTicket.value?.toMutableList() ?: mutableListOf()
                                list.add(update)
                                _arrayTicket.value = list
                            }

                            "Safety & Security" -> {
                                val list = _arraySafe.value?.toMutableList() ?: mutableListOf()
                                list.add(update)
                                _arraySafe.value = list
                            }
                        }
                    }

            }
        }.addOnFailureListener {
            Log.e("Error", "Failed to fetch complaints: ${it.message}")
            }

        Log.d("error PD", _arrayPD.value.size.toString())
        Log.d("error Food", _arrayFood.value.size.toString())

    }


    fun categoryToComplaint(context: Context, position: Int) {

            // Start the activity with the selected complaints
            val intent = Intent(context, complaintList::class.java)
            Log.d("error omkar", "three")
            intent.putExtra("position",position)
            startActivity(context, intent, bundleOf())

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

    fun passTOdetails(context: Context, data: Complaint) {
        val intent = Intent(context, complaintEdit::class.java)
        intent.putExtra("data",data)
        startActivity(context, intent, null)
    }

    fun getFeedbackComplaint(context: Context, complaintId: String, onResult: (DataSnapshot?) -> Unit) {
        val docRef = database.getReference("feedback").child(userId).child(complaintId)

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
                Toast.makeText(context, "Error fetching feedback: ${exception.message}", Toast.LENGTH_SHORT).show()
                onResult(null)
            }
    }

    fun statusChange(context: Context, complaintId: String, newStatus: String,userIdToken: String,lifecycleOwner: LifecycleOwner) {
        val database = FirebaseDatabase.getInstance().getReference("users")
        val updates = hashMapOf<String, Any>(
            "users/$userId/$complaintId/status" to newStatus
        )

        notifySwitch.observe(lifecycleOwner) {
            if (it) {
                val message = "Your complaint (#$complaintId) status has been updated to: $newStatus."
                getTokenFromUserId(message, context,userIdToken)
            }

        }

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (userSnapshot in snapshot.children) {
                    val userId = userSnapshot.key ?: continue

                    // Check if this user has the complaintId
                    val complaintSnapshot = userSnapshot.child(complaintId)
                    if (complaintSnapshot.exists()) {
                        // Found the complaintId under this user, now update the status
                        val statusPath = "users/$userId/$complaintId/status"
                        val updates = hashMapOf<String, Any>(
                            statusPath to newStatus
                        )
                        FirebaseDatabase.getInstance().reference.updateChildren(updates)
                            .addOnSuccessListener {
                                fireStore.collection("complaints").document(complaintId).update("status", newStatus)
                                    .addOnSuccessListener {
                                        Toast.makeText(context, "Status updated successfully", Toast.LENGTH_SHORT).show()

                                        _loadingNotify.value = false
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e("Firestore", "Error updating status in Firestore", e)
                                        Toast.makeText(context, "Error updating status in Firestore: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                                Log.d("FirebaseUpdate", "Status updated successfully for user: $userId")
                            }
                            .addOnFailureListener { e ->
                                Log.e("FirebaseUpdate", "Failed to update status", e)
                            }
                        break // Exit loop since we found and updated it
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseUpdate", "Database error: ${error.message}")
            }
        })

    }

    fun getTokenFromUserId(message: String,context: Context,userIdToken: String) {
        val docRef = fireStore.collection("tokens").document(userIdToken)

        docRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // Document exists, return the token
                    val token = document.get("fcmToken") as? String
                    sendNotification(token ?: "", message,context)
                } else {
                    // Document doesn't exist
                    Toast.makeText(context, "No token found for user ID: $userId", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error fetching document", exception)
                Toast.makeText(context, "Error fetching token: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    fun sendNotification(token: String, message: String,context: Context) {
        Log.d("Notification", "Sending notification...")
        val request = NotificationRequest(
            title = "Complaint Status Updated",
            token = token,
            message = message
        )
        Log.d("NotificationRequest", "Request: $request")

        ApiClient.retrofit.sendNotification(request).enqueue(object : retrofit2.Callback<Map<String, Any>> {
            override fun onResponse(call: retrofit2.Call<Map<String, Any>>, response: retrofit2.Response<Map<String, Any>>) {
                Log.d("NotificationResponse", "Response: ${response.body()}")
                if (response.body()?.get("success") == true) {
                    Toast.makeText(context, "Notification sent successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Failed to send notification", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: retrofit2.Call<Map<String, Any>>, t: Throwable) {
                Log.e("NotificationError", "Error: ${t.message}")
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })

    }





}

