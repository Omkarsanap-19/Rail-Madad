package com.example.railmadad

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.os.postDelayed
import androidx.core.splashscreen.SplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.cloudinary.android.MediaManager
import com.example.railmadad.Admin.adminDashboardActivity
import com.example.railmadad.databinding.ActivityLoginBinding
import com.example.railmadad.databinding.ActivityMainBinding
import com.example.railmadad.frags.dashboardFragment
import com.example.railmadad.viewmodels.authViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    var splashIndi: Boolean = false
    lateinit var binding: ActivityMainBinding
    val viewModel: authViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            installSplashScreen()
        } else null

        // Initially set to false, so splash is kept
        var keepSplash = true

        splashScreen?.setKeepOnScreenCondition { keepSplash }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Dexter.withContext(applicationContext)
                .withPermission(Manifest.permission.POST_NOTIFICATIONS)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(p0: PermissionGrantedResponse?) {}

                    override fun onPermissionDenied(p0: PermissionDeniedResponse?) {}
                    // jar ekda deny keli tar kay karnar tyasathi , continue karel parat request process ani .check() is like .build() sarakh intialize karnya sathi process
                    override fun onPermissionRationaleShouldBeShown(
                        p0: PermissionRequest?,
                        p1: PermissionToken?
                    ) {
                        p1?.continuePermissionRequest()
                    }
                }).check()

        }

        // Launch splash logic
        lifecycleScope.launch {
            val user = viewModel.isUserLoggedIn()
            if (user == null) {
                keepSplash = false
                startActivity(Intent(this@MainActivity, loginActivity::class.java))
                finish()
            } else {
                val role = viewModel.checkUserRoleAndNavigate(user)
                keepSplash = false
                when (role) {
                    "admin" -> {
                        startActivity(Intent(this@MainActivity, adminDashboardActivity::class.java))
                        finish()
                    }
                    "user" -> {
                        // continue showing main UI normally
                        setupUI()
                    }
                    else -> {
                        Toast.makeText(this@MainActivity, "Invalid role", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@MainActivity, loginActivity::class.java))
                        finish()
                    }
                }
            }
        }





    }


    fun splashWork(){
        if (viewModel.isUserLoggedIn()==null){
            val intent = Intent(this, loginActivity::class.java)
            splashIndi = true
            startActivity(intent)
        }else if (viewModel.isUserLoggedIn() != null) {
            lifecycleScope.launch {
                val role = viewModel.checkUserRoleAndNavigate(viewModel.isUserLoggedIn()!!)
                when (role) {
                    "admin" -> {
                        val intent = Intent(this@MainActivity, adminDashboardActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        splashIndi = true
                        startActivity(intent)

                    }
                    "user" -> {
                        val intent = Intent(this@MainActivity, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        splashIndi = true
                        startActivity(intent)

                    }
                    else -> {
                        Toast.makeText(this@MainActivity, "Invalid user role", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@MainActivity, loginActivity::class.java)
                        splashIndi = true
                        startActivity(intent)
                    }
                }
            }
        }


    }

    private fun setupUI() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNav.setupWithNavController(navController)

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (!navController.popBackStack()) {
                    finishAffinity()
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
    }





}