package com.example.schoolnet

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.schoolnet.databinding.ActivityTopicListBinding
import com.google.android.material.chip.Chip
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody

class TopicListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTopicListBinding
    private lateinit var adapter: TopicAdapter
    private val gson = Gson()
    private val PREFS_NAME = "SchoolNetPrefs"
    private var isAllExpanded = false
    private var currentFormFilter = "All Forms"
    private var subjectId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTopicListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        subjectId = intent.getStringExtra("subject_id")
        val subjectName = intent.getStringExtra("subject_name") ?: "Content"
        
        binding.toolbar.title = subjectName
        binding.toolbar.setNavigationOnClickListener { finish() }

        setupRecyclerView()
        setupUIListeners()
        
        loadCachedData()
        fetchData()
    }

    private fun setupUIListeners() {
        binding.btnToggleAll.setOnClickListener {
            isAllExpanded = !isAllExpanded
            adapter.toggleAll(isAllExpanded)
            binding.btnToggleAll.text = if (isAllExpanded) "Collapse All" else "Expand All"
        }

        binding.swipeRefresh.setColorSchemeColors(0xFF1B5E20.toInt())
        binding.swipeRefresh.setOnRefreshListener {
            fetchData(isManualRefresh = true)
        }

        binding.btnRetry.setOnClickListener {
            fetchData()
        }

        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter(s.toString(), currentFormFilter)
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.formChipGroup.setOnCheckedChangeListener { group, checkedId ->
            val chip = group.findViewById<Chip>(checkedId)
            currentFormFilter = chip?.text?.toString() ?: "All Forms"
            adapter.filter(binding.searchEditText.text.toString(), currentFormFilter)
        }
    }

    private fun setupRecyclerView() {
        adapter = TopicAdapter(emptyList())
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun loadCachedData() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val cacheKey = "school_data_$subjectId"
        val cachedJson = prefs.getString(cacheKey, null)
        if (cachedJson != null) {
            try {
                val type = object : TypeToken<List<Form>>() {}.type
                val cachedData: List<Form> = gson.fromJson(cachedJson, type)
                updateUI(cachedData)
            } catch (e: Exception) {
                Log.e("TopicListActivity", "Failed to parse cached data", e)
            }
        }
    }

    private fun updateUI(forms: List<Form>) {
        if (forms.isEmpty()) {
            showError("No valid content found in this folder.")
            return
        }
        binding.errorContainer.visibility = View.GONE
        adapter.updateData(forms)
        populateFormChips(forms)
    }

    private fun populateFormChips(forms: List<Form>) {
        val allFormsChip = binding.chipAll
        binding.formChipGroup.removeAllViews()
        binding.formChipGroup.addView(allFormsChip)

        forms.map { it.formName }.distinct().forEach { formName ->
            val chip = Chip(ContextThemeWrapper(this, com.google.android.material.R.style.Widget_MaterialComponents_Chip_Choice))
            chip.apply {
                text = formName
                isCheckable = true
                id = View.generateViewId()
                setChipBackgroundColorResource(R.color.chip_bg_state_list)
            }
            binding.formChipGroup.addView(chip)
        }
    }

    private fun saveToCache(json: String) {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString("school_data_$subjectId", json).apply()
    }

    private fun fetchData(isManualRefresh: Boolean = false) {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val cacheKey = "school_data_$subjectId"
        
        if (!isManualRefresh && !prefs.contains(cacheKey)) {
            binding.progressBar.visibility = View.VISIBLE
        }
        binding.errorContainer.visibility = View.GONE

        lifecycleScope.launch {
            try {
                Log.d("TopicListActivity", "Requesting content for folder: $subjectId")
                val response = RetrofitClient.instance.getSchoolData(subjectId)

                if (response.isSuccessful && response.body() != null) {
                    val rawJson = response.body()!!.string()
                    Log.d("TopicListActivity", "JSON received: $rawJson")

                    // Attempt to parse as List<Form>
                    val forms: List<Form> = try {
                        val type = object : TypeToken<List<Form>>() {}.type
                        gson.fromJson(rawJson, type)
                    } catch (e: Exception) {
                        Log.e("TopicListActivity", "Parsing error, data might be malformed.")
                        emptyList()
                    }

                    if (forms.isNotEmpty()) {
                        withContext(Dispatchers.IO) { saveToCache(rawJson) }
                        updateUI(forms)
                    } else {
                        showError("Folder is empty or content is still being processed.")
                    }
                } else {
                    if (!prefs.contains(cacheKey)) {
                        showError("Server Error: ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                Log.e("TopicListActivity", "Fetch failed", e)
                if (!prefs.contains(cacheKey)) {
                    showError("Connection failed. Check your internet.")
                }
            } finally {
                binding.progressBar.visibility = View.GONE
                binding.swipeRefresh.isRefreshing = false
            }
        }
    }

    private fun showError(message: String) {
        binding.errorText.text = message
        binding.errorContainer.visibility = View.VISIBLE
    }
}
