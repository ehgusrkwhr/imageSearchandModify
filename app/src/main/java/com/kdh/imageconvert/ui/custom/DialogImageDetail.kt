package com.kdh.imageconvert.ui.custom

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.kdh.imageconvert.R
import com.kdh.imageconvert.databinding.DialogImageDetailBinding
import com.kdh.imageconvert.repeatLastCollectOnStarted
import com.kdh.imageconvert.ui.adapter.ImageDetailViewPagerAdapter
import com.kdh.imageconvert.ui.state.UiState
import com.kdh.imageconvert.ui.viewmodel.SearchViewModel
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber

class DialogImageDetail(pos: Int) : DialogFragment() {

    private var _binding: DialogImageDetailBinding? = null
    private val binding get() = _binding
    private val searchViewModel: SearchViewModel by activityViewModels()
    private var imagePosition: Int = pos
    private var imageDetailViewPagerAdapter: ImageDetailViewPagerAdapter? = null


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        return dialog
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //다이어로그 화면 가득 채우는
        setStyle(STYLE_NO_FRAME, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DialogImageDetailBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initImageDetailAdapter()
        initObserverData()
        initEventListener()
    }

    private fun initImageDetailAdapter() {
        imageDetailViewPagerAdapter = ImageDetailViewPagerAdapter()
    }

    private fun initEventListener() {
        binding?.vpImageDetail?.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding?.apply {
                    tvImageTitle.text = searchViewModel.sumSearchData[position].display_sitename
                    tvImageSize.text = "${searchViewModel.sumSearchData[position].width} x ${searchViewModel.sumSearchData[position].height}"
                }
            }
        })

    }

    override fun onDestroyView() {
        imageDetailViewPagerAdapter = null
        //  binding?.vpImageDetail?.unregisterOnPageChangeCallback()
        _binding = null
        super.onDestroyView()
    }


    private fun initObserverData() {
        binding?.vpImageDetail?.adapter = imageDetailViewPagerAdapter
        repeatLastCollectOnStarted {
            searchViewModel.searchData.collectLatest { state ->
                when (state) {
                    is UiState.Success -> {
                        state.data.documents?.let { searchViewModel.sumSearchData.addAll(it) }
                        imageDetailViewPagerAdapter?.submitList(searchViewModel.sumSearchData.toMutableList())
                        if (imagePosition != -1) {
                            binding?.vpImageDetail?.setCurrentItem(imagePosition, false)
                            imagePosition = -1
                        }
                    }
                    is UiState.Error -> {
                        Timber.d("initDataObserver error ${state.error}")
                    }
                    is UiState.Loading -> {
//                        binding.pbLoading.visibility = View.VISIBLE
                    }
                    else -> {

                    }
                }
            }
        }
    }


}