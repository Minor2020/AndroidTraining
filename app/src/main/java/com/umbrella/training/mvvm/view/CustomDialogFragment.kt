package com.umbrella.training.mvvm.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.*
import android.util.Log
import android.view.Gravity.BOTTOM
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
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
//        val rootView = inflater.inflate(R.layout.fragment_dialog_container_layout, container, true)
//        val containerView = rootView.findViewById<FrameLayout>(R.id.fragment_dialog_container)

        var containerView: FrameLayout? = null
        context?.let {
            containerView = createFrameLayout(it)
            containerView?.removeAllViews()
            containerView?.addView(CustomEditView(it))
        }
        return containerView
    }

    private fun createFrameLayout(context: Context): FrameLayout {
        return FrameLayout(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        triggerButton = view.findViewById(R.id.training_fragment_dialog_trigger)
        msgView = view.findViewById(R.id.training_fragment_dialog_msg)

        editView = view.findViewById(R.id.training_edit)
        editView?.filters = arrayOf(InputFilter.LengthFilter(13))

        val phoneNumberWatcher = PhoneTextWatcher()
            editView?.addTextChangedListener(phoneNumberWatcher)
        phoneNumberWatcher.textChangeCallback = object : TextChangeCallback {
            override fun afterTextChanged(unformatted: String?, isPhoneNumberValid: Boolean) {
                Log.d(TAG, "unformatted num $unformatted is valid num $isPhoneNumberValid")
            }
        }
        processProtocolTips()
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

    /**
     *
     */
    private fun processProtocolTips() {
        val spannableString = SpannableString("这是联通协议")
        val start = 2
        // 字体颜色
        val foregroundColorSpan = ForegroundColorSpan(resources.getColor(R.color.colorPrimary, null))
        spannableString.setSpan(foregroundColorSpan, start, spannableString.length, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
        // 字体大小
        val absoluteSizeSpan = AbsoluteSizeSpan(60, false)
        spannableString.setSpan(absoluteSizeSpan, start, spannableString.length, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
        // 垂直居中
        val verticalCenterSpan = VerticalCenterSpan(60f)
        spannableString.setSpan(verticalCenterSpan, start, spannableString.length, Spanned.SPAN_INCLUSIVE_INCLUSIVE)

        // 字体样式（粗）
        val styleSpanBold = StyleSpan(Typeface.BOLD)
        spannableString.setSpan(styleSpanBold, start, spannableString.length, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                Toast.makeText(context, "联通", Toast.LENGTH_LONG).show()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }
        spannableString.setSpan(clickableSpan, start, spannableString.length, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
        msgView?.movementMethod = LinkMovementMethod.getInstance()
        msgView?.highlightColor = resources.getColor(android.R.color.transparent, null)
        msgView?.text = spannableString
    }
}

/**
 * 局限性，当标记字体小于普通文本时，才会居中
 */
class VerticalCenterSpan(val fontSizePx: Float = 0f) : ReplacementSpan() {

    override fun getSize(
        paint: Paint,
        text: CharSequence?,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt?
    ): Int {
        return getCustomTextPaint(paint).measureText(text?.subSequence(start, end).toString()).toInt()
    }

    override fun draw(
        canvas: Canvas,
        text: CharSequence?,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        val subText = text?.subSequence(start, end)
        val customPaint = getCustomTextPaint(paint)
        val fontMetricsInt = customPaint.getFontMetricsInt()
        subText?.let {
            canvas.drawText(
                subText.toString(),
                x,
                y - ((y + fontMetricsInt.descent + y + fontMetricsInt.ascent) / 2f - (bottom + top) / 2f),
                customPaint
            )
        }
    }

    private fun getCustomTextPaint(srcPaint: Paint): TextPaint {
        val paint = TextPaint(srcPaint)
        paint.textSize = fontSizePx
        return paint
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
}

const val DEBUG = true
