package com.whatsthegame.whatsTheGameFragments

import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.whatsthegame.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RightAnswerLoggedFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RightAnswerLoggedFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private lateinit var countDownTimer: CountDownTimer
    private lateinit var timerTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_right_answer_logged, container, false)




        countDownTimer = object : CountDownTimer(86400000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                val hours = seconds / 3600
                val minutes = (seconds % 3600) / 60
                val remainingSeconds = seconds % 60

                timerTextView = view.findViewById(R.id.timerTextView)

                timerTextView.text = String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds)
            }

            override fun onFinish() {
                // O timer de 24 horas chegou ao fim
                timerTextView.text = "00:00:00"
            }
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        // Iniciar o timer quando a tela estiver visível
        countDownTimer.start()
    }

    override fun onPause() {
        super.onPause()
        // Parar o timer quando a tela não estiver visível para economizar recursos
        countDownTimer.cancel()
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RightAnswerLoggedFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RightAnswerLoggedFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}