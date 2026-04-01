package com.example.schoolnet

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.schoolnet.databinding.ItemSubjectBinding
import android.graphics.Color
import android.util.Log

class SubjectAdapter(
    private val subjects: List<Subject>,
    private val onSubjectClick: (Subject) -> Unit
) : RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder>() {

    inner class SubjectViewHolder(val binding: ItemSubjectBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder {
        val binding = ItemSubjectBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SubjectViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SubjectViewHolder, position: Int) {
        val subject = subjects[position]
        holder.binding.subjectName.text = subject.name
        holder.binding.subjectIcon.setImageResource(subject.iconRes)
        
        try {
            val color = if (subject.color.startsWith("#")) subject.color else "#1976D2"
            holder.binding.iconContainer.setCardBackgroundColor(Color.parseColor(color))
        } catch (e: Exception) {
            holder.binding.iconContainer.setCardBackgroundColor(Color.parseColor("#1976D2"))
        }
        
        // Use the root itemView to ensure the largest possible click area
        holder.itemView.isClickable = true
        holder.itemView.isFocusable = true
        holder.itemView.setOnClickListener {
            Log.d("SubjectAdapter", "Clicked on subject: ${subject.name}")
            onSubjectClick(subject)
        }
        
        // Also set on the card just in case
        holder.binding.subjectCard.setOnClickListener {
            onSubjectClick(subject)
        }
    }

    override fun getItemCount() = subjects.size
}
