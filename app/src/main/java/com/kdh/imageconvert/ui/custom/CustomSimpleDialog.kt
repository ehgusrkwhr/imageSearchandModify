package com.kdh.imageconvert.ui.custom

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.kdh.imageconvert.R
import com.kdh.imageconvert.data.model.FileInfo
import com.kdh.imageconvert.databinding.DialogSimpleQuestionBinding

class CustomSimpleDialog(private val title: String = "", private val body: String = "") : DialogFragment() {

    private var _binding: DialogSimpleQuestionBinding? = null
    private val binding get() = _binding!!
    private var buttonClickListener: SimpleDialogClickedListener? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DialogSimpleQuestionBinding.inflate(inflater, container, false)
        binding.tvTitleText.text = title
        binding.tvBodyText.text = body
        buttonClickListener?.let { listener ->
            binding.btnPositive.setOnClickListener {
                listener.onPositiveClicked()
            }
            binding.btnNegative.setOnClickListener {
                listener.onNegativeClicked()
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState)
    }

    fun setButtonClickListener(clickedListener: SimpleDialogClickedListener) {
        this.buttonClickListener = clickedListener
    }

    interface SimpleDialogClickedListener {
        fun onNegativeClicked()
        fun onPositiveClicked()
    }

}