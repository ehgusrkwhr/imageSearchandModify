package com.kdh.imageconvert

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.View.OnClickListener
import android.view.animation.AnimationUtils
import android.widget.EditText
import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

fun EditText.textChangesToFlow(): Flow<CharSequence?> {
    return callbackFlow {
        val listener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

            override fun afterTextChanged(s: Editable?) {
                trySend(text)
            }
        }
        addTextChangedListener(listener)
        awaitClose { removeTextChangedListener(listener) }
    }.onStart {
        emit(text)
    }
}

fun View.clicks(): Flow<Unit> {
    return callbackFlow {
        val listener = OnClickListener {
            trySend(Unit)
        }
        setOnClickListener(listener)
        awaitClose { setOnClickListener(null) }
    }
}

fun LifecycleOwner.repeatLastCollectOnStarted(block: suspend CoroutineScope.() -> Unit) {
    lifecycleScope.launch {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED, block)
    }
}

private const val CLICK_DELAY = 1000L
private var lastClickTime: Long = 0

fun View.safeOnClickListener(block: (View) -> Unit) {
    setOnClickListener {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime > CLICK_DELAY) {
            block(it)
            lastClickTime = currentTime
        }
    }

}




