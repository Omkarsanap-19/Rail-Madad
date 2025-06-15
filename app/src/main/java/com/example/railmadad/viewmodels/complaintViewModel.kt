package com.example.railmadad.viewmodels

import android.app.Application
import android.app.DatePickerDialog
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.railmadad.Data.Complaint
import com.example.railmadad.Data.Feedback
import com.example.railmadad.Data.Image
import com.example.railmadad.Data.SentimentResponse
import com.example.railmadad.Data.Update
import com.example.railmadad.Data.upload_select
import com.example.railmadad.R
import com.example.railmadad.RetrofitInstance
import com.example.railmadad.RetrofitInstance.api
import com.example.railmadad.RetrofitInstance.token
import com.example.railmadad.TextClassificationRequest
import com.example.railmadad.TextClassificationResult
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.firestore
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.devanagari.DevanagariTextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.security.MessageDigest
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class MediaType {
    IMAGE, AUDIO, VIDEO
}

class complaintViewModel(val application: Application): ViewModel() {

    private val calendar = Calendar.getInstance()
    val fireStore = Firebase.firestore

    val userId = FirebaseAuth.getInstance().currentUser?.uid!!



    private val _selectedDate = MutableLiveData<String>()
    val selectDate : LiveData<String> get() = _selectedDate

    private val _currentDate = MutableLiveData<String>()
    val currentDate: LiveData<String> get() = _currentDate

    private val _listAdapter = MutableLiveData<MutableList<upload_select>>(mutableListOf<upload_select>())
    val listAdapter : LiveData<MutableList<upload_select>> get() = _listAdapter

    private val _attachIndi = MutableLiveData<Boolean>(false)
    val attachIndi : LiveData<Boolean> get() = _attachIndi

    private val _imgAttsch = MutableLiveData<List<String>>(emptyList<String>())
    val imgAttach : LiveData<List<String>> get() = _imgAttsch

    private val _progress = MutableLiveData<Boolean>(false)
    val progress : LiveData<Boolean> get() = _progress

    private val _secure = MutableLiveData<String>("")
    val secure : LiveData<String> get() = _secure

    private val _imgData = MutableLiveData<MutableList<Image>>(mutableListOf<Image>())
    val imgData :  LiveData<MutableList<Image>> get() = _imgData

    private val _imgLabel = MutableLiveData<List<String>>(emptyList<String>())

    private val _imgOCR = MutableLiveData<String>(" ")

     val complaintLoad = MutableLiveData<Boolean>(false)
    val complaintLoadStatus: LiveData<Boolean> get() = complaintLoad





    fun showDatePicker(context: Context) {
        val calendar = Calendar.getInstance() // Make sure calendar is initialized

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            context,
            { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                _selectedDate.value = "Selected: $formattedDate"
            },
            year,
            month,
            day
        )

        // 🔒 Prevent future dates
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()

