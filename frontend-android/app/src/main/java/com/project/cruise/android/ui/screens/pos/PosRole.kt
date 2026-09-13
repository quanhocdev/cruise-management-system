package com.project.cruise.android.ui.screens.pos

enum class PosRole(
    val apiRole: String,
    val title: String,
    val subtitle: String
) {
    FINANCE("FINANCE", "Lễ tân & tài chính", "Check-in và checkout hành khách"),
    CONVENIENCE("CONVENIENCE", "Dịch vụ tiện ích", "Quản lý hành khách sử dụng tiện ích"),
    ONBOARD("ONBOARD", "Hoạt động trên tàu", "Quản lý người tham gia hoạt động trên tàu"),
    SHORE("SHORE", "Hoạt động bờ", "Quản lý người tham gia hoạt động tham quan");

    companion object {
        fun fromApiRole(value: String?): PosRole? {
            val normalized = value?.trim()?.uppercase()?.removePrefix("ROLE_") ?: return null
            return entries.firstOrNull { it.apiRole == normalized }
        }
    }
}
