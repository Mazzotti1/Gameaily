package com.whatsthegame.tutorial

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.whatsthegame.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class TutorialWhatsThegame : DialogFragment() {
    companion object {
        fun newInstance(title: String, imageResId: Int): TutorialWhatsThegame {
            val fragment = TutorialWhatsThegame()
            val args = Bundle()
            args.putString("title", title)
            args.putInt("imageResId", imageResId)
            fragment.arguments = args
            return fragment
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_tutorial_whats_thegame, container, false)

        val title = arguments?.getString("title")
        val imageResId = arguments?.getInt("imageResId")

        val titleTextView = view.findViewById<TextView>(R.id.textView)
        val imageView = view.findViewById<ImageView>(R.id.imageView)

        titleTextView.text = title
        imageView.setImageResource(imageResId ?: R.drawable.roulettebody)

        return view
    }
}