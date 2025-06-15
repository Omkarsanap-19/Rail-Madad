package com.example.railmadad

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import com.example.railmadad.Admin.adminDashboardActivity
import com.example.railmadad.databinding.ActivityLoginBinding
import com.example.railmadad.viewmodels.authViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class loginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding
    lateinit var authViewModel: authViewModel
    lateinit var status_Dialog: BottomSheetDialog
    private lateinit var googleSignInClient: GoogleSignInClient

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                authViewModel.handleSignInResult(task)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        authViewModel = authViewModel()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.btnLogin.setOnClickListener {
            lifecycleScope.launch {
            binding.bar.visibility = View.VISIBLE
            delay(1000) // Simulate a delay for the progress bar
            val mail = binding.etEmail.text.toString()
            val pass = binding.etPassword.text.toString()
            if (validateInputs(mail, pass)) {

                    authViewModel.cus_login(mail, pass, this@loginActivity)
                    binding.bar.visibility = View.GONE
                }

            }
        }

        binding.tvRegister.setOnClickListener {
            val intent = Intent(this, signupActivity::class.java)
            startActivity(intent)
        }

        binding.tvForgotPassword.setOnClickListener {
            val intent = Intent(this, forgotPass::class.java)
            startActivity(intent)
        }

        binding.gLogin.setOnClickListener {
            binding.bar.visibility = View.VISIBLE
            googleSignInClient.signOut()
            val intent = googleSignInClient.signInIntent
            launcher.launch(intent)
        }


        observeViewModel()


    }

    private fun validateInputs(email: String, password: String): Boolean {
        var isValid = true

        if (email.isEmpty()) {
            binding.etEmail.error = "Email is required"
            binding.bar.visibility = View.GONE
            isValid = false
        }

        if (password.isEmpty()) {
            binding.etPassword.error = "Password is required"
            binding.bar.visibility = View.GONE
            isValid = false
        } else if (password.length < 6) {
            binding.etPassword.error = "Password should be at least 6 characters"
            isValid = false
            binding.bar.visibility = View.GONE
        }

        return isValid
    }

    private fun observeViewModel() {

            authViewModel.loginSuccess.observe(this@loginActivity) { success ->
                CoroutineScope(Dispatchers.Main).launch {
                    if (success !=null) {
                        binding.bar.visibility = View.GONE
                        if (authViewModel.checkUserRoleAndNavigate(success.uid) == null){
                            authViewModel.sendTokenToFirestore(success.uid)
                            showDialogStatus(success.uid, success.email!!)
                        }else{
                            binding.bar.visibility = View.GONE
                            authViewModel.sendTokenToFirestore(success.uid)
                            Toast.makeText(this@loginActivity, "Login Successfully!!", Toast.LENGTH_SHORT).show()
                            authViewModel.navigateBasedOnRole(authViewModel.checkUserRoleAndNavigate(success.uid)!!, this@loginActivity)
                        }
                    }
                }
            }



        authViewModel.loginError.observe(this)
        { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }

        authViewModel.isLoading.observe(this)
        { loading ->
            // Show/hide progress bar based on `loading`
            binding.bar.visibility = if (loading == true) View.VISIBLE else View.GONE
        }
    }



//    // flash screen mein lagana hai
//    override fun onStart() {
//        super.onStart()
//        CoroutineScope(Dispatchers.Main).launch{
//            authViewModel.getuser(this@loginActivity)
//        }
//    }

    fun showDialogStatus(unique: String,email: String) {
        status_Dialog = BottomSheetDialog(this)
        status_Dialog.setContentView(R.layout.role_dialog)
        status_Dialog.setCancelable(true)
        status_Dialog.setCanceledOnTouchOutside(true)

        status_Dialog.show()
        val admin = status_Dialog.findViewById<TextView>(R.id.admin)
        val user = status_Dialog.findViewById<TextView>(R.id.user)

        user?.setOnClickListener {
            binding.bar.visibility = View.VISIBLE
            authViewModel.gLoginToFirestore(unique, email,"user",this)
            status_Dialog.dismiss()
        }

        admin?.setOnClickListener {
            binding.bar.visibility = View.VISIBLE
            authViewModel.gLoginToFirestore(unique, email,"admin",this)
            status_Dialog.dismiss()
        }

        authViewModel.roleLoading.observe(this) {
            if (it == true) {
                binding.bar.visibility = View.GONE
            }
        }

    }




}