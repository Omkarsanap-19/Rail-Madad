# Rail Madad - AI-Powered Railway Complaint Resolution App
Rail Madad is an AI-powered complaint resolution app that enhances the Indian Railways' grievance system. It uses AI for image recognition, text extraction, classification, prioritization, and smart routing — even for complaints submitted via images or videos. This significantly reduces manual effort, speeds up resolution times, and improves overall user satisfaction for both passengers and railway administrators.
## 📑 Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Setup and Installation](#setup-and-installation)
- [Usage](#usage)
- [Screenshots](#screenshots)
- [Contributing](#contributing)
- [License](#license)
- [Contact](#contact)



---

## ✨ Features

### 👤 User Features

#### 🔐 Authentication
- Secure and streamlined access for users
- Firebase Authentication for login/sign-up
- Session management with role-based access

#### 🧠 AI-Powered Complaint Submission
- AI categorization of images, videos.
- Sentiment analysis from feedback

#### 🖼️ Media Upload & Playback
- Upload images, videos with complaints
- Integrated ExoPlayer for video playback
- Cloudinary integration for secure media storage

#### 📦 Complaint Tracking
- Live tracking of complaint status
- Notifications for every complaint update
- Unique complaint ID generation for easy reference

#### 🤖 AI Assistant
- Context-aware suggestions during complaint registration
- Help and support via conversational AI

#### 🎨 UI & UX Enhancements
- Material Design components
- Dark mode support

#### 📬 Feedback
- Feedback form with sentiment analysis
- Helps improve the quality of service

#### 🔔 Notifications
- Firebase Cloud Messaging (FCM) integration
- Notifications for new complaints, status changes.

---

### 🛠️ Admin Features

#### 📥 Complaint Management
- View and filter complaints
- Access complaint media and metadata
- Receive real-time user submissions

#### 🟢 Status Updates
- Update status: Pending → In Progress → Resolved
- Notify users of updates in real time

#### 📊 Data Analytics
- Department-wise resolution performance
- Category-wise complaint breakdown (e.g., cleanliness, staff behavior)
- Frequent complaint types to understand recurring issues

#### 🧠 Feedback Analysis
- Analyze user-submitted feedback using AI
- Generate satisfaction score per complaint
  
---

## Tech Stack

| Category                | Technologies                                                                 |
|-------------------------|------------------------------------------------------------------------------|
| **Programming Languages**   | Kotlin, XML                                                               |
| **Frameworks & Architecture** | Android Studio, MVVM                                                  |
| **Machine Learning & AI**   | Google ML Kit (Image Labeling, OCR) <br>Gemini AI <br>Hugging Face Inference API |
| **Media Handling**         | ExoPlayer <br>Cloudinary <br>Glide                                  |
| **Networking & APIs**      | Retrofit <br>Render <br>Node.js + Express                             |
| **Database**               | Firebase Realtime Database <br>Firebase Firestore <br>Room DB      |
| **Authentication**         | Firebase Authentication                                                  |
| **Notifications**          | Firebase Cloud Messaging (FCM)                                         |
| **Charts & Analytics**     | MP Android Charts                                                        |
| **Permissions & Utilities**| Dexter                                                                   |

---

## 🚀 Setup and Installation

### 1️⃣ Clone the Repository

```bash
git clone https://github.com/Omkarsanap-19/Rail-Madad.git
```

Open Android Studio → **File** → **Open** → Select the cloned project folder.

### 2️⃣ Configure Firebase

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Select your project or create a new one
3. Navigate to **Project Settings** → **General**
4. Download the `google-services.json` file
5. Place it inside the `/app` directory of your project

✅ This will enable Firebase Authentication, Firestore, Realtime Database, and FCM.

### 3️⃣ Set Up Cloudinary

1. Create a Cloudinary account: https://cloudinary.com
2. From your Dashboard, get the following credentials:
   - `cloud_name`
   - `api_key`
   - `api_secret`

3. Open `MyApplication.kt` (inside your base package, e.g., `com.example.railmadad`)
4. Initialize Cloudinary inside the `onCreate()` method:

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val config = HashMap<String, String>()
        config["cloud_name"] = "your_cloud_name"
        config["api_key"] = "your_api_key"
        config["api_secret"] = "your_api_secret"
        MediaManager.init(this, config)
    }
}
```

### 🤖 4️⃣ Set Up Gemini AI (Google AI)

1. Go to [Google AI Studio](https://aistudio.google.com/)
2. Create a project and generate a Gemini API Key
3. In your code (e.g., `GeminiChatService.kt`), locate where you instantiate the `GenerativeModel`
4. Add your API key like this:

```kotlin
val generativeModel = GenerativeModel(
    modelName = "gemini-1.5-flash",
    apiKey = "YOUR_GEMINI_API_KEY"
)
```

### 🧠 5️⃣ Set Up Hugging Face Inference API

1. Go to [Hugging Face](https://huggingface.co/) and log in
2. Navigate to your **Settings** → **Access Tokens** and generate a new token
3. In your Kotlin class where you perform the API call (e.g., `RetrofitInstance.kt`), include the token:



### Run the App
1. Sync your project with Gradle files
2. Build and run the app on an emulator or physical device
3. Ensure all permissions are granted for notification, storage, and internet access

---

## 📱 Usage

### 👤 For Users

- Login with your email using Firebase Authentication.
- Submit complaints by uploading images, videos, along with an optional description.
- Track complaint status in real-time: `Pending → In Progress → Resolved`.
- Get notifications for updates via Firebase Cloud Messaging (FCM).
- Use the AI Assistant for help or general queries.
- Submit feedback after resolution to help improve services.


### 🛠️ For Admins

- Login to access the admin dashboard.
- Review complaints with AI-generated categories
- Update complaint statuses and notify users in real-time.
- Access analytics for complaint trends, resolution performance, and heatmaps.
- View feedback sentiment analysis for better planning.

---

## 📸 Screenshots

<p float="left">
    <img src="https://github.com/user-attachments/assets/37272b8b-01a7-443b-b564-e7ed599ce5d1" width="250" />
  <img src="https://github.com/user-attachments/assets/ba265275-82e1-466a-ba81-ad7b12270326" width="250" />
  <img src="https://github.com/user-attachments/assets/1395794b-63ff-4ac2-9617-c05bc9485cc2" width="250" />
</p>

<p float="left">
  <img src="https://github.com/user-attachments/assets/3aa5d83b-88d4-4c3a-99e5-f1215493a278" width="250" />
  <img src="https://github.com/user-attachments/assets/97b0c549-eb21-4f22-b09d-a7365e14300c" width="250" />
  <img src="https://github.com/user-attachments/assets/c46cc429-b72d-40d9-be53-ab8c21cb01af" width="250" />
</p>

<p float="left">
  <img src="https://github.com/user-attachments/assets/9967ba6c-8e3a-48f5-bec1-36d7d8888502" width="250" />
  <img src="https://github.com/user-attachments/assets/23c12fb5-7a80-4d47-b2e3-4876a02f636a" width="250" />
  <img src="https://github.com/user-attachments/assets/7358c206-2313-4fd3-9f86-aea3b9b86ed8" width="250" />
</p>

<p float="left">
  <img src="https://github.com/user-attachments/assets/0e0fc5fd-9297-4198-919b-a20912ef7e6a" width="250" />
  <img src="https://github.com/user-attachments/assets/65692d85-6b8c-4aa6-a0c2-3bcc582244f4" width="250" />
  <img src="https://github.com/user-attachments/assets/b0401e62-b9a3-4154-8a0e-c555f3831c28" width="250" />
</p>




---

## 🤝 Contributing

Contributions are welcome and appreciated! Whether it's reporting bugs, suggesting features, or submitting pull requests — your input helps make this project better for everyone.

### 🧩 How to Contribute

1. **Fork the repository**
2. **Create a new branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```
3. **Make your changes**
4. **Commit your changes**
   ```bash
   git commit -m "Add: Brief description of your changes"
   ```
5. **Push to your branch**
   ```bash
   git push origin feature/your-feature-name
   ```
6. **Open a Pull Request** on the main repository with a clear description of your changes

### 🛠️ Contribution Areas

You can contribute to any of the following areas:

- **New features** (AI improvements, UI enhancements, etc.)
- PDF report generation of all complaints
- Multi-language support for accessibility

-----

## 🔖 License

This project is licensed under the MIT License. See the [MIT](https://github.com/Omkarsanap-19/Rail-Madad/blob/master/LICENSE) file for details.

## 📬 Contact

For questions, suggestions, or collaborations, feel free to reach out:

- 📧 Email: [omkar@gmail.com](mailto:omkar.sanap019@gmail.com)
- 🐙 GitHub: [@Omkarsanap-19](https://github.com/Omkarsanap-19/)
- 💼 LinkedIn: [Omkar Sanap](https://www.linkedin.com/in/omkar-sanap-app/)

We'd love to hear from you!

