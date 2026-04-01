package com.example.schoolnet

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.schoolnet.databinding.ItemPathwayBinding

class PathwayAdapter(
    private val pathways: List<Pathway>,
    private val onPathwayClick: (Pathway) -> Unit,
    private val onSubjectClick: (Subject) -> Unit
) : RecyclerView.Adapter<PathwayAdapter.PathwayViewHolder>() {

    inner class PathwayViewHolder(val binding: ItemPathwayBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PathwayViewHolder {
        val binding = ItemPathwayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PathwayViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PathwayViewHolder, position: Int) {
        val pathway = pathways[position]
        holder.binding.pathwayName.text = pathway.name
        
        try {
            val color = if (pathway.color.startsWith("#")) pathway.color else "#F8F9FA"
            holder.binding.pathwayCardBackground.setBackgroundColor(Color.parseColor(color))
        } catch (e: Exception) {
            holder.binding.pathwayCardBackground.setBackgroundColor(Color.parseColor("#F8F9FA"))
        }

        // Set click listener on the entire item view
        holder.itemView.isClickable = true
        holder.itemView.isFocusable = true
        holder.itemView.setOnClickListener {
            Log.d("PathwayAdapter", "Pathway card clicked: ${pathway.name}")
            onPathwayClick(pathway)
        }
        
        // Also set it on the card specifically
        holder.binding.pathwayCard.setOnClickListener {
            onPathwayClick(pathway)
        }

        // Setup subjects preview
        val subjectAdapter = SubjectAdapter(pathway.subjects, onSubjectClick)
        holder.binding.subjectsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = subjectAdapter
            setHasFixedSize(true)
            // Disable nested scrolling to ensure parent gets the touch if there's no horizontal scroll needed
            isNestedScrollingEnabled = false
            // If no subjects, hide to prevent touch interception
            visibility = if (pathway.subjects.isNotEmpty()) android.view.View.VISIBLE else android.view.View.GONE
        }
    }

    override fun getItemCount() = pathways.size
}
