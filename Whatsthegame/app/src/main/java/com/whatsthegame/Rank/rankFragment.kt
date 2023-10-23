package com.whatsthegame.fragments

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.whatsthegame.Api.ViewModel.AllGamesViewModel
import com.whatsthegame.Api.ViewModel.AllUsersViewModel
import com.whatsthegame.Api.ViewModel.DiaryGameViewModel
import com.whatsthegame.Api.ViewModel.UserVipViewModel
import com.whatsthegame.R
import com.whatsthegame.Rank.User
import com.whatsthegame.Rank.UserAdapter
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [rankFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class rankFragment : Fragment() {
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
    private lateinit var originalUserList: List<User>
    private var isSearching = false
    private lateinit var adapter: UserAdapter
    private var filteredList: List<User> = mutableListOf()
    private lateinit var allUsersViewModel: AllUsersViewModel
    lateinit var mAdView : AdView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_rank, container, false)


        val sharedPreferences = requireContext().getSharedPreferences("Preferences", Context.MODE_PRIVATE)
        val authToken = sharedPreferences.getString("tokenJwt", "")
        val userVipViewModel = ViewModelProvider(this).get(UserVipViewModel::class.java)

        if (authToken.isNullOrEmpty()) {
            mAdView = view.findViewById(R.id.adView)
            val adRequest = AdRequest.Builder().build()
            mAdView.loadAd(adRequest)
        } else {
            val decodedJWT: DecodedJWT = JWT.decode(authToken)
            val userId = decodedJWT.subject

            userVipViewModel.vip.observe(this) { vip ->
                val userVip = vip ?: false
                if (!userVip) {
                    mAdView = view.findViewById(R.id.adView)
                    val adRequest = AdRequest.Builder().build()
                    mAdView.loadAd(adRequest)
                }
            }

            userVipViewModel.getVip(userId.toLong())
        }

        allUsersViewModel = ViewModelProvider(this).get(AllUsersViewModel::class.java)
        allUsersViewModel.users.observe(viewLifecycleOwner, Observer { users ->
            println("Usuarios: $users")
            if (users != null) {
                originalUserList = users.map { user ->
                    User(user.name, user.rank, user.division, user.points)
                }


                val sortedUserList = originalUserList.sortedByDescending { it.points }
                val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
                adapter = UserAdapter(sortedUserList)
                recyclerView.adapter = adapter
                recyclerView.layoutManager = LinearLayoutManager(requireContext())
            }
        })

        viewLifecycleOwner.lifecycleScope.launch {
            allUsersViewModel.fetchAllUsers()

        }

        val searchView = view.findViewById<SearchView>(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (!::originalUserList.isInitialized) {

                    return true
                }

                if (newText.isNullOrBlank()) {
                    isSearching = false

                    val sortedUserList = originalUserList.sortedByDescending { it.points }
                    val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
                    adapter = UserAdapter(sortedUserList)
                    recyclerView.adapter = adapter
                    recyclerView.layoutManager = LinearLayoutManager(requireContext())

                } else {
                    isSearching = true
                    filteredList = originalUserList.filter { user ->
                        user.username.contains(newText.orEmpty(), ignoreCase = true)
                    }
                    adapter.updateUserList(filteredList)
                }
                return true
            }
        })


        return view
    }

    private val rankIconsMap = mapOf(
        "Bronze" to R.drawable.bronze,
        "Prata" to R.drawable.prata,
        "Ouro" to R.drawable.ouro,
        "Platina" to R.drawable.platina,
        "Diamante" to R.drawable.diamante
    )

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment rankFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            rankFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}



