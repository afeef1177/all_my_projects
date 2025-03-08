package com.example.quizapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quizapp.databinding.ActivityMainBinding
import com.google.firebase.database.FirebaseDatabase



class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var quizModelList : MutableList<QuizModel>
    lateinit var adapter: QuizListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        quizModelList = mutableListOf()
        getDataFromFirebase()


    }

    private fun setupRecyclerView(){
        binding.progressBar.visibility = View.GONE
        adapter = QuizListAdapter(quizModelList)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun getDataFromFirebase() {
        binding.progressBar.visibility = View.VISIBLE
        FirebaseDatabase.getInstance().reference.child("quizzes")
            .get()
            .addOnSuccessListener { dataSnapshot ->
                if (dataSnapshot.exists()) {
                    quizModelList.clear()
                    for (snapshot in dataSnapshot.children) {
                        val quizId = snapshot.key ?: ""
                        val title = snapshot.child("title").value.toString()
                        val subtitle = snapshot.child("subtitle").value.toString()
                        val time = snapshot.child("time").value.toString()

                        val questionList = mutableListOf<QuestionModel>()
                        val questionSnapshot = snapshot.child("questionList")

                        for (qSnap in questionSnapshot.children) {
                            val question = qSnap.child("question").value.toString()
                            val options = qSnap.child("options").children.map { it.value.toString() }
                            val correct = qSnap.child("correct").value.toString()
                            questionList.add(QuestionModel(question, options, correct))
                        }

                        val quizModel = QuizModel(quizId, title, subtitle, time, questionList)
                        quizModelList.add(quizModel)
                    }
                    setupRecyclerView()
                } else {
                    Log.d("FirebaseData", "No data found in Firebase")
                }
                binding.progressBar.visibility = View.GONE
            }
            .addOnFailureListener { exception ->
                Log.e("FirebaseData", "Error fetching data", exception)
                binding.progressBar.visibility = View.GONE
            }
    }


}














