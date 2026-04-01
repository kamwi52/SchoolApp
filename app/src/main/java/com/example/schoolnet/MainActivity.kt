package com.example.schoolnet

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.schoolnet.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        fetchCurriculum()
    }

    private fun setupUI() {
        binding.swipeRefresh.setColorSchemeColors(0xFF1B5E20.toInt())
        binding.swipeRefresh.setOnRefreshListener {
            fetchCurriculum()
        }

        binding.btnRetry.setOnClickListener {
            fetchCurriculum()
        }
    }

    private fun fetchCurriculum() {
        lifecycleScope.launch {
            try {
                binding.progressBar.visibility = View.VISIBLE
                binding.errorContainer.visibility = View.GONE
                
                val response = try {
                    RetrofitClient.instance.getCurriculum()
                } catch (e: Exception) {
                    Log.e("MainActivity", "Network fetch failed", e)
                    emptyList()
                }
                
                if (response.isEmpty()) {
                    showError("No categories found. Check your internet connection.")
                    return@launch
                }

                val pathways = response.map { res ->
                    Pathway(
                        id = res.id ?: res.name ?: "",
                        name = res.name ?: "Unknown Category",
                        color = if (res.color.isNullOrBlank()) "#F8F9FA" else res.color!!,
                        subjects = res.subjects?.map { sub ->
                            Subject(
                                id = sub.id ?: "",
                                name = sub.name ?: "Unknown Subject",
                                description = "",
                                iconRes = getIconForSubject(sub.name ?: ""),
                                color = if (sub.color.isNullOrBlank()) "#1976D2" else sub.color!!
                            )
                        } ?: emptyList()
                    )
                }

                setupRecyclerView(pathways)
                binding.subjectsRecyclerView.visibility = View.VISIBLE
                binding.errorContainer.visibility = View.GONE
                
            } catch (e: Exception) {
                Log.e("MainActivity", "Error processing curriculum", e)
                showError("Error: ${e.localizedMessage}")
            } finally {
                binding.progressBar.visibility = View.GONE
                binding.swipeRefresh.isRefreshing = false
            }
        }
    }

    private fun showError(message: String) {
        binding.errorContainer.visibility = View.VISIBLE
        binding.errorText.text = message
        binding.subjectsRecyclerView.visibility = View.GONE
    }

    private fun setupRecyclerView(pathways: List<Pathway>) {
        val adapter = PathwayAdapter(
            pathways = pathways,
            onPathwayClick = { pathway ->
                // When a parent folder (Pathway) is clicked
                Log.d("MainActivity", "Opening Pathway Folder: ${pathway.name} (ID: ${pathway.id})")
                openTopicList(pathway.id, pathway.name)
            },
            onSubjectClick = { subject ->
                // When a sub-folder (Subject) is clicked
                Log.d("MainActivity", "Opening Subject Folder: ${subject.name} (ID: ${subject.id})")
                openTopicList(subject.id, subject.name)
            }
        )

        binding.subjectsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.subjectsRecyclerView.adapter = adapter
    }

    private fun openTopicList(id: String, name: String) {
        val intent = Intent(this, TopicListActivity::class.java).apply {
            putExtra("subject_id", id)
            putExtra("subject_name", name)
        }
        startActivity(intent)
    }

    private fun getIconForSubject(name: String): Int {
        val n = name.uppercase()
        return when {
            n.contains("MATH") -> android.R.drawable.ic_menu_today
            n.contains("ENGLISH") -> android.R.drawable.ic_menu_sort_alphabetically
            n.contains("CIVIC") -> android.R.drawable.ic_menu_edit
            n.contains("ICT") || n.contains("COMPUTER") -> android.R.drawable.ic_menu_view
            n.contains("BIOLOGY") || n.contains("SCIENCE") -> android.R.drawable.ic_menu_compass
            n.contains("CHEMISTRY") -> android.R.drawable.ic_menu_directions
            n.contains("PHYSICS") -> android.R.drawable.ic_menu_info_details
            n.contains("COMMERCE") -> android.R.drawable.ic_menu_agenda
            n.contains("HISTORY") -> android.R.drawable.ic_menu_recent_history
            else -> android.R.drawable.ic_menu_help
        }
    }
}
