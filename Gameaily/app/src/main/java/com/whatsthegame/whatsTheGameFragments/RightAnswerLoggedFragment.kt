package com.whatsthegame.whatsTheGameFragments

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.analytics.FirebaseAnalytics
import com.whatsthegame.Api.ViewModel.AllGamesViewModel
import com.whatsthegame.Api.ViewModel.DiaryGameViewModel
import com.whatsthegame.Api.ViewModel.GuessDiaryGameViewModel
import com.whatsthegame.Api.ViewModel.SendPointsViewModel
import com.whatsthegame.R
import kotlinx.coroutines.launch

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


        return view
    }

    private lateinit var diaryGameViewModel: DiaryGameViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        logUserEnteredScreenEvent()
        val sharedPreferences = requireContext().getSharedPreferences("Preferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("playerHasAnswer", true)
        editor.apply()

        diaryGameViewModel = ViewModelProvider(this).get(DiaryGameViewModel::class.java)

        diaryGameViewModel.game.observe(viewLifecycleOwner, Observer { game ->
            if (game != null) {
                val timer = game.second
                println("Timer : $timer")
                // Parse a string do timer para obter horas, minutos e segundos
                val timeParts = timer.split(":")
                val hours = timeParts[0].toLong()
                val minutes = timeParts[1].toLong()
                val seconds = timeParts[2].toLong()

                // Calcule o tempo total em milissegundos
                val totalTimeInMillis = (hours * 3600 + minutes * 60 + seconds) * 1000

                // Configure o CountDownTimer com o tempo total
                countDownTimer = object : CountDownTimer(totalTimeInMillis, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        val seconds = millisUntilFinished / 1000
                        val hours = seconds / 3600
                        val minutes = (seconds % 3600) / 60
                        val remainingSeconds = seconds % 60

                        timerTextView = view.findViewById(R.id.timerTextView)

                        timerTextView.text = String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds)

                    }

                    override fun onFinish() {
                        // O timer chegou ao fim
                        timerTextView.text = "00:00:00"
                        val sharedPreferences = requireContext().getSharedPreferences("Preferences", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putBoolean("playerHasAnswer", false)
                        editor.apply()
                    }
                }

                // Inicie o CountDownTimer
                countDownTimer.start()
            }
        })
        viewLifecycleOwner.lifecycleScope.launch {
            diaryGameViewModel.fetchDiaryGame()
        }
    }

    private fun logUserEnteredScreenEvent() {
        val firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())

        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "RightAnswerLoggedFragment")
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }


    override fun onResume() {
        super.onResume()
        if (::countDownTimer.isInitialized) {
            countDownTimer.start()
        }
    }

    override fun onPause() {
        super.onPause()
        if (::countDownTimer.isInitialized) {
            countDownTimer.cancel()
        }
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