package com.handroid.dovemessengerkt.ui.auth.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.handroid.dovemessengerkt.R
import com.handroid.dovemessengerkt.databinding.FragmentRegisterBinding
import com.handroid.dovemessengerkt.domain.User
import com.handroid.dovemessengerkt.ui.auth.viewmodel.AuthViewModel
import com.handroid.dovemessengerkt.util.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding: FragmentRegisterBinding
        get() = _binding ?: throw RuntimeException("FragmentRegisterBinding == null")

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnRegister.setOnClickListener {
            observer()
            if (valildation()) {
                viewModel.register(
                    email = binding.etEmail.text.toString(),
                    password = binding.etPassword.text.toString(),
                    user = getUserObj()
                )
            }
        }
    }

    private fun observer() {
        viewModel.register.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.btnRegister.setText("")
                    binding.pbRegister.show()
                }
                is UiState.Failure -> {
                    binding.btnRegister.setText("Register")
                    binding.pbRegister.hide()
                    toast(state.error)
                }
                is UiState.Success -> {
                    binding.btnRegister.setText("Register")
                    binding.pbRegister.hide()
                    toast(state.data)
                    findNavController().navigate(
                        R.id.action_registerFragment_to_noteListingFragment
                    )
                }
            }
        }
    }

    fun valildation(): Boolean {
        var isValid = true
        if (binding.etFirstName.text.isNullOrEmpty()) {
            isValid = false
            toast(getString(R.string.enter_first_name))
        }
        if (binding.etLastName.text.isNullOrEmpty()) {
            isValid = false
            toast(getString(R.string.enter_last_name))
        }
        if (binding.etJob.text.isNullOrEmpty()) {
            isValid = false
            toast(getString(R.string.enter_job_title))
        }
        if (binding.etEmail.text.isNullOrEmpty()) {
            isValid = false
            toast(getString(R.string.enter_email))
        } else {
            if (!binding.etEmail.text.toString().isValidEmail()) {
                isValid = false
                toast(getString(R.string.invalid_email))
            }
        }
        if (binding.etPassword.text.isNullOrEmpty()) {
            isValid = false
            toast(getString(R.string.enter_password))
        } else {
            if (binding.etPassword.text.toString().length < 8) {
                isValid = false
                toast(getString(R.string.invalid_password))
            }
        }
        return isValid
    }

    fun getUserObj(): User {
        with(binding) {
            return User(
                id = "",
                first_name = etFirstName.text.toString(),
                last_name = etLastName.text.toString(),
                job_title = etJob.text.toString(),
                email = etEmail.text.toString()
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val LOG_TAG = "RegisterFragment"
    }
}


