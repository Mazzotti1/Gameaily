package com.whatsthegame.accountFragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
import com.whatsthegame.Api.ViewModel.GuessDiaryGameViewModel
import com.whatsthegame.Api.ViewModel.SendPointsViewModel
import com.whatsthegame.R
import com.whatsthegame.models.GuessDiaryGame

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PosLoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PosLoginFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var sendPointsViewModel: SendPointsViewModel
    private lateinit var guessDiaryGameViewModel: GuessDiaryGameViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sendPointsViewModel = ViewModelProvider(this).get(SendPointsViewModel::class.java)
        guessDiaryGameViewModel = ViewModelProvider(this).get(GuessDiaryGameViewModel::class.java)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_pos_login, container, false)

        val recievePointsButton = view.findViewById<Button>(R.id.recievePoints)
        recievePointsButton.setOnClickListener {

            val sharedPreferences =
                requireContext().getSharedPreferences("Preferences", Context.MODE_PRIVATE)
            val authToken = sharedPreferences.getString("tokenJwt", "")
            val points = sharedPreferences.getInt("points", 0)
            val gameName = sharedPreferences.getString("choosedGame", "")
            val choosedGameJson = GuessDiaryGame(gameName)

            val decodedJWT: DecodedJWT = JWT.decode(authToken)
            val playerAnswerString = decodedJWT.getClaim("userAnswer")
            val playerAnswer = playerAnswerString.asBoolean()
                println("resposta: $playerAnswer")
            if (playerAnswer) {
                val inflater = layoutInflater
                val layout = inflater.inflate(R.layout.empty_submit_layout, null)
                val toastText = layout.findViewById<TextView>(R.id.empty_submit_text)
                toastText.text =
                    "Você já acertou o jogo hoje, novos pontos não serão computados!"
                val toast = Toast(requireContext())
                toast.duration = Toast.LENGTH_LONG
                toast.view = layout
                toast.show()

                findNavController().navigate(R.id.action_posLoginFragment_to_rankNavbar)

            } else {
                try {
                    val decodedJWT: DecodedJWT = JWT.decode(authToken)
                    val userId = decodedJWT.subject

                    sendPointsViewModel.sendPoints(userId.toLong(), points)
                    guessDiaryGameViewModel.guessDiaryGame(choosedGameJson, userId.toLong())

                } catch (e: Exception) {
                    e.printStackTrace()
                }
                val editor = sharedPreferences.edit()
                editor.remove("points")
                editor.remove("choosedGame")
                editor.apply()
                findNavController().navigate(R.id.action_posLoginFragment_to_rankNavbar)
            }
        }
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PosLoginFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PosLoginFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}