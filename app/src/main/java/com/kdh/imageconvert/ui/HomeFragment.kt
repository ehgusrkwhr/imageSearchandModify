package com.kdh.imageconvert.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.kdh.imageconvert.databinding.FragmentHomeBinding
import com.kdh.imageconvert.databinding.FragmentSearchBinding
import com.kdh.imageconvert.ui.viewmodel.SearchViewModel

class HomeFragment : Fragment() {

//    private val binding : Sear
    private var _binding : FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel :  SearchViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}