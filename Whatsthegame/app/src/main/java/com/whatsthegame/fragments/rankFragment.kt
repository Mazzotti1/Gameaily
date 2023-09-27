package com.whatsthegame.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.whatsthegame.R

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_rank, container, false)

        val userList = listOf(
            User("Tiago", "Ouro", "Division 3", 300),
            User("Mateus", "Platina", "Division 2", 5900),
            User("Jorge", "Prata", "Division 2", 100),
            User("Gabriel", "Diamante", "Division 1", 1200),
        )




        val sortedUserList = userList.sortedByDescending { it.points }
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        val adapter = UserAdapter(sortedUserList)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())



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

data class User(val username: String, val rank: String, val division: String, val points: Int)



class UserAdapter(private val userList: List<User>) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val usernameTextView: TextView = itemView.findViewById(R.id.username)
        val rankTextView: TextView = itemView.findViewById(R.id.rank)
        val divisionTextView: TextView = itemView.findViewById(R.id.division)
        val pointsTextView: TextView = itemView.findViewById(R.id.points)
        val iconImageView: ImageView = itemView.findViewById(R.id.iconImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return ViewHolder(itemView)
    }

    private val rankIcons = listOf(
        R.drawable.firstposition,
        R.drawable.secondposition,
        R.drawable.thirdposition
    )
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentUser = userList[position]

        holder.usernameTextView.text = currentUser.username
        holder.rankTextView.text = currentUser.rank
        holder.divisionTextView.text = currentUser.division
        holder.pointsTextView.text = "${currentUser.points} Pontos"

        if (position < 3) {
            // Defina o ícone com base na posição
            holder.iconImageView.setImageResource(rankIcons[position])
        } else {
            // Se não estiver nas três primeiras posições, defina um ícone padrão ou oculte o ícone
            holder.iconImageView.setImageResource(R.drawable.play)
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }
}
