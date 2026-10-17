package de.planer.app.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Hiking
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocalLaundryService
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.filled.Yard
import androidx.compose.ui.graphics.vector.ImageVector

/** Farbpalette für Listen (ARGB, wie in [de.planer.app.data.TaskList.color] gespeichert). */
object ListColors {
    const val BLUE = 0xFF4772FA
    const val SKY = 0xFF29B6F6
    const val TEAL = 0xFF26A69A
    const val GREEN = 0xFF43A047
    const val LIME = 0xFF9CCC65
    const val YELLOW = 0xFFFBC02D
    const val ORANGE = 0xFFFB8C00
    const val RED = 0xFFE53935
    const val PINK = 0xFFEC407A
    const val PURPLE = 0xFF8E24AA
    const val INDIGO = 0xFF5C6BC0
    const val BROWN = 0xFF8D6E63
    const val GRAY = 0xFF78909C

    val all = listOf(BLUE, SKY, TEAL, GREEN, LIME, YELLOW, ORANGE, RED, PINK, PURPLE, INDIGO, BROWN, GRAY)
}

/**
 * Standardsymbole für Listen. Der Enum-Name wird in der Datenbank gespeichert,
 * deshalb Namen nie umbenennen, nur neue anhängen. [color] ist die Vorschlagsfarbe.
 */
enum class ListIcon(val label: String, val vector: ImageVector, val color: Long) {
    LIST("Liste", Icons.AutoMirrored.Filled.List, ListColors.BLUE),
    INBOX("Eingang", Icons.Filled.Inbox, ListColors.BLUE),
    WORK("Arbeit", Icons.Filled.Work, ListColors.BROWN),
    HOME("Zuhause", Icons.Filled.Home, ListColors.ORANGE),
    SHOPPING("Einkauf", Icons.Filled.ShoppingCart, ListColors.GREEN),
    SCHOOL("Lernen", Icons.Filled.School, ListColors.INDIGO),
    BOOK("Lesen", Icons.AutoMirrored.Filled.MenuBook, ListColors.PURPLE),
    FITNESS("Sport", Icons.Filled.FitnessCenter, ListColors.RED),
    HEALTH("Gesundheit", Icons.Filled.LocalHospital, ListColors.RED),
    FOOD("Essen", Icons.Filled.Restaurant, ListColors.ORANGE),
    TRAVEL("Reisen", Icons.Filled.Flight, ListColors.SKY),
    CAR("Auto", Icons.Filled.DirectionsCar, ListColors.GRAY),
    MONEY("Finanzen", Icons.Filled.Savings, ListColors.GREEN),
    EVENT("Termine", Icons.Filled.Event, ListColors.BLUE),
    PARTY("Feiern", Icons.Filled.Celebration, ListColors.PINK),
    IDEA("Ideen", Icons.Filled.Lightbulb, ListColors.YELLOW),
    MUSIC("Musik", Icons.Filled.MusicNote, ListColors.PURPLE),
    PETS("Haustiere", Icons.Filled.Pets, ListColors.BROWN),
    GARDEN("Garten", Icons.Filled.Yard, ListColors.LIME),
    REPAIR("Reparieren", Icons.Filled.Build, ListColors.GRAY),
    CODE("Programmieren", Icons.Filled.Code, ListColors.TEAL),
    FAVORITE("Herz", Icons.Filled.Favorite, ListColors.PINK),
    STAR("Stern", Icons.Filled.Star, ListColors.YELLOW),
    KIDS("Kinder", Icons.Filled.ChildCare, ListColors.SKY),
    FAMILY("Familie", Icons.Filled.Groups, ListColors.ORANGE),
    GIFT("Geschenke", Icons.Filled.CardGiftcard, ListColors.RED),
    CLEANING("Putzen", Icons.Filled.CleaningServices, ListColors.TEAL),
    LAUNDRY("Wäsche", Icons.Filled.LocalLaundryService, ListColors.SKY),
    MEDICATION("Medikamente", Icons.Filled.Medication, ListColors.PINK),
    WELLNESS("Wellness", Icons.Filled.Spa, ListColors.PURPLE),
    BIKE("Radfahren", Icons.AutoMirrored.Filled.DirectionsBike, ListColors.GREEN),
    HIKING("Wandern", Icons.Filled.Hiking, ListColors.BROWN),
    SOCCER("Fußball", Icons.Filled.SportsSoccer, ListColors.GREEN),
    GAMES("Spiele", Icons.Filled.SportsEsports, ListColors.INDIGO),
    MOVIE("Filme", Icons.Filled.Movie, ListColors.RED),
    PHOTO("Fotos", Icons.Filled.PhotoCamera, ListColors.GRAY),
    COFFEE("Café", Icons.Filled.LocalCafe, ListColors.BROWN),
    VACATION("Urlaub", Icons.Filled.BeachAccess, ListColors.YELLOW),
    ART("Kreativ", Icons.Filled.Brush, ListColors.PINK),
    PHONE("Anrufe", Icons.Filled.Phone, ListColors.TEAL),
    MAIL("Post & Mail", Icons.Filled.Email, ListColors.BLUE),
    LANGUAGE("Sprachen", Icons.Filled.Translate, ListColors.INDIGO),
    SCIENCE("Wissen", Icons.Filled.Science, ListColors.TEAL),
    NATURE("Umwelt", Icons.Filled.Eco, ListColors.LIME),
    WATER("Trinken", Icons.Filled.WaterDrop, ListColors.SKY);

    companion object {
        fun of(name: String): ListIcon = entries.firstOrNull { it.name == name } ?: LIST
    }
}
