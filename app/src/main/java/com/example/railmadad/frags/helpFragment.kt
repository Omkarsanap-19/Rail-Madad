package com.example.railmadad.frags

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.railmadad.R
import com.example.railmadad.adapter.ChatAdapter
import com.example.railmadad.databinding.FragmentHelpBinding
import com.example.railmadad.databinding.FragmentUpdateBinding
import com.example.railmadad.viewmodels.ChatViewModel
import kotlin.compareTo
import kotlin.getValue
import kotlin.text.clear
import kotlin.text.compareTo


class helpFragment : Fragment() {

    private var _helpBinding: FragmentHelpBinding? = null
    private val binding get() = _helpBinding!!
    private lateinit var chatAdapter: ChatAdapter
    private val viewModel: ChatViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _helpBinding = FragmentHelpBinding.inflate(inflater, container, false)

        val view =  binding?.root

        setupRecyclerView()
        setupClickListeners()
        observeViewModel()

        return view
    }

//    private fun setupWindowInsets() {
//        // Handle window insets to adjust layout when keyboard appears
//        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
//            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
//            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//
//            // Adjust bottom margin of the input container based on keyboard height
//            val layoutParams = binding.inputContainer.layoutParams as ViewGroup.MarginLayoutParams
//            layoutParams.bottomMargin = imeInsets.bottom
//            binding.inputContainer.layoutParams = layoutParams
//
//            // Adjust padding for system bars
//            v.setPadding(
//                systemBarsInsets.left,
//                systemBarsInsets.top,
//                systemBarsInsets.right,
//                0 // Don't add bottom padding as we handle it with margin
//            )
//
//            // Scroll to bottom when keyboard appears/disappears
//            if (::chatAdapter.isInitialized && chatAdapter.itemCount > 0) {
//                binding.recyclerViewChat.post {
//                    binding.recyclerViewChat.scrollToPosition(chatAdapter.itemCount - 1)
//                }
//            }
//
//            insets
//        }
//    }

    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter()
        binding.recyclerViewChat.apply {
            adapter = chatAdapter
            layoutManager = LinearLayoutManager(requireContext()).apply {
                stackFromEnd = true
            }
            // Add padding to show last message above input area
            clipToPadding = false
        }
    }

    private fun setupClickListeners() {
        binding.buttonSend.setOnClickListener {
            sendMessage()
        }

        binding.editTextMessage.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendMessage()
                true
            } else false
        }

        // Handle focus changes to scroll to bottom
        binding.editTextMessage.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && ::chatAdapter.isInitialized && chatAdapter.itemCount > 0) {
                binding.recyclerViewChat.post {
                    binding.recyclerViewChat.scrollToPosition(chatAdapter.itemCount - 1)
                }
            }
        }
    }

    private fun observeViewModel() {
        viewModel.messages.observe(viewLifecycleOwner) { messages ->
            chatAdapter.submitList(messages) {
                if (messages.isNotEmpty()) {
                    binding.recyclerViewChat.post {
                        binding.recyclerViewChat.scrollToPosition(messages.size - 1)
                    }
                }
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.buttonSend.isEnabled = !isLoading
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), "Error: $it", Toast.LENGTH_SHORT).show()
                viewModel.clearError()
            }
        }
    }

    private fun sendMessage() {
        val message = binding.editTextMessage.text.toString()
        if (message.trim().isNotEmpty()) {
            viewModel.sendMessage(message)
            binding.editTextMessage.text?.clear()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _helpBinding = null
    }

}