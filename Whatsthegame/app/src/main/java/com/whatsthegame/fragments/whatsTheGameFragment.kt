package com.whatsthegame.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
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
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.whatsthegame.Api.ViewModel.*
import com.whatsthegame.models.GuessDiaryGame
import com.whatsthegame.tutorial.TutorialWhatsThegame
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
    private lateinit var userVipViewModel: UserVipViewModel
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

    private var mInterstitialAd: InterstitialAd? = null
    private var rewardedAd: RewardedAd? = null
    private var watchedRewardAd = false


    @SuppressLint("ClickableViewAccessibility", "MissingInflatedId", "SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_whats_the_game, container, false)

        val sharedPreferences = requireContext().getSharedPreferences("Preferences", Context.MODE_PRIVATE)

        val adController = sharedPreferences.getBoolean("adControl", false)

        val timestamp = sharedPreferences.getLong("adControlTimestamp", 0L)
        val vinteEQuatroHoras = 24 * 60 * 60 * 1000
        if (System.currentTimeMillis() - timestamp >= vinteEQuatroHoras) {
            val editor = sharedPreferences.edit()
            editor.putBoolean("adControl", false)
            editor.apply()
        }

        val authToken = sharedPreferences.getString("tokenJwt", "")
        if (authToken.isNullOrEmpty()) {
            if (!adController) {
                loadAdInterstitial()
            }
        } else {
            val decodedJWT: DecodedJWT = JWT.decode(authToken)
            val userId = decodedJWT.subject

            userVipViewModel = ViewModelProvider(this).get(UserVipViewModel::class.java)
            userVipViewModel.vip.observe(viewLifecycleOwner) { vip ->
                val userVip = vip ?: false
                if (!userVip && !adController) {
                    loadAdInterstitial()
                }
            }

            userVipViewModel.getVip(userId.toLong())
        }



        loadAdRewarded()
        startTimer()

        val searchView = rootView.findViewById<SearchView>(R.id.searchView)
        val gameNameListView = rootView.findViewById<ListView>(R.id.gameNameListView)

        searchView.setOnClickListener {
            searchView.isIconified = false
        }

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


       // if (lifesTimestamp > 0 && (currentTimeMillis - lifesTimestamp) < (24 * 60 * 60 * 1000)) {
           // remainingLives = sharedPreferences.getInt("remainingLives", 5)

           // lifesCounter.text = "$remainingLives vidas restantes"
       // }

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

                                if (watchedRewardAd) {
                                    findNavController().navigate(R.id.action_whatsTheGame_to_gameOverFragment)
                                } else {

                                    val alertDialogBuilder = AlertDialog.Builder(
                                        ContextThemeWrapper(
                                            requireContext(),
                                            R.style.AlertDialogStyle
                                        )
                                    )

                                    alertDialogBuilder.setTitle("Sem vidas restantes")
                                    alertDialogBuilder.setMessage("Você gostaria de assistir um anúncio para mais uma última chance?")

                                    alertDialogBuilder.setPositiveButton("Sim") { dialog, which ->

                                        rewardedAd?.let { ad ->
                                            ad.show(
                                                requireActivity(),
                                                OnUserEarnedRewardListener { rewardItem ->
                                                    watchedRewardAd = true
                                                    remainingLives = 1
                                                    lifesCounter.text =
                                                        "$remainingLives vidas restantes"
                                                })
                                        } ?: run {
                                            println("The rewarded ad wasn't ready yet.")
                                        }
                                    }


                                alertDialogBuilder.setNegativeButton("Não") { dialog, which ->
                                    if (mInterstitialAd != null) {
                                        mInterstitialAd?.show(requireActivity())
                                    } else {
                                        println("O anúncio intersticial ainda não estava pronto.")
                                    }
                                    findNavController().navigate(R.id.action_whatsTheGame_to_gameOverFragment)
                                }

                                alertDialogBuilder.create().show()
                             }
                            } else {

                                val imageViewGame = view!!.findViewById<ImageView>(R.id.imageViewGame)
                                    when (remainingLives) {
                                        4 -> displayMosaic(requireContext(), imageViewGame, blockSize = 50)
                                        3 -> displayMosaic(requireContext(), imageViewGame, blockSize = 30)
                                        2 -> displayMosaic(requireContext(), imageViewGame, blockSize = 20)
                                        1 -> displayMosaic(requireContext(), imageViewGame, blockSize = 10)
                                        else -> {
                                            println("Número de vidas desconhecido: $remainingLives")
                                        }
                                    }


                                val searchView = rootView.findViewById<SearchView>(R.id.searchView)
                                searchView.setQuery("", false)

                                val inflater = layoutInflater
                                val layout = inflater.inflate(R.layout.submit_layout, null)
                                val toastText = layout.findViewById<TextView>(R.id.empty_submit_text)
                                toastText.text = "Jogo errado!"
                                val toast = Toast(requireContext())
                                toast.duration = Toast.LENGTH_SHORT
                                toast.view = layout
                                toast.show()
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
                               if (mInterstitialAd != null) {
                                   mInterstitialAd?.show(requireActivity())
                               } else {
                                   println("O anúncio intersticial ainda não estava pronto.")
                               }
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

                                if (mInterstitialAd != null) {
                                    mInterstitialAd?.show(requireActivity())
                                } else {
                                    println("O anúncio intersticial ainda não estava pronto.")
                                }

                                findNavController().navigate(R.id.action_whatsTheGame_to_rightAnswerLoggedFragment)

                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            }
                        } else {

                            if (mInterstitialAd != null) {
                                mInterstitialAd?.show(requireActivity())
                            } else {
                                println("O anúncio intersticial ainda não estava pronto.")
                            }

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
                        println("Caiu aqui")
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
    private fun loadAdInterstitial(){
        var adRequest = AdRequest.Builder().build()

        InterstitialAd.load(requireContext(),"ca-app-pub-3940256099942544/1033173712", adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                println(adError?.toString())
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                println("Ad was loaded.")
                mInterstitialAd = interstitialAd
            }
        })
    }

    private fun loadAdRewarded(){
        var adRequest = AdRequest.Builder().build()
        RewardedAd.load(requireContext(),"ca-app-pub-3940256099942544/5224354917", adRequest, object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                println(adError?.toString())
                rewardedAd = null
            }

            override fun onAdLoaded(ad: RewardedAd) {
                println("Ad was loaded.")
                rewardedAd = ad
            }
        })
    }


    private fun getImageFromBucket() {
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference
        val imageRef: StorageReference = storageRef.child("/capa jogos/$gameImage.png")

        val screenWidth = resources.displayMetrics.widthPixels // Largura da tela
        val screenHeight = resources.displayMetrics.heightPixels // Altura da tela

        val imageViewGame = view?.findViewById<ImageView>(R.id.imageViewGame)

        val localFile = File.createTempFile("temp_image", "jpg")

        imageRef.getFile(localFile).addOnSuccessListener { taskSnapshot ->
            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)

            val targetWidth = screenWidth
            val scaleFactor = targetWidth.toFloat() / bitmap.width
            val targetHeight = (bitmap.height * scaleFactor).toInt()

            val resizedBitmap = Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, false)

            imageViewGame!!.setImageBitmap(resizedBitmap)

            applyMosaic(requireContext(), imageViewGame, blockSize = 70)

        }.addOnFailureListener {
            println("Erro ao fazer o download da imagem do jogo")
        }
    }

    private var originalBitmap: Bitmap? = null
    private fun applyMosaic(context: Context, imageView: ImageView, blockSize: Int) {
        originalBitmap = imageView.drawable.toBitmap()

        // Redimensionar a imagem original para uma resolução menor
        val originalWidth = originalBitmap!!.width
        val originalHeight = originalBitmap!!.height
        val scaledBitmap = Bitmap.createScaledBitmap(originalBitmap!!, originalWidth / 2, originalHeight / 2, true)

        val mosaicBitmap = Bitmap.createBitmap(
            scaledBitmap.width,
            scaledBitmap.height,
            Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(mosaicBitmap)
        val paint = Paint()

        for (x in 0 until scaledBitmap.width step blockSize) {
            for (y in 0 until scaledBitmap.height step blockSize) {
                var redSum = 0
                var greenSum = 0
                var blueSum = 0
                var pixelCount = 0

                for (i in x until x + blockSize) {
                    for (j in y until y + blockSize) {
                        if (i < scaledBitmap.width && j < scaledBitmap.height) {
                            val pixelColor = scaledBitmap.getPixel(i, j)
                            redSum += Color.red(pixelColor)
                            greenSum += Color.green(pixelColor)
                            blueSum += Color.blue(pixelColor)
                            pixelCount++
                        }
                    }
                }

                val averageRed = redSum / pixelCount
                val averageGreen = greenSum / pixelCount
                val averageBlue = blueSum / pixelCount

                val blockColor = Color.rgb(averageRed, averageGreen, averageBlue)
                paint.color = blockColor
                canvas.drawRect(
                    x.toFloat(),
                    y.toFloat(),
                    (x + blockSize).toFloat(),
                    (y + blockSize).toFloat(),
                    paint
                )
            }
        }

        imageView.setImageBitmap(mosaicBitmap)
    }

    private fun displayMosaic(context: Context, imageView: ImageView, blockSize: Int) {
        if (originalBitmap != null) {
            // Redimensionar a imagem original para uma resolução menor
            val originalWidth = originalBitmap!!.width
            val originalHeight = originalBitmap!!.height
            val scaledBitmap = Bitmap.createScaledBitmap(originalBitmap!!, originalWidth / 2, originalHeight / 2, true)

            val mosaicBitmap = scaledBitmap.copy(scaledBitmap.config, true)

            val width = mosaicBitmap.width
            val height = mosaicBitmap.height

            for (x in 0 until width step blockSize) {
                for (y in 0 until height step blockSize) {
                    var totalRed = 0
                    var totalGreen = 0
                    var totalBlue = 0
                    var blockPixels = 0

                    for (i in x until x + blockSize) {
                        for (j in y until y + blockSize) {
                            if (i < width && j < height) {
                                val pixelColor = mosaicBitmap.getPixel(i, j)
                                totalRed += Color.red(pixelColor)
                                totalGreen += Color.green(pixelColor)
                                totalBlue += Color.blue(pixelColor)
                                blockPixels++
                            }
                        }
                    }

                    val averageRed = totalRed / blockPixels
                    val averageGreen = totalGreen / blockPixels
                    val averageBlue = totalBlue / blockPixels

                    for (i in x until x + blockSize) {
                        for (j in y until y + blockSize) {
                            if (i < width && j < height) {
                                mosaicBitmap.setPixel(i, j, Color.rgb(averageRed, averageGreen, averageBlue))
                            }
                        }
                    }
                }
            }

            imageView.setImageBitmap(mosaicBitmap)
        } else {
            println("Não há imagem original disponível.")
        }
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