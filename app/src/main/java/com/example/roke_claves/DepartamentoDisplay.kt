package com.example.roke_claves

data class DepartamentoDisplay(
    val id: Int,
    val label: String
) {
    override fun toString(): String = label
}
