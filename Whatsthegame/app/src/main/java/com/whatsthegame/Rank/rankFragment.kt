package com.whatsthegame.fragments

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
import com.whatsthegame.Api.ViewModel.AllGamesViewModel
import com.whatsthegame.Api.ViewModel.AllUsersViewModel
import com.whatsthegame.Api.ViewModel.DiaryGameViewModel
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
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_rank, container, false)

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



