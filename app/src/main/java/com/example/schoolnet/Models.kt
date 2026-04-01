package com.example.schoolnet

import com.google.gson.annotations.SerializedName

data class PathwayResponse(
    @SerializedName("id") val id: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("subjects") val subjects: List<SubjectItem>?,
    @SerializedName("color") val color: String? = "#F8F9FA"
)

data class SubjectItem(
    @SerializedName("name") val name: String?,
    @SerializedName("id") val id: String?,
    @SerializedName("icon") val icon: String? = null,
    @SerializedName("color") val color: String? = "#1976D2"
)

data class SchoolData(
    @SerializedName("forms") val forms: List<Form>?
)

data class Form(
    @SerializedName("formId") val formId: String?,
    @SerializedName("formName") val formName: String?,
    @SerializedName("topics") val topics: List<Topic>?
)

data class Topic(
    @SerializedName("topicId") val topicId: String?,
    @SerializedName("title") val title: String?,
    @SerializedName("order") val order: Int?,
    @SerializedName("lessons") val lessons: List<Lesson>?,
    @SerializedName("quiz") val quiz: Quiz?,
    var isExpanded: Boolean = false,
    var formName: String? = null
)

data class Lesson(
    @SerializedName("order") val order: Int?,
    @SerializedName("text") val text: String?,
    @SerializedName("image") val image: String?
)

data class Quiz(
    @SerializedName("passMark") val passMark: Int?,
    @SerializedName("questions") val questions: List<Question>?
)

data class Question(
    @SerializedName("id") val id: Int?,
    @SerializedName("question") val question: String?,
    @SerializedName("options") val options: List<String>?,
    @SerializedName("answer") val answer: Int?
)

data class Subject(
    val id: String,
    val name: String,
    val description: String,
    val iconRes: Int,
    val color: String
)

data class Pathway(
    val id: String,
    val name: String,
    val subjects: List<Subject>,
    val color: String
)
