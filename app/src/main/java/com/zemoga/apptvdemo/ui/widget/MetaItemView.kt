package com.zemoga.apptvdemo.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.annotation.StringRes
import com.zemoga.apptvdemo.databinding.LayoutMetaItemViewBinding

class MetaItemView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private var binding: LayoutMetaItemViewBinding

    init {
        val inflater = LayoutInflater.from(context)
        binding = LayoutMetaItemViewBinding.inflate(inflater, this, true)
    }

    fun setData(@StringRes label: Int, value: String) {
        with(binding) {
            tvLabel.setText(label)
            tvValue.text = value
        }
    }
}