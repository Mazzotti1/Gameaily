package com.whatsthegame.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
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
import android.content.Context
import android.widget.Toast.LENGTH_LONG
import com.whatsthegame.Api.ViewModel.GuessDiaryGameViewModel


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
    private var gameTip: String? = null
    private var gameName: String? = null
    @SuppressLint("ClickableViewAccessibility", "MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_whats_the_game, container, false)

        val searchView = rootView.findViewById<SearchView>(R.id.searchView)
        val gameNameListView = rootView.findViewById<ListView>(R.id.gameNameListView)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                val filteredList = gameNameList.filter { gameName ->
                    gameName.contains(newText.orEmpty(), ignoreCase = true)
                }

                //gameNameTextView.text = filteredList.joinToString("\n")
                val gameNameListView = rootView.findViewById<ListView>(R.id.gameNameListView)
                val adapter = ArrayAdapter<String>(requireContext(),  R.layout.list_item_custom, filteredList)
                gameNameListView.adapter = adapter

                gameNameListView.setOnItemClickListener { parent, view, position, id ->
                    val clickedGameName = filteredList[position]

                    val sharedPreferences = requireContext().getSharedPreferences("Preferences", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString("choosedGame", clickedGameName)
                    editor.apply()

                    searchView.setQuery(clickedGameName, false)
                    searchView.clearFocus()
                }
                gameNameListView.setBackgroundResource(R.drawable.search_view_bg);
                return true
            }
        })


        rootView.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    searchView.clearFocus()
                }
                false
            }

            searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    gameNameListView.visibility = View.GONE
                } else {
                    gameNameListView.visibility = View.VISIBLE
                }
            }

        val tipButton = rootView.findViewById<Button>(R.id.tipButton)
        tipButton.setOnClickListener {
            val inflater = layoutInflater
            val layout = inflater.inflate(R.layout.custom_toast_layout, null)
            val toastText = layout.findViewById<TextView>(R.id.toast_text)
            toastText.text = gameTip

            val toast = Toast(requireContext())
            toast.duration = Toast.LENGTH_LONG
            toast.view = layout
            toast.show()
        }


        val alternativeIcons = listOf(
                R.drawable.heartbreakthin,
                R.drawable.heartbreakthin,
                R.drawable.heartbreakthin,
                R.drawable.heartbreakthin,
                R.drawable.heartbreakthin
        )
        val iconContainer = rootView.findViewById<LinearLayout>(R.id.hearts)
        var currentIconIndex = alternativeIcons.size - 1

        val lifesCounter = rootView.findViewById<TextView>(R.id.textViewLifes)
        var remainingLives = 5
        
        val sendButton = rootView.findViewById<Button>(R.id.sendButton)
        sendButton.setOnClickListener {

            val sharedPreferences = requireContext().getSharedPreferences("Preferences", Context.MODE_PRIVATE)
            val choosedGame = sharedPreferences.getString("choosedGame", "")

            val guessDiaryGameViewModel = ViewModelProvider(this).get(GuessDiaryGameViewModel::class.java)

            viewLifecycleOwner.lifecycleScope.launch {
                if (!choosedGame.isNullOrEmpty()) {
                    guessDiaryGameViewModel.guessDiaryGame(choosedGame)

                    if(choosedGame != gameName){

                        val imageViewToChange = iconContainer.getChildAt(currentIconIndex) as ImageView

                        imageViewToChange.setImageResource(alternativeIcons[currentIconIndex])
                        currentIconIndex = (currentIconIndex - 1 + alternativeIcons.size) % alternativeIcons.size

                        remainingLives--
                        lifesCounter.text = "$remainingLives vidas restantes"

                        val inflater = layoutInflater
                        val layout = inflater.inflate(R.layout.submit_layout, null)
                        val toastText = layout.findViewById<TextView>(R.id.empty_submit_text)
                        toastText.text = "Jogo errado!"
                        val toast = Toast(requireContext())
                        toast.duration = Toast.LENGTH_SHORT
                        toast.view = layout
                        toast.show()

                        val searchView = rootView.findViewById<SearchView>(R.id.searchView)
                        searchView.setQuery("", false)

                    }else if (!token){
                        findNavController().navigate(R.id.action_whatsTheGame_to_rightAnswerFragment)
                    }else{
                        findNavController().navigate(R.id.action_whatsTheGame_to_rightAnswerLoggedFragment)
                    }

                    val editor = sharedPreferences.edit()
                    editor.putString("choosedGame", null)
                    editor.apply()

                } else {
                    val inflater = layoutInflater
                    val layout = inflater.inflate(R.layout.empty_submit_layout, null)
                    val toastText = layout.findViewById<TextView>(R.id.empty_submit_text)
                    toastText.text = "Selecione um jogo antes de enviar!"
                    val toast = Toast(requireContext())
                    toast.duration = Toast.LENGTH_LONG
                    toast.view = layout
                    toast.show()
                }
            }
        }


        val alternativeIconsA = listOf(
                R.drawable.heartthin,
                R.drawable.heartthin,
                R.drawable.heartthin,
                R.drawable.heartthin,
                R.drawable.heartthin
        )

        val iconContainerA = rootView.findViewById<LinearLayout>(R.id.hearts)
        val iconSize = resources.getDimensionPixelSize(R.dimen.icon_size)

        for (iconResId in alternativeIconsA) {
            val imageView = ImageView(requireContext())
            imageView.setImageResource(iconResId)
            imageView.layoutParams = LinearLayout.LayoutParams(iconSize, iconSize)
            imageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.destaques))
            iconContainerA.addView(imageView)
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
                gameName = game.gameName
                val gameImage = game.gameImage
                gameTip = game.tips
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