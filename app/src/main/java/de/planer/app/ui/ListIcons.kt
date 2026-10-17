package de.planer.app.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.filled.Yard
import androidx.compose.ui.graphics.vector.ImageVector

/** Standardsymbole für Listen. Der Enum-Name wird in der Datenbank gespeichert. */
enum class ListIcon(val label: String, val vector: ImageVector) {
    LIST("Liste", Icons.AutoMirrored.Filled.List),
    INBOX("Eingang", Icons.Filled.Inbox),
    WORK("Arbeit", Icons.Filled.Work),
    HOME("Zuhause", Icons.Filled.Home),
    SHOPPING("Einkauf", Icons.Filled.ShoppingCart),
    SCHOOL("Lernen", Icons.Filled.School),
    BOOK("Lesen", Icons.Filled.MenuBook),
    FITNESS("Sport", Icons.Filled.FitnessCenter),
    HEALTH("Gesundheit", Icons.Filled.LocalHospital),
    FOOD("Essen", Icons.Filled.Restaurant),
    TRAVEL("Reisen", Icons.Filled.Flight),
    CAR("Auto", Icons.Filled.DirectionsCar),
    MONEY("Finanzen", Icons.Filled.Savings),
    EVENT("Termine", Icons.Filled.Event),
    PARTY("Feiern", Icons.Filled.Celebration),
    IDEA("Ideen", Icons.Filled.Lightbulb),
    MUSIC("Musik", Icons.Filled.MusicNote),
    PETS("Haustiere", Icons.Filled.Pets),
    GARDEN("Garten", Icons.Filled.Yard),
    REPAIR("Reparieren", Icons.Filled.Build),
    CODE("Programmieren", Icons.Filled.Code),
    FAVORITE("Herz", Icons.Filled.Favorite),
    STAR("Stern", Icons.Filled.Star);

    companion object {
        fun of(name: String): ListIcon = entries.firstOrNull { it.name == name } ?: LIST
    }
}
