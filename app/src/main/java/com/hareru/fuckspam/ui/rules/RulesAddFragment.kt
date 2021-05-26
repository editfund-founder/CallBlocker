package com.hareru.fuckspam.ui.rules

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hareru.fuckspam.databinding.FragmentAddSpamBinding

class RulesAddFragment : BottomSheetDialogFragment() {
    // 因为 fragment 的生命周期与 activity 的生命周期不同，并且该fragment 可以超出其视图的生命周期，因此如果不将其设置为null，则可能会发生内存泄漏。
    private var _binding: FragmentAddSpamBinding? = null
    private val binding: FragmentAddSpamBinding
        get() = _binding!!

    private val viewModel by activityViewModels<SpamRulesViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        if (_binding == null) _binding = FragmentAddSpamBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.edit.addTextChangedListener {
            try {
                val pattern = it?.toString()
                if (pattern.isNullOrBlank())
                    throw NullPointerException()
                Regex(pattern)
                binding.input.error = null
                binding.btnOk.isClickable = true
            } catch (e: NullPointerException) {
                binding.input.error = "规则不能为空"
                binding.btnOk.isClickable = false
            } catch (e: Exception) {
                binding.input.error = "正则表达式错误"
                binding.btnOk.isClickable = false
            }
        }
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
        binding.btnOk.setOnClickListener {
            viewModel.addSpam(binding.edit.text.toString())
            dismiss()
        }
        binding.btnOk.isClickable = false
        showSoftInput(binding.edit)

    }

    private fun showSoftInput(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            view.windowInsetsController?.show(WindowInsets.Type.ime())
            view.requestFocus()
        } else {
            view.requestFocus()
            val inputManager = getSystemService(requireContext(), InputMethodManager::class.java)
            inputManager?.showSoftInput(view, InputMethodManager.SHOW_FORCED)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}