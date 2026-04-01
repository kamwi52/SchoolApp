package com.example.schoolnet

object SubjectRepository {
    val subjects = mapOf(
        "maths" to Subject("maths", "MATHS", "", android.R.drawable.ic_menu_today, "#1976D2"),
        "english" to Subject("english", "ENGLISH", "", android.R.drawable.ic_menu_sort_alphabetically, "#7B1FA2"),
        "civic" to Subject("civic", "CIVIC EDUCATION", "", android.R.drawable.ic_menu_edit, "#FBC02D"),
        "ict" to Subject("ict", "I.C.T", "", android.R.drawable.ic_menu_view, "#1B5E20"),
        "biology" to Subject("biology", "BIOLOGY", "", android.R.drawable.ic_menu_compass, "#388E3C"),
        "chemistry" to Subject("chemistry", "CHEMISTRY", "", android.R.drawable.ic_menu_directions, "#F57C00"),
        "physics" to Subject("physics", "PHYSICS", "", android.R.drawable.ic_menu_info_details, "#D32F2F"),
        "commerce" to Subject("commerce", "COMMERCE", "", android.R.drawable.ic_menu_agenda, "#00796B"),
        "pa" to Subject("pa", "P.A", "", android.R.drawable.ic_menu_save, "#455A64"),
        "maths1" to Subject("maths1", "MATHEMATICS 1", "", android.R.drawable.ic_menu_today, "#1976D2"),
        "lit_tonga" to Subject("lit_tonga", "LIT. IN TONGA", "", android.R.drawable.ic_menu_sort_alphabetically, "#7B1FA2"),
        "art_design" to Subject("art_design", "ART & DESIGN", "", android.R.drawable.ic_menu_camera, "#E91E63"),
        "tonga" to Subject("tonga", "TONGA", "", android.R.drawable.ic_menu_myplaces, "#5D4037"),
        "history" to Subject("history", "HISTORY", "", android.R.drawable.ic_menu_recent_history, "#795548"),
        "lit_english" to Subject("lit_english", "LIT.IN.ENGLISH", "", android.R.drawable.ic_menu_sort_alphabetically, "#673AB7"),
        "design_tech" to Subject("design_tech", "DESIGN & TECHNOLOG", "", android.R.drawable.ic_menu_manage, "#FF5722")
    )

    fun getSubject(id: String): Subject {
        return subjects[id] ?: throw IllegalArgumentException("Subject not found: $id")
    }

    fun getSubjects(ids: List<String>): List<Subject> {
        return ids.map { getSubject(it) }
    }
}