        datePickerDialog.show()
    }



     fun handleAttachment(uri: Uri,context: Context,id: String) {
        val mimeType = context.contentResolver.getType(uri)

        _progress.value = true

        when {
            mimeType?.startsWith("video") == true -> uploadVideo(uri,context,id)
            mimeType?.startsWith("audio") == true -> uploadAudio(uri,context,id)
            mimeType?.startsWith("image") == true -> uploadImage(uri,context,id)
            else -> {
                Toast.makeText(context, "Unsupported file type", Toast.LENGTH_SHORT).show()
                _progress.value = false
            }
        }
    }

    private fun uploadImage(uri: Uri,context: Context,id: String) {
        // Simulate backend upload
        viewModelScope.launch {

//            val bitmap = getBitmapFromUri(context,uri)
//            prepareAndStartImageDescription(bitmap!!,context)
            val image = InputImage.fromFilePath(context, uri)
            imageProcessing(image,id)
            imageOCR(image,id)

            try {
                uploadImageToCloudinary(uri,
                    onSuccess = { url ->
                        Log.d("MyApp", "Image uploaded: $url")
                        val secureUrl = url

                        val imageData = Image(
                            url = secureUrl ,
                            metadata = fetchMediaMetadata(uri, MediaType.IMAGE),
                            fileName = getFileNameFromUri(context, uri),
                            keywords = _imgLabel.value ?: emptyList(),
                            ocrText = _imgOCR.value // OCR text can be set later if needed
                        )

                        addItemAttach(imageData)
                    },
                    onError = { error ->
                        Toast.makeText(context, "Upload failed: $error", Toast.LENGTH_SHORT).show()
                    }
                )

            } catch (e: IOException) {
                Toast.makeText(context, "Error in input image", Toast.LENGTH_SHORT).show()
            }



            Toast.makeText(context, "Image uploaded!", Toast.LENGTH_SHORT).show()
            val img = upload_select(R.drawable.baseline_image_24,getFileNameFromUri(context,uri)!!)
            addItem(img)
            _progress.value = false
            _attachIndi.value = true
        }

    }

    private fun uploadVideo(uri: Uri,context: Context,id: String) {
        viewModelScope.launch {
            val videoFile = getFileFromUri(context, uri)
            videoFile?.let {
                uploadVideoWithMediaManager(context, it.absolutePath, onSuccess = { url->
                    Log.d("MyApp", "Video uploaded: $url")
                    val imageData = Image(
                        url = url,
                        metadata = fetchMediaMetadata(uri, MediaType.VIDEO),
                        fileName = getFileNameFromUri(context, uri),
                        keywords = null,
                        ocrText = null // OCR text can be set later if needed
                    )

                    addItemAttach(imageData)

                    Toast.makeText(context, "Video uploaded!", Toast.LENGTH_SHORT).show()
                    val video = upload_select(R.drawable.baseline_video_file_24,getFileNameFromUri(context,uri)!!)
                    addItem(video)
                    _progress.value = false
                    _attachIndi.value = true

                }, onError = { error ->
                    Toast.makeText(context, "Upload failed: $error", Toast.LENGTH_SHORT).show()

                })
            }

        }
    }

    private fun uploadAudio(uri: Uri,context: Context,id: String) {
        viewModelScope.launch {
            delay(2500)
            val imageData = Image(
                url = " ",
                metadata = fetchMediaMetadata(uri, MediaType.AUDIO),
                fileName = getFileNameFromUri(context, uri),
                keywords = null,
                ocrText = null // OCR text can be set later if needed
            )

            addItemAttach(imageData)
            Toast.makeText(context, "Audio uploaded!", Toast.LENGTH_SHORT).show()
            val audio = upload_select(R.drawable.baseline_audio_file_24,getFileNameFromUri(context,uri)!!)
            addItem(audio)
            _progress.value = false
            _attachIndi.value = true
        }
    }

    private fun getFileNameFromUri(context: Context, uri: Uri): String? {
        return when (uri.scheme) {
            "content" -> {
                val cursor = context.contentResolver.query(uri, null, null, null, null)
                cursor?.use {
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (cursor.moveToFirst()) {
                        cursor.getString(nameIndex)
                    } else "unknown"
                }
            }
            "file" -> {
                File(uri.path!!).name
            }
            else -> "unknown"
        }
    }

    private fun addItem(item: upload_select) {
        val currentList = _listAdapter.value ?: mutableListOf()
        currentList.add(item)
        _listAdapter.value = currentList // 🔥 Trigger LiveData observers
    }

    private fun addItemAttach(item: Image) {
        val currentList = _imgData.value ?: mutableListOf()
        currentList.add(item)
        _imgData.value = currentList // 🔥 Trigger LiveData observers
    }

    private fun imageProcessing(image: InputImage,id: String){

         val options = ImageLabelerOptions.Builder()
            .setConfidenceThreshold(0.6f)
            .build()
        val labeler = ImageLabeling.getClient(options)

        labeler.process(image).addOnSuccessListener {

            val labelList = it.map { it.text }
            _imgLabel.value = labelList
            appendItems(labelList)
//            imageDescriptionToFirebase(labelList,id)
            Log.d("error image ","chal gaya")
        }.addOnFailureListener {
            Log.d("error image ", "can't get labels of image")
        }
    }

    private fun imageOCR(image: InputImage,id: String){
        val recognizer = TextRecognition.getClient(DevanagariTextRecognizerOptions.Builder().build())

        val result = recognizer.process(image).addOnSuccessListener {

            val labelList = it
            _imgOCR.value = labelList.text
//            imageDescriptionToFirebase(labelList,id)
            Log.d("error image ","chal gaya")
        }.addOnFailureListener {
            Log.d("error image ", "can't get labels of image")
        }
    }

    fun generateNumericId(): String {
        val chars = "0123456789"
        return (1..8)
            .map { chars.random() }
            .joinToString("")
    }
    fun appendItems(newItems: List<String>) {
        val currentList = _imgAttsch.value ?: emptyList()
        val updatedList = currentList + newItems // appending
        _imgAttsch.value = updatedList
    }

    fun uploadImageToCloudinary(uri: Uri, onSuccess: (String) -> Unit, onError: (String) -> Unit){
        MediaManager.get().upload(uri).callback(object : UploadCallback {
            override fun onStart(requestId: String) {
                Log.d("Cloudinary", "Upload started: $requestId")
            }

            override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                Log.d("Cloudinary", "Upload progress: $bytes/$totalBytes")
            }

            override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                Log.d("Cloudinary", "Upload successful: $resultData")
                val url = resultData["secure_url"] as? String
                if (url != null) {
                    Log.d("Cloudinary", "Upload successful. URL: $url")
                    onSuccess(url)
                } else {
                    onError("Upload succeeded but no URL returned.")
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

    fun uploadVideoWithMediaManager(context: Context, filePath: String, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        MediaManager.get().upload(filePath)
            .option("resource_type", "video")
            .callback(object : UploadCallback {
                override fun onStart(requestId: String?) {
                    Log.d("Cloudinary", "Upload started...")
                }

                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {
                    val progress = (bytes * 100) / totalBytes
                    Log.d("Cloudinary", "Progress: $progress%")
                }

                override fun onSuccess(requestId: String?, resultData: Map<*, *>) {
                    Log.d("Cloudinary", "Upload success: ${resultData}")
                    Log.d("Cloudinary", "Upload success: ${resultData["url"]}")
                    val url = resultData["secure_url"] as? String
                    if (url != null) {
                        Log.d("Cloudinary", "Upload successful. URL: $url")
                        onSuccess(url)
                    } else {
                        onError("Upload succeeded but no URL returned.")
                    }

                }

                override fun onError(requestId: String?, error: ErrorInfo?) {
                    Log.e("Cloudinary", "Upload error: ${error?.description}")
                }

                override fun onReschedule(requestId: String?, error: ErrorInfo?) {
                    Log.w("Cloudinary", "Upload rescheduled: ${error?.description}")
                }
            }).dispatch()
    }





    fun getFileFromUri(context: Context, uri: Uri): File? {
        val contentResolver = context.contentResolver
        val fileExtension = when (contentResolver.getType(uri)) {
            "video/mp4" -> ".mp4"
            "video/3gpp" -> ".3gp"
            "video/x-matroska" -> ".mkv"
            else -> ".mp4" // default fallback
        }

        val fileName = "temp_video_${System.currentTimeMillis()}$fileExtension"
        val tempFile = File(context.cacheDir, fileName)

        return try {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(tempFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            tempFile
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }


    fun fetchMediaMetadata(uri: Uri, mediaType: MediaType): List<String> {
        val contentResolver = application.contentResolver

        val projection = when (mediaType) {
            MediaType.IMAGE -> arrayOf(
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Images.Media.LATITUDE,
                MediaStore.Images.Media.LONGITUDE
            )
            MediaType.VIDEO -> arrayOf(
                MediaStore.Video.Media.DATE_TAKEN,
                MediaStore.Video.Media.LATITUDE,
                MediaStore.Video.Media.LONGITUDE
            )
            MediaType.AUDIO -> arrayOf(
                MediaStore.Audio.Media.DATE_ADDED
            )
        }

        val cursor = contentResolver.query(uri, projection, null, null, null)
        var result = emptyList<String>()

        cursor?.use {
            if (it.moveToFirst()) {
                when (mediaType) {
                    MediaType.IMAGE, MediaType.VIDEO -> {
                        val dateTaken = it.getLong(it.getColumnIndexOrThrow(
                            if (mediaType == MediaType.IMAGE)
                                MediaStore.Images.Media.DATE_TAKEN
                            else
                                MediaStore.Video.Media.DATE_TAKEN
                        ))

                        val latitude = it.getDouble(it.getColumnIndexOrThrow(
                            if (mediaType == MediaType.IMAGE)
                                MediaStore.Images.Media.LATITUDE
                            else
                                MediaStore.Video.Media.LATITUDE
                        ))

                        val longitude = it.getDouble(it.getColumnIndexOrThrow(
                            if (mediaType == MediaType.IMAGE)
                                MediaStore.Images.Media.LONGITUDE
                            else
                                MediaStore.Video.Media.LONGITUDE
                        ))


                        val geocoder = Geocoder(application, Locale.getDefault())
                        val addresses: List<Address> = geocoder.getFromLocation(latitude, longitude, 1)!!

                        if (addresses.isNotEmpty()) {
                            val city = addresses[0].locality
                            val state = addresses[0].adminArea
                            val country = addresses[0].countryName
                            result  = listOf(Date(dateTaken).toString(),
                                "Location: $city, $state, $country","Type: ${mediaType.name}")

                        }else{
                            result  = listOf(Date(dateTaken).toString(),
                                "Location: Not Available ","Type: ${mediaType.name}")
                        }

                    }

                    MediaType.AUDIO -> {
                        val dateAdded = it.getLong(it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)) * 1000L
                        result  = listOf(Date(dateAdded).toString(),
                            "Location: Not Available ","Type: Audio")
                    }
                }
            }
        }


        return if (result.isEmpty()){
            emptyList()
        } else result

    }




//    @SuppressLint("SuspiciousIndentation")
//    suspend fun prepareAndStartImageDescription(bitmap: Bitmap, context: Context) {
//        // Create an image describer
//        val options = ImageDescriberOptions.builder(context).build()
//         imageDescriber = ImageDescription.getClient(options)
//        // Check feature availability, status will be one of the following:
//        // UNAVAILABLE, DOWNLOADABLE, DOWNLOADING, AVAILABLE
//
//        val featureStatus = imageDescriber.checkFeatureStatus().await()
//            Log.d("feature status","true")
//            if (featureStatus == FeatureStatus.DOWNLOADABLE) {
//                // Download feature if necessary.
//                // If downloadFeature is not called, the first inference request
//                // will also trigger the feature to be downloaded if it's not
//                // already downloaded.
//                imageDescriber.downloadFeature(object : DownloadCallback {
//                    override fun onDownloadStarted(bytesToDownload: Long) { Log.d("feature status","downStart")}
//
//                    override fun onDownloadFailed(e: GenAiException) { Log.d("feature status","downFail")}
//
//                    override fun onDownloadProgress(totalBytesDownloaded: Long) { Log.d("feature status","downPro")}
//
//                    override fun onDownloadCompleted() {
//                        startImageDescriptionRequest(bitmap, imageDescriber)
//                    }
//                })
//            } else if (featureStatus == FeatureStatus.DOWNLOADING) {
//                // Inference request will automatically run once feature is
//                // downloaded.
//                // If Gemini Nano is already downloaded on the device, the
//                // feature-specific LoRA adapter model will be downloaded
//                // very quickly. However, if Gemini Nano is not already
//                // downloaded, the download process may take longer.
//                startImageDescriptionRequest(bitmap, imageDescriber)
//            } else if (featureStatus == FeatureStatus.AVAILABLE) {
//                startImageDescriptionRequest(bitmap, imageDescriber)
//
//            }
//
//
//
//    }

//     fun startImageDescriptionRequest(
//        bitmap: Bitmap,
//        imageDescriber: ImageDescriber
//    ) {
//        // Create task request
//        val imageDescriptionRequest = ImageDescriptionRequest
//            .builder(bitmap)
//            .build()
//         Log.d("image desc","true")
//        // Run inference with a streaming callback
////        val imageDescriptionResultStreaming =
////            imageDescriber.runInference(imageDescriptionRequest) { outputText ->
////                // Append new output text to show in UI
////                // This callback is called incrementally as the description
////                // is generated
////            }
//
////         You can also get a non-streaming response from the request
//         viewModelScope.launch {
//             val imageDescription =
//                 imageDescriber.runInference(imageDescriptionRequest).await().description
//            imageDescriptiontoFirebase(imageDescription)
//             Log.d("description", "$imageDescription")
//
//         }
//
//    }

//    override fun onCleared() {
//        super.onCleared()
//        imageDescriber.close()
//    }






//    fun imageDescriptionToFirebase(descList: List<String>, uniqueID: String) {
//
//        // Use arrayUnion to append images to existing list
//        val updateData = mapOf(
//            "Image Description" to FieldValue.arrayUnion(*descList.toTypedArray()) // Spread the list
//        )
//
//        fireStore.collection("complaints").document(uniqueID)
//            .set(updateData, SetOptions.merge())
//            .addOnSuccessListener {
//                Log.d("Firebase", "Image descriptions appended successfully")
//            }
//            .addOnFailureListener {
//                Log.e("Firebase", "Failed to append images: ${it.message}")
//            }
//    }


    fun complaintTOuser(complaintData: Complaint,context: Context,unique: String, updateData: Update){
        val database = FirebaseDatabase.getInstance().getReference("users").child(userId).child(unique)
        fireStore.collection("complaints").document(unique).set(complaintData).addOnSuccessListener {
            database.setValue(updateData).addOnSuccessListener {
                Log.d("error image ","complaints to user ho gaya")
                complaintLoad.value = false
                Toast.makeText(context, "Complaint registered successfully!!", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Log.d("error image ", it.message.toString() + "complaintTOuser")
            }
        }

    }

    fun feedbackToFirebase(
        unique: String,
        feedback: List<SentimentResponse>,
        context: Context,
        msg: String
    ) {

        val feedSet = Feedback(msg,feedback)

        val database = FirebaseDatabase.getInstance().getReference("feedback").child(userId).child(unique)
        database.setValue(feedSet).addOnSuccessListener {
            Log.d("error image ", "feedback to firebase ho gaya")
            Toast.makeText(context, "Feedback sent successfully!!", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Log.d("error image ", it.message.toString() + "feedbackToFirebase")
        }
    }

     suspend fun submitFeedback(feedback: String,context: Context, ComplaintID: String) {
        try {
            return withContext(Dispatchers.IO) {
                try {
                    val formattedText = "\"$feedback\""

                    val request = TextClassificationRequest(
                        inputs = formattedText
                    )

                    val response = api.textClassification(
                        authorization = "Bearer $token",
                        request = request
                    )

                    if (response.isSuccessful) {
                        val nestedResults = response.body() ?: null

                        Log.d("HuggingFaceRepo", "Raw Response: $nestedResults")

                        // Extract the first (and likely only) array from the nested structure
                        val results = if (nestedResults!!.isNotEmpty()) {
                            nestedResults[0] // Get the first inner array
                        } else {
                            emptyList()
                        }

                        val classifications = results.map { result ->
                            SentimentResponse(
                                label = result.label,
                                score = result.score
                            )
                        }.sortedByDescending { it.score }

                        Log.d("error HuggingFaceRepo", "Processed Classifications: $classifications")

                        feedbackToFirebase(ComplaintID, classifications, context,feedback)

                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.d("error HuggingFaceRepo", "API Error: ${response.code()} - ${response.message()}")
                        Log.d("error HuggingFaceRepo", "Error Body: $errorBody")

                        TextClassificationResult(
                            success = false,
                            classifications = emptyList(),
                            error = "API Error: ${response.code()} - ${response.message()}"
                        )
                    }
                } catch (e: Exception) {
                    Log.d("error HuggingFaceRepo", "Network Error", e)
                    TextClassificationResult(
                        success = false,
                        classifications = emptyList(),
                        error = "Network Error: ${e.message}"
                    )
                }
            }

        } catch (e: Exception) {

        }



    }

    suspend fun feedbackPresent(unique: String, context: Context): Boolean {
        val database = FirebaseDatabase.getInstance().getReference("feedback").child(userId).child(unique)
        var present = false

        return try {
            val doc = database.get().await()
            if (doc.exists()) {
                present = true
                return present
            } else {
                present = false
                return present
            }
        }catch (e: Exception) {
            Toast.makeText(context, "Error checking feedback", Toast.LENGTH_SHORT).show()
            Log.e("error feedback", e.message.toString())
            present = false
            return present
        }

    }




    fun onButtonClicked() {
        val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.getDefault())
        val now = LocalDateTime.now().format(formatter)
        _currentDate.value = now
    }

//
//    fun getBitmapFromUri(context: Context, imageUri: Uri): Bitmap? {
//        return try {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//                val source = ImageDecoder.createSource(context.contentResolver, imageUri)
//                ImageDecoder.decodeBitmap(source)
//            } else {
//                @Suppress("DEPRECATION")
//                MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
//            }
//        } catch (e: IOException) {
//            e.printStackTrace()
//            null
//        }
//    }





}










