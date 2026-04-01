package com.example.schoolnet

import android.content.res.ColorStateList
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.util.Base64
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.schoolnet.databinding.ItemTopicBinding
import com.google.android.material.card.MaterialCardView
import com.google.android.material.button.MaterialButton

class TopicAdapter(private var allTopics: List<Topic>?) : RecyclerView.Adapter<TopicAdapter.TopicViewHolder>() {

    private var filteredTopics: List<Topic> = allTopics ?: emptyList()

    inner class TopicViewHolder(val binding: ItemTopicBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicViewHolder {
        val binding = ItemTopicBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TopicViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TopicViewHolder, position: Int) {
        val topic = filteredTopics[position]
        
        holder.binding.topicTitle.text = topic.title ?: "Untitled Topic"
        holder.binding.formInfo.text = topic.formName ?: "Module ${topic.order}"

        holder.binding.lessonsContainer.visibility = if (topic.isExpanded) View.VISIBLE else View.GONE
        holder.binding.expandIcon.rotation = if (topic.isExpanded) 180f else 0f

        val toggleAction = View.OnClickListener {
            val currentPos = holder.adapterPosition
            if (currentPos != RecyclerView.NO_POSITION) {
                topic.isExpanded = !topic.isExpanded
                notifyItemChanged(currentPos)
            }
        }
        
        holder.binding.cardContainer.setOnClickListener(toggleAction)
        holder.binding.headerLayout.setOnClickListener(toggleAction)

        holder.binding.lessonsContainer.removeAllViews()

        topic.lessons?.forEach { lesson ->
            renderLessonItem(holder.binding.lessonsContainer, lesson)
        }

        if (topic.quiz?.questions?.isNotEmpty() == true) {
            renderQuizSection(holder.binding.lessonsContainer, topic.quiz)
        }
    }

    private fun renderLessonItem(container: LinearLayout, lesson: Lesson) {
        val context = container.context
        val textView = TextView(context).apply {
            text = HtmlCompat.fromHtml(lesson.text ?: "", HtmlCompat.FROM_HTML_MODE_LEGACY)
            setTextColor(Color.parseColor("#424242"))
            textSize = 16f
            setLineSpacing(8f, 1f)
            setPadding(0, 8, 0, 8)
        }
        container.addView(textView)

        lesson.image?.let { base64Data ->
            if (base64Data.isNotBlank()) {
                try {
                    val pureBase64 = if (base64Data.contains(",")) base64Data.split(",")[1] else base64Data
                    val imageBytes = Base64.decode(pureBase64, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    val imageView = ImageView(context).apply {
                        setImageBitmap(bitmap)
                        adjustViewBounds = true
                        scaleType = ImageView.ScaleType.FIT_CENTER
                        setPadding(0, 16, 0, 16)
                    }
                    container.addView(imageView)
                } catch (e: Exception) { }
            }
        }
    }

    private fun renderQuizSection(container: LinearLayout, quiz: Quiz) {
        val context = container.context
        val header = TextView(context).apply {
            text = "Knowledge Check"
            textSize = 20f
            setTypeface(null, Typeface.BOLD)
            setTextColor(Color.parseColor("#1976D2"))
            setPadding(0, 32, 0, 16)
        }
        container.addView(header)

        quiz.questions?.forEach { q ->
            val questionCard = MaterialCardView(context).apply {
                val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                params.setMargins(0, 12, 0, 12)
                layoutParams = params
                radius = 16f
                cardElevation = 4f
                setCardBackgroundColor(Color.WHITE)
            }

            val cardContent = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(24, 24, 24, 24)
            }

            cardContent.addView(TextView(context).apply {
                text = "Q${q.id}: ${q.question}"
                textSize = 17f
                setTextColor(Color.BLACK)
                setTypeface(null, Typeface.BOLD)
                setPadding(0, 0, 0, 16)
            })

            val feedbackText = TextView(context).apply {
                visibility = View.GONE
                textSize = 14f
                setPadding(0, 12, 0, 4)
                gravity = Gravity.CENTER
            }

            q.options?.forEachIndexed { index, option ->
                cardContent.addView(MaterialButton(context, null, com.google.android.material.R.attr.materialButtonStyle).apply {
                    text = option
                    isAllCaps = false
                    setOnClickListener {
                        feedbackText.visibility = View.VISIBLE
                        if (index == q.answer) {
                            text = "✅ $option"
                            feedbackText.text = "Correct!"
                            feedbackText.setTextColor(Color.parseColor("#2E7D32"))
                        } else {
                            text = "❌ $option"
                            feedbackText.text = "Try again!"
                            feedbackText.setTextColor(Color.parseColor("#C62828"))
                        }
                    }
                })
            }
            cardContent.addView(feedbackText)
            questionCard.addView(cardContent)
            container.addView(questionCard)
        }
    }

    fun updateData(forms: List<Form>?) {
        if (forms == null) return
        this.allTopics = forms.flatMap { form ->
            form.topics?.onEach { it.formName = form.formName } ?: emptyList()
        }
        this.filteredTopics = allTopics ?: emptyList()
        notifyDataSetChanged()
    }

    fun toggleAll(expand: Boolean) {
        allTopics?.forEach { it.isExpanded = expand }
        notifyDataSetChanged()
    }

    fun filter(query: String, formFilter: String) {
        filteredTopics = (allTopics ?: emptyList()).filter { topic ->
            val matchesQuery = query.isEmpty() ||
                    (topic.title?.contains(query, ignoreCase = true) == true)
            val matchesForm = formFilter == "All Forms" || topic.formName == formFilter
            matchesQuery && matchesForm
        }
        notifyDataSetChanged()
    }

    override fun getItemCount() = filteredTopics.size
}
