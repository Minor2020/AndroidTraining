package com.umbrella.training.mvvm.view

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.Selection
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity.BOTTOM
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.umbrella.training.mvvm.R
import com.umbrella.training.mvvm.viewmodel.MainViewModel
import java.util.regex.Pattern

class CustomDialogFragment : DialogFragment() {

    companion object {
        const val TAG = "CustomDialogFragment"
    }
    var editView: EditText? = null
    var triggerButton: Button? = null
    var msgView: TextView? = null

    val viewMode: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_custom_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        triggerButton = view.findViewById(R.id.training_fragment_dialog_trigger)
        msgView = view.findViewById(R.id.training_fragment_dialog_msg)

        editView = view.findViewById(R.id.training_edit)
        editView?.filters = arrayOf(InputFilter.LengthFilter(13))
//    setListener()
        val phoneNumberWatcher = PhoneTextWatcher()
            editView?.addTextChangedListener(phoneNumberWatcher)
        phoneNumberWatcher.textChangeCallback = object : TextChangeCallback {
            override fun afterTextChanged(unformatted: String?, isPhoneNumberValid: Boolean) {
                Log.d(TAG, "unformatted num $unformatted is valid num $isPhoneNumberValid")
            }
        }

        viewMode.info.observe(viewLifecycleOwner) {
            msgView?.text = it
        }
        triggerButton?.setOnClickListener {
            viewMode.mergeInfo()
        }

    }

    override fun onStart() {
        super.onStart()
        val window = dialog?.window
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window?.attributes?.apply {
            gravity = BOTTOM
        }
    }

    private fun setListener() {
        editView?.addTextChangedListener(object : TextWatcher {
            val stringBuilder = StringBuilder()

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Log.d(TAG, "beforeTextChanged s$s start$start count$count after$after")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d(TAG, "onTextChanged s$s start$start before$before count$count")
            }

            override fun afterTextChanged(s: Editable?) {
                Log.d(TAG, "afterTextChanged s$s")
            }
        })
    }
}

class AsYouTypeFormatter {
    companion object {
        const val SEPARATOR_SPACE = ' '
    }
    private val accruedInput = StringBuilder()

    var separator = SEPARATOR_SPACE

    fun inputDigit(nextChar: Char): String {
        accruedInput.append(nextChar)
        val accruedInputNumber = accruedInput.toString()
        val formattedNumber = attemptToFormatAccruedDigits(accruedInputNumber)
        if (formattedNumber.isNotEmpty()) {
            return formattedNumber
        }
        return accruedInputNumber
    }

    private fun attemptToFormatAccruedDigits(accruedInputNumber: String): String {
        var formattedNumber = ""
        var formattingNumber = StringBuilder()
        if (accruedInputNumber.isNotEmpty()) {
            for (i in accruedInputNumber.indices) {
                if (i == 3 || i == 8 || accruedInputNumber[i] != separator) {
                    formattingNumber.append(accruedInputNumber[i])
                    // 分隔符插入点
                    val atSeparatorIndex = formattingNumber.length == 4 || formattingNumber.length == 9
                    val lastCharIsNotSeparator = formattingNumber[formattingNumber.length - 1] != separator
                    if (atSeparatorIndex && lastCharIsNotSeparator) {
                        formattingNumber.insert(formattingNumber.length - 1, separator)
                    }
                }
            }
        }
        formattedNumber = formattingNumber.toString()
        return formattedNumber
    }

    fun clear() {
        accruedInput.clear()
    }
}

fun isNonSeparator(c: Char): Boolean {
    return (c in '0'..'9') || c == '*' || c == '#' || c == '+'
}

interface TextChangeCallback {
    fun afterTextChanged(unformatted: String?, isPhoneNumberValid: Boolean)
}

class PhoneTextWatcher : TextWatcher {
    val formatter = AsYouTypeFormatter()
    private var selfChange = false
    private var stopFormatting = false
    var textChangeCallback: TextChangeCallback? = null

    init {
        formatter.separator = AsYouTypeFormatter.SEPARATOR_SPACE
        formatter.clear()
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        if (selfChange || stopFormatting) {
            return
        }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (selfChange || stopFormatting) {
            return
        }
    }

    @Synchronized
    override fun afterTextChanged(s: Editable) {
        if (stopFormatting) {
            stopFormatting = s.isNotEmpty()
            return
        }
        if (selfChange) {
            // 格式化引起的更改
            return
        }
        val selectionEnd = Selection.getSelectionEnd(s)
        val isCursorAtEnd = (selectionEnd == s.length)
        val formatted = format(s)
        var finalCursorPosition = 0
        if (formatted == s.toString()) {
            finalCursorPosition = selectionEnd
        } else if (isCursorAtEnd) {
            finalCursorPosition = formatted.length
        } else {
            var digitsBeforeCursor = 0
            for (i in s.indices) {
                if (i >= selectionEnd) {
                    break
                }
                if (isNonSeparator(s[i])) {
                    digitsBeforeCursor++
                }
            }
            var digitPassed = 0
            for (i in formatted.indices) {
                if (digitPassed == digitsBeforeCursor) {
                    finalCursorPosition = i
                    break
                }
                if (isNonSeparator(formatted[i])) {
                    digitPassed++
                }
            }
        }
        if (!isCursorAtEnd) {
            while (0 < finalCursorPosition - 1 && !isNonSeparator(formatted[finalCursorPosition - 1])) {
                finalCursorPosition--
            }
        }
        try {
            formatted.let {
                selfChange = true
                s.replace(0, s.length, formatted, 0, formatted.length)
                selfChange = false
                Selection.setSelection(s, finalCursorPosition);
            }
        } catch (e: Exception) {
            if (DEBUG) {
                throw e
            }
        }
        textChangeCallback?.let {
            val unformatted = unformatted(s)
            it.afterTextChanged(unformatted, checkMobile(unformatted))
        }
    }

    private fun unformatted(s: CharSequence): String {
        val separator = formatter.separator.toString()
        return s.toString().replace(separator, "")
    }

    private fun checkMobile(mobile: String): Boolean {
        val regex = "\\d{11}"
        return Pattern.matches(regex, mobile)
    }

    private fun format(s: CharSequence): String {
        var internationalFormatted = ""
        formatter.clear()
        var lastNonSeparator: Char? = null
        for (i in s.indices) {
            val c = s[i]
            if (isNonSeparator(c)) {
                if (lastNonSeparator != null) {
                    internationalFormatted = formatter.inputDigit(lastNonSeparator)
                }
                lastNonSeparator = c
            }
        }
        if (lastNonSeparator != null) {
            internationalFormatted = formatter.inputDigit(lastNonSeparator)
        }
        internationalFormatted = internationalFormatted.trim()
        return internationalFormatted
    }

    private fun stopFormatting() {
        stopFormatting = true
        formatter.clear()
    }

    private fun hasSeparator(s: CharSequence, start: Int, count: Int): Boolean {
        for (i in 0..start + count) {
            val c = s[i];
            if (!isNonSeparator(c)) {
                return true
            }
        }
        return false
    }
}

const val DEBUG = true
