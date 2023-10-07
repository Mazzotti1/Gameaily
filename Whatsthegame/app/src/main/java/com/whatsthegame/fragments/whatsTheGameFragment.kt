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
import com.whatsthegame.R
import kotlinx.coroutines.launch
import android.content.Context
import android.content.SharedPreferences
import android.graphics.*
import android.os.CountDownTimer
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import androidx.core.graphics.drawable.toBitmap
import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.whatsthegame.Api.ViewModel.*
import com.whatsthegame.models.GuessDiaryGame
import java.io.File



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

    private val gameNameList = mutableListOf<String>()
    private var gameTip: String? = null
    private var gameName: String? = null
    private var gameImage: String? = null
    private var gameTipUsed = false

    private var timer: CountDownTimer? = null
    private var timeElapsedInMs: Long = 0
    private var timePassedInMin = timeElapsedInMs / 60000
    @SuppressLint("ClickableViewAccessibility", "MissingInflatedId", "SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_whats_the_game, container, false)

        val sharedPreferences = requireContext().getSharedPreferences("Preferences", Context.MODE_PRIVATE)
        //val editor = sharedPreferences.edit()
        //editor.putBoolean("playerHasAnswer", false)
        //editor.apply()

        startTimer()


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

        searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                gameNameListView.visibility = View.VISIBLE
            } else {
                gameNameListView.visibility = View.GONE
            }
        }
        rootView.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    searchView.clearFocus()
                }
                false
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

            gameTipUsed = true
        }




        val lifesCounter = rootView.findViewById<TextView>(R.id.textViewLifes)
        var remainingLives = 5

        val lifesTimestamp = sharedPreferences.getLong("lifesTimestamp", 0)
        val currentTimeMillis = System.currentTimeMillis()


       /* if (lifesTimestamp > 0 && (currentTimeMillis - lifesTimestamp) < (24 * 60 * 60 * 1000)) {
            remainingLives = sharedPreferences.getInt("remainingLives", 5)

            lifesCounter.text = "$remainingLives vidas restantes"
        }*/

        val token = sharedPreferences.getString("tokenJwt", null)
        val sendButton = rootView.findViewById<Button>(R.id.sendButton)
        sendButton.setOnClickListener {
            val sharedPreferences = requireContext().getSharedPreferences("Preferences", Context.MODE_PRIVATE)
            val choosedGame = sharedPreferences.getString("choosedGame", "")
            val choosedGameJson = GuessDiaryGame(choosedGame)


            if (remainingLives > 0) {
                viewLifecycleOwner.lifecycleScope.launch {
                    if (!choosedGame.isNullOrEmpty()) {
                        if (choosedGame != gameName) {

                            remainingLives--
                            lifesCounter.text = "$remainingLives vidas restantes"

                            if (remainingLives <= 0) {

                                findNavController().navigate(R.id.action_whatsTheGame_to_gameOverFragment)
                            } else {

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
                            }
                        } else if (token != null) {

                            val sharedPreferences = requireContext().getSharedPreferences(
                                "Preferences",
                                Context.MODE_PRIVATE
                            )
                            //enviar direto pontos pro server

                            val authToken = sharedPreferences.getString("tokenJwt", "")
                            val decodedJWT: DecodedJWT = JWT.decode(authToken)
                            val userId = decodedJWT.subject
                            val playerAnswerString = decodedJWT.getClaim("userAnswer")
                            val playerAnswer = playerAnswerString.asBoolean()
                           if (playerAnswer) {

                                val inflater = layoutInflater
                                val layout = inflater.inflate(R.layout.empty_submit_layout, null)
                                val toastText =
                                    layout.findViewById<TextView>(R.id.empty_submit_text)
                                toastText.text =
                                    "Você já acertou o jogo hoje, novos pontos não serão computados!"
                                val toast = Toast(requireContext())
                                toast.duration = Toast.LENGTH_LONG
                                toast.view = layout
                                toast.show()

                                findNavController().navigate(R.id.action_whatsTheGame_to_rightAnswerLoggedFragment)
                            } else {

                                points = calculateTipsPoints(gameTipUsed)
                                points = calculateMinutesPoints(timePassedInMin)
                                points = calculateLivesPoints(remainingLives)

                            try {

                                if (authToken != null) {
                                    sendPointsViewModel.sendPoints(userId.toLong(), points)
                                    guessDiaryGameViewModel.guessDiaryGame(choosedGameJson, userId.toLong())

                                    println("Token: $authToken")

                                }
                                findNavController().navigate(R.id.action_whatsTheGame_to_rightAnswerLoggedFragment)

                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            }
                        } else {
                            findNavController().navigate(R.id.action_whatsTheGame_to_rightAnswerFragment)
                            points = calculateTipsPoints(gameTipUsed)
                            points = calculateMinutesPoints(timePassedInMin)
                            points = calculateLivesPoints(remainingLives)

                            val sharedPreferences: SharedPreferences = context!!.getSharedPreferences("Preferences", Context.MODE_PRIVATE)
                            val editor: SharedPreferences.Editor = sharedPreferences.edit()
                            editor.putInt("points", points)
                            editor.apply()

                        }

                        val editor = sharedPreferences.edit()
                        editor.putString("choosedGame", choosedGame)
                        editor.putInt("remainingLives", remainingLives)
                        editor.putLong("lifesTimestamp", currentTimeMillis)
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
            } else {

                val inflater = layoutInflater
                val layout = inflater.inflate(R.layout.empty_submit_layout, null)
                val toastText = layout.findViewById<TextView>(R.id.empty_submit_text)
                toastText.text = "Você não tem vidas restantes!"
                val toast = Toast(requireContext())
                toast.duration = Toast.LENGTH_LONG
                toast.view = layout
                toast.show()
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

    private var points = 70
    private fun calculateTipsPoints (userUsedHint: Boolean) : Int{
        if (userUsedHint) {
            points -= 10
        }
        return points
    }
    private fun calculateMinutesPoints (timeInMinutes: Long) : Int{
        if (timeInMinutes > 30) {
            points -= 20
        }
        return points
    }
    private fun calculateLivesPoints (remainingLives: Int) : Int{
        when (remainingLives) {
            1 -> points -= 30
            2 -> points -= 25
            3 -> points -= 15
            4 -> points -= 10
            5 -> points -= 0
        }
        return points
    }




    private lateinit var diaryGameViewModel: DiaryGameViewModel
    private lateinit var allGamesViewModel: AllGamesViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        diaryGameViewModel = ViewModelProvider(this).get(DiaryGameViewModel::class.java)

        val textViewDifficulty = view.findViewById<TextView>(R.id.difficulty)

        diaryGameViewModel.game.observe(viewLifecycleOwner, Observer { game ->
            if (game != null){
                gameName = game.first.gameName
                gameImage = game.first.gameImage
                gameTip = game.first.tips
                val gameDifficulty = game.first.difficulty
                val colorResId = when (gameDifficulty) {
                    "Fácil" -> R.color.win
                    "Médio" -> R.color.secundaria
                    "Difícil" -> R.color.destaques
                    else -> android.R.color.white
                }
                getImageFromBucket()
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
    private fun getImageFromBucket() {
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference
        val imageRef: StorageReference = storageRef.child("/capa jogos/$gameImage.png")

        val imageViewGame = view?.findViewById<ImageView>(R.id.imageViewGame)

        val screenWidth = resources.displayMetrics.widthPixels // Largura da tela
        val screenHeight = resources.displayMetrics.heightPixels // Altura da tela

        val localFile = File.createTempFile("temp_image", "jpg")

        imageRef.getFile(localFile).addOnSuccessListener { taskSnapshot ->
            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)

            val targetWidth = screenWidth
            val scaleFactor = targetWidth.toFloat() / bitmap.width
            val targetHeight = (bitmap.height * scaleFactor).toInt()

            val resizedBitmap = Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, false)

            imageViewGame!!.setImageBitmap(resizedBitmap)
            applyBlur(requireContext(), imageViewGame, 25f)
            applyBlur(requireContext(), imageViewGame, 25f)
            applyBlur(requireContext(), imageViewGame, 25f)
            applyBlur(requireContext(), imageViewGame, 25f)
            applyBlur(requireContext(), imageViewGame, 25f)
            applyBlur(requireContext(), imageViewGame, 25f)
            applyBlur(requireContext(), imageViewGame, 25f)
            applyBlur(requireContext(), imageViewGame, 25f)
            applyBlur(requireContext(), imageViewGame, 25f)
            applyBlur(requireContext(), imageViewGame, 25f)
            applyBlur(requireContext(), imageViewGame, 25f)
            applyBlur(requireContext(), imageViewGame, 25f)
            applyBlur(requireContext(), imageViewGame, 25f)
            applyBlur(requireContext(), imageViewGame, 25f)

        }.addOnFailureListener {
            println("Erro ao fazer o download da imagem do jogo")
        }
    }


    private fun applyBlur(context: Context, imageView: ImageView, radius: Float) {
        // Obtenha a imagem da ImageView
        val originalBitmap = imageView.drawable.toBitmap()

        // Crie uma cópia da imagem original para evitar a sobreposição
        val blurredBitmap = Bitmap.createBitmap(
            originalBitmap.width,
            originalBitmap.height,
            Bitmap.Config.ARGB_8888
        )

        // Crie um Canvas para desenhar a imagem borrada
        val canvas = Canvas(blurredBitmap)

        // Crie um Paint com o filtro de desfoque
        val paint = Paint()
        paint.isAntiAlias = true
        paint.isFilterBitmap = true
        paint.shader = BitmapShader(originalBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)

        // Crie um efeito RenderScript para o desfoque
        val renderScript = RenderScript.create(context)
        val blurScript = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))
        blurScript.setInput(Allocation.createFromBitmap(renderScript, originalBitmap))
        blurScript.setRadius(radius)
        blurScript.forEach(Allocation.createFromBitmap(renderScript, blurredBitmap))

        // Desenhe a imagem borrada no Canvas
        canvas.drawBitmap(blurredBitmap, 0f, 0f, paint)

        // Defina a imagem borrada na ImageView
        imageView.setImageBitmap(blurredBitmap)

        // Libere recursos do RenderScript
        renderScript.destroy()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        timer?.cancel()
    }

    private fun startTimer() {
        timer = object : CountDownTimer(Long.MAX_VALUE, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeElapsedInMs += 1000
            }

            override fun onFinish() {

            }
        }.start()

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