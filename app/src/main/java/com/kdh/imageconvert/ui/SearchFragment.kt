package com.kdh.imageconvert.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.kdh.imageconvert.databinding.FragmentSearchBinding
import com.kdh.imageconvert.ui.viewmodel.SearchViewModel

class SearchFragment : Fragment() {

    //    private val binding : Sear

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val  searchViewModel: SearchViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)


        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}