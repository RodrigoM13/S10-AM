package com.example.app_s10

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class GamesListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: GameAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_list)

        // Inicializar Firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        // Referencias a vistas
        recyclerView = findViewById(R.id.recyclerViewGames)
        progressBar = findViewById(R.id.progressBarGames)

        // Configurar RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = GameAdapter(emptyList())
        recyclerView.adapter = adapter

        // Cargar datos
        fetchGames()
    }

    private fun fetchGames() {
        val userId = auth.currentUser?.uid ?: return

        progressBar.visibility = View.VISIBLE

        val userGamesRef = database.child("games").child(userId)

        userGamesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val gameList = mutableListOf<Game>()
                for (child in snapshot.children) {
                    val game = child.getValue(Game::class.java)
                    if (game != null) {
                        gameList.add(game)
                    }
                }
                adapter.updateGames(gameList)
                progressBar.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                progressBar.visibility = View.GONE
                Toast.makeText(this@GamesListActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
