package com.kdh.imageconvert

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onStart

fun EditText.textChangesToFlow() : Flow<CharSequence?>{
    return callbackFlow {
        val listener = object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

            override fun afterTextChanged(s: Editable?) {
                trySend(text)
            }
        }
        addTextChangedListener(listener)
        awaitClose{removeTextChangedListener(listener)}
    }.onStart {
        emit(text)
    }
}