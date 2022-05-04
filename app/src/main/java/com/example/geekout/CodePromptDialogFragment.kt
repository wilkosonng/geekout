package com.example.geekout

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import androidx.fragment.app.DialogFragment

// Dialog fragment to handle text input for lobby code input

class CodePromptDialogFragment: DialogFragment() {
    companion object {
        fun newInstance(): CodePromptDialogFragment {
            return CodePromptDialogFragment()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Creates a builder with an editable text field.

        var builder = AlertDialog.Builder(activity)
        var input = EditText(context)
        input.inputType = InputType.TYPE_CLASS_TEXT

        // Sets builder parameters

        builder.setTitle("Join a Lobby")
            .setMessage("Enter Lobby Code")
            .setCancelable(true)
            .setPositiveButton("Join") {_, _ ->
                (activity as MainActivity).joinLobby(input.text.toString())
            }
            .setNegativeButton("Cancel") {_, _ ->
                dialog?.cancel()
            }
            .setView(input)

        // Sends builder to Activity

        return builder.create()
    }
}