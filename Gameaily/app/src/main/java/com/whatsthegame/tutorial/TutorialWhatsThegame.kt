package com.whatsthegame.tutorial

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.whatsthegame.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class TutorialWhatsThegame : DialogFragment() {
    private var steps: List<TutorialStep>? = null
    private var currentStepIndex = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_tutorial_whats_thegame, container, false)
        val textView = view.findViewById<TextView>(R.id.textView)
        val imageView = view.findViewById<ImageView>(R.id.imageView)

        steps?.get(currentStepIndex)?.let { step ->
            textView.text = step.text
            imageView.setImageResource(step.imageResource)
        }

        val button = view.findViewById<Button>(R.id.button)
        button.setOnClickListener {
            if (currentStepIndex < steps!!.size - 1) {
                currentStepIndex++
                steps?.get(currentStepIndex)?.let { step ->
                    textView.text = step.text
                    imageView.setImageResource(step.imageResource)
                }
            } else {
                dismiss()
            }
        }


        return view
    }

    companion object {
        fun newInstance(steps: List<TutorialStep>): TutorialWhatsThegame {
            val fragment = TutorialWhatsThegame()
            fragment.steps = steps
            return fragment
        }
    }
}


