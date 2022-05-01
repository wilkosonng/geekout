package com.example.geekout

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import androidx.fragment.app.DialogFragment

class CodePromptDialogFragment: DialogFragment() {
    companion object {
        fun newInstance(): CodePromptDialogFragment {
            return CodePromptDialogFragment()
        }
    }

    private lateinit var code: String

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        var builder = AlertDialog.Builder(activity)
        var input = EditText(context)
        input.inputType = InputType.TYPE_CLASS_TEXT

        builder.setTitle("Join a Lobby")
            .setMessage("Enter Lobby Code")
            .setCancelable(true)
            .setPositiveButton("Join") {_, _ ->
                code = input.text.toString()
            }
            .setNegativeButton("Cancel") {_, _ ->
                dialog.cancel()
            }
            .setView(input)

        return builder.create()
    }

    fun getCode(): String {
        return code
    }
}