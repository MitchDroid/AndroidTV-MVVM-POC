package com.zemoga.apptvdemo.ui.feature.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.NavOptions
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.zemoga.apptvdemo.R
import com.zemoga.apptvdemo.databinding.FragmentSplashBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashFragment : Fragment() {

    private val viewModel: SplashViewModel by viewModels()
    private var _binding: FragmentSplashBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSplashBinding.inflate(inflater, container, false)

        viewModel.shouldGoToLogin.asLiveData().observe(viewLifecycleOwner) { shouldGoToLogin ->
            if (shouldGoToLogin) {
                goToLogin()
            }
        }

        return _binding!!.root
    }

    private fun goToLogin() {
        val action = SplashFragmentDirections.actionSplashToLogin()

        val extras = FragmentNavigatorExtras(
            _binding!!.ztvLogo to _binding!!.ztvLogo.transitionName,
        )
        findNavController().navigate(
            action,extras
        )
    }

    companion object {
        fun newInstance() = SplashFragment()
    }
}