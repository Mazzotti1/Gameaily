package com.whatsthegame.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.whatsthegame.Api.ViewModel.AllGamesViewModel
import com.whatsthegame.Api.ViewModel.GameViewModel
import com.whatsthegame.R
import kotlinx.coroutines.launch


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [whatsTheGameFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class whatsTheGameFragment : Fragment() {
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
    private val token = false
    private val gameNameList = mutableListOf<String>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_whats_the_game, container, false)

        val searchView = rootView.findViewById<SearchView>(R.id.searchView)
        val gameNameTextView = rootView.findViewById<TextView>(R.id.gameNameTextView)

        // Configurar o ouvinte de texto para a SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Não é necessário fazer nada aqui, pois você quer atualizar em tempo real.
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Filtrar a lista com base no texto digitado
                val filteredList = gameNameList.filter { gameName ->
                    gameName.contains(newText.orEmpty(), ignoreCase = true)
                }

                // Atualizar o texto do TextView com os resultados filtrados
                gameNameTextView.text = filteredList.joinToString("\n")
                gameNameTextView.setBackgroundResource(R.drawable.search_view_bg);
                return true
            }
        })


        val sendButton = rootView.findViewById<Button>(R.id.sendButton)
        sendButton.setOnClickListener {
            if (token) {
                findNavController().navigate(R.id.action_whatsTheGame_to_rightAnswerLoggedFragment)
            } else {
                findNavController().navigate(R.id.action_whatsTheGame_to_rightAnswerFragment)
            }

        }
        val iconList = listOf(
            R.drawable.heartthin,
            R.drawable.heartthin,
            R.drawable.heartthin,
            R.drawable.heartthin,
            R.drawable.heartthin
        )
        val iconContainer = rootView.findViewById<LinearLayout>(R.id.hearts)
        val iconSize = resources.getDimensionPixelSize(R.dimen.icon_size)
        for (iconResId in iconList) {
            val imageView = ImageView(requireContext()) // Use requireContext() instead of 'this'
            imageView.setImageResource(iconResId)
            imageView.layoutParams = LinearLayout.LayoutParams(
                resources.getDimensionPixelSize(R.dimen.icon_size),
                resources.getDimensionPixelSize(R.dimen.icon_size)
            )
            imageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.destaques))

            iconContainer.addView(imageView)
        }
        return rootView
    }

    private lateinit var diaryGameViewModel: GameViewModel
    private lateinit var allGamesViewModel: AllGamesViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        diaryGameViewModel = ViewModelProvider(this).get(GameViewModel::class.java)

        val textViewDifficulty = view.findViewById<TextView>(R.id.difficulty)

        diaryGameViewModel.game.observe(viewLifecycleOwner, Observer { game ->
            if (game != null){
                val gameName = game.gameName
                val gameImage = game.gameImage
                val gameTip = game.tips
                val gameDifficulty = game.difficulty

                val colorResId = when (gameDifficulty) {
                    "Fácil" -> R.color.win
                    "Médio" -> R.color.secundaria
                    "Difícil" -> R.color.destaques
                    else -> android.R.color.white
                }
                activity?.runOnUiThread {
                    textViewDifficulty.text = "$gameDifficulty"
                    textViewDifficulty.setTextColor(ContextCompat.getColor(requireContext(), colorResId))
                }
            }
        })

        allGamesViewModel = ViewModelProvider(this).get(AllGamesViewModel::class.java)
        allGamesViewModel.games.observe(viewLifecycleOwner, Observer { games ->
            if (games != null) {
                for (game in games) {
                    val gameList = game?.gameName
                    if (gameList != null) {
                        gameNameList.add(gameList)
                    }
                }
            }
        })

        viewLifecycleOwner.lifecycleScope.launch {
            diaryGameViewModel.fetchDiaryGame()
            allGamesViewModel.fetchAllGames()
        }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment whatsTheGameFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            whatsTheGameFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}