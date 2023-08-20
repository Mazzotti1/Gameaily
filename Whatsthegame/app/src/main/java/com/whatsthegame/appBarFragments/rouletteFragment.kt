package com.whatsthegame.appBarFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.RotateAnimation
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.whatsthegame.R
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

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
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }


    private val sectors = arrayOf("Roxa", "Verde", "Azul", "Roxa", "Verde", "Azul", "Dourada", "Verde", "Azul", "Vermelha", "Verde", "Azul")
    private val sectorDegrees = IntArray(sectors.size)
    private val random = Random()
    private var degree = 0
    private var isSpinning = false;
    private lateinit var rouletteRollBody: ImageView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_roulette, container, false)

        val rouletteRollButton = view?.findViewById<Button>(R.id.rouletteRollButton)
        rouletteRollBody = view?.findViewById<ImageView>(R.id.rouletteBody)!!
        getDegreeForSectors()


        if (rouletteRollButton != null) {
            rouletteRollButton.setOnClickListener { v ->
                if (!isSpinning) {
                    spin()
                    isSpinning = true
                }
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
                Toast.makeText(
                    requireContext(),
                    "Você ganhou uma recompensa ${sectors[sectors.size - (degree + 1)]}",
                    Toast.LENGTH_SHORT
                ).show()
                isSpinning = false;
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