package com.handroid.dovemessengerkt.ui.auth.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.handroid.dovemessengerkt.R
import com.handroid.dovemessengerkt.databinding.FragmentForgotPasswordBinding
import com.handroid.dovemessengerkt.ui.auth.viewmodel.AuthViewModel
import com.handroid.dovemessengerkt.util.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotPasswordFragment : Fragment() {

    private var _binding: FragmentForgotPasswordBinding? = null
    private val binding: FragmentForgotPasswordBinding
        get() = _binding ?: throw RuntimeException("FragmentForgotPasswordBinding == null")

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observer()
        binding.btnForgotPass.setOnClickListener {
            if (validation()) {
                viewModel.forgotPassword(binding.etEmail.text.toString())
                findNavController().navigate(R.id.action_forgotPasswordFragment_to_loginFragment)
                toast("Check your email message")
            }
        }
    }

    private fun observer() {
        viewModel.forgotPassword.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.btnForgotPass.setText("")
                    binding.pbForgotPass.show()
                }
                is UiState.Failure -> {
                    binding.btnForgotPass.setText("Send")
                    binding.pbForgotPass.hide()
                }
                is UiState.Success -> {
                    binding.btnForgotPass.setText("Send")
                    binding.pbForgotPass.hide()
                    toast(state.data)
                }
            }
        }
    }

    fun validation(): Boolean {
        var isValid = true

        if (binding.etEmail.text.isNullOrEmpty()) {
            isValid = false
            toast(getString(R.string.enter_email))
        } else {
            if (!binding.etEmail.text.toString().isValidEmail()) {
                isValid = false
                toast(getString(R.string.invalid_email))
            }
        }
        return isValid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val LOG_TAG = "ForgotPasswordFragment"
    }
}