package com.whatsthegame.appBarFragments

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.RotateAnimation
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
import com.whatsthegame.Api.ViewModel.*
import com.whatsthegame.R
import com.whatsthegame.models.GuessEnigma
import kotlinx.coroutines.launch
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private lateinit var setUserVipViewModel: SetUserVipViewModel
/**
 * A simple [Fragment] subclass.
 * Use the [rouletteFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class rouletteFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUserVipViewModel = ViewModelProvider(this).get(SetUserVipViewModel::class.java)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }


    private val sectors = arrayOf(
        "Roxa",
        "Verde",
        "Azul",
        "Roxa",
        "Verde",
        "Azul",
        "Dourada",
        "Verde",
        "Azul",
        "Dourada",
        "Verde",
        "Azul"
    )
    private val sectorDegrees = IntArray(sectors.size)
    private val random = Random()
    private var degree = 0
    private var isSpinning = false;
    private lateinit var rouletteRollBody: ImageView

    private lateinit var userRollsViewModel: UserRollsViewModel

    var defaultRolls = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_roulette, container, false)

        val sharedPreferences = requireContext().getSharedPreferences("Preferences", Context.MODE_PRIVATE)
        val authToken = sharedPreferences.getString("tokenJwt", "")

        val rollsCounter = view.findViewById<TextView>(R.id.textViewRolls)
        rollsCounter.text = "$defaultRolls Giros restantes"


        val rouletteRollButton = view?.findViewById<Button>(R.id.rouletteRollButton)
        rouletteRollBody = view?.findViewById<ImageView>(R.id.rouletteBody)!!
        getDegreeForSectors()

        if (!authToken.isNullOrEmpty()){
            val decodedJWT: DecodedJWT = JWT.decode(authToken)
            val userId = decodedJWT.subject

            userRollsViewModel = ViewModelProvider(this).get(UserRollsViewModel::class.java)

            userRollsViewModel.rolls.observe(viewLifecycleOwner) { rolls ->
                rollsCounter.text = "$rolls Giros restantes"
                defaultRolls = rolls ?: 0
            }

            userRollsViewModel.getRolls(userId.toLong())
        }

        rouletteRollButton!!.setOnClickListener { v ->
            if (defaultRolls > 0 && !isSpinning) {
                defaultRolls--
                rollsCounter.text = "$defaultRolls Giros restantes"
                spin()
                isSpinning = true
            } else {
                val inflater = layoutInflater
                val layout = inflater.inflate(R.layout.submit_layout, null)
                val toastText = layout.findViewById<TextView>(R.id.empty_submit_text)
                toastText.text = "Você não tem nenhum giro sobrando!"
                val toast = Toast(requireContext())
                toast.duration = Toast.LENGTH_SHORT
                toast.view = layout
                toast.show()
            }
        }

        return view
    }


    private fun spin() {
        degree = random.nextInt(sectors.size - 1)

        val rotateAnimation = RotateAnimation(
            0f,
            (360f * sectors.size) + sectorDegrees[degree],
            RotateAnimation.RELATIVE_TO_SELF,
            0.5f,
            RotateAnimation.RELATIVE_TO_SELF,
            0.5f
        )

        rotateAnimation.duration = 3600
        rotateAnimation.fillAfter = true
        rotateAnimation.interpolator = DecelerateInterpolator()
        rotateAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {

            }

            override fun onAnimationEnd(animation: Animation) {
                val reward: String
                when (sectors[sectors.size - (degree + 1)]) {
                    "Verde" -> {
                        reward = "Você ganhou desconto único para remover os anúncios!!"
                        val dialog = AlertDialog.Builder(
                            ContextThemeWrapper(
                                requireContext(),
                                R.style.AlertDialogStyle
                            )
                        )
                        dialog.setTitle("Parabéns!")
                        dialog.setMessage("Você ganhou uma recompensa ${sectors[sectors.size - (degree + 1)]}: $reward")

                        dialog.setPositiveButton("Resgatar") { _, _ ->
                            //lógica para dar desconto no anti anuncio
                        }

                        dialog.show()
                    }
                    "Azul" -> {
                        reward = "Você ganhou uma vida extra para o seu próximo jogo diario!"
                        val dialog = AlertDialog.Builder(
                            ContextThemeWrapper(
                                requireContext(),
                                R.style.AlertDialogStyle
                            )
                        )
                        dialog.setTitle("Parabéns!")
                        dialog.setMessage("Você ganhou uma recompensa ${sectors[sectors.size - (degree + 1)]}: $reward")

                        dialog.setPositiveButton("Resgatar") { _, _ ->
                            val remainingLives = 6
                            val sharedPreferences = requireContext().getSharedPreferences("Preferences", Context.MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putInt("remainingLives", remainingLives)
                            editor.apply()
                        }

                        dialog.show()
                    }
                    "Roxa" -> {
                        reward = "um dia inteiro sem anúncios de vídeo!"
                        val dialog = AlertDialog.Builder(
                            ContextThemeWrapper(
                                requireContext(),
                                R.style.AlertDialogStyle
                            )
                        )
                        dialog.setTitle("Parabéns!")
                        dialog.setMessage("Você ganhou uma recompensa ${sectors[sectors.size - (degree + 1)]}: $reward")

                        dialog.setPositiveButton("Resgatar") { _, _ ->
                            val adController = true
                            val sharedPreferences = requireContext().getSharedPreferences("Preferences", Context.MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putBoolean("adControl", adController)
                            editor.putLong("adControlTimestamp", System.currentTimeMillis())
                            editor.apply()
                        }

                        dialog.show()
                    }
                    "Dourada" -> {
                        reward = "Você ganhou acesso vip, e não receberá mais anúncios para SEMPRE"
                        val dialog = AlertDialog.Builder(
                            ContextThemeWrapper(
                                requireContext(),
                                R.style.AlertDialogStyle
                            )
                        )
                        dialog.setTitle("Parabéns!")
                        dialog.setMessage("Você ganhou uma recompensa ${sectors[sectors.size - (degree + 1)]}: $reward")

                        dialog.setPositiveButton("Resgatar") { _, _ ->
                            val sharedPreferences = requireContext().getSharedPreferences("Preferences", Context.MODE_PRIVATE)
                            val authToken = sharedPreferences.getString("tokenJwt", "")
                            val decodedJWT: DecodedJWT = JWT.decode(authToken)
                            val userId = decodedJWT.subject
                            setUserVipViewModel.setVipStatus(userId.toLong())
                        }

                        dialog.show()
                    }
                    else -> {
                        reward = "Você ganhou um prêmio!"
                    }
                }



                isSpinning = false
            }



            override fun onAnimationRepeat(animation: Animation) {

            }
        })

        rouletteRollBody.startAnimation(rotateAnimation)
    }


    private fun getDegreeForSectors(){
        val sectorDegree = 360/sectors.size

        for (i in 0 until sectors.size) {
            sectorDegrees[i] = (i + 1) * sectorDegree
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment rouletteFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            rouletteFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}