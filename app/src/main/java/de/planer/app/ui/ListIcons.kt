package de.planer.app.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.BakeryDining
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BrunchDining
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Chair
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.ChildFriendly
import androidx.compose.material.icons.filled.Church
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DirectionsBoat
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Elderly
import androidx.compose.material.icons.filled.ElectricalServices
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Euro
import androidx.compose.material.icons.filled.EvStation
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Forest
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Hiking
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Icecream
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.LocalGroceryStore
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocalLaundryService
import androidx.compose.material.icons.filled.LocalPizza
import androidx.compose.material.icons.filled.Luggage
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Piano
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Plumbing
import androidx.compose.material.icons.filled.Pool
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.RamenDining
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Slideshow
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.SportsBasketball
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.SportsTennis
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material.icons.filled.TheaterComedy
import androidx.compose.material.icons.filled.Train
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.Weekend
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WineBar
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

/** Themengruppen der Symbolauswahl, in Anzeigereihenfolge. */
enum class IconCategory(val label: String) {
    GENERAL("Allgemein"),
    WORK("Arbeit & Büro"),
    HOME("Zuhause & Haushalt"),
    FAMILY("Familie & Soziales"),
    MONEY("Einkaufen & Finanzen"),
    FOOD("Essen & Trinken"),
    HEALTH("Gesundheit & Sport"),
    LEARN("Lernen & Wissen"),
    FUN("Freizeit & Kultur"),
    TRAVEL("Reisen & Unterwegs"),
    NATURE("Natur & Tiere"),
    TECH("Technik");
}

/**
 * Standardsymbole für Listen. Der Enum-Name wird in der Datenbank gespeichert,
 * deshalb Namen nie umbenennen oder löschen. [color] ist die Vorschlagsfarbe.
 */
enum class ListIcon(val label: String, val vector: ImageVector, val color: Long, val category: IconCategory) {
    // Allgemein
    LIST("Liste", Icons.AutoMirrored.Filled.List, ListColors.BLUE, IconCategory.GENERAL),
    INBOX("Eingang", Icons.Filled.Inbox, ListColors.BLUE, IconCategory.GENERAL),
    STAR("Stern", Icons.Filled.Star, ListColors.YELLOW, IconCategory.GENERAL),
    FAVORITE("Herz", Icons.Filled.Favorite, ListColors.PINK, IconCategory.GENERAL),
    FLAG("Fahne", Icons.Filled.Flag, ListColors.RED, IconCategory.GENERAL),
    BOOKMARK("Merken", Icons.Filled.Bookmark, ListColors.ORANGE, IconCategory.GENERAL),
    PIN("Pinnwand", Icons.Filled.PushPin, ListColors.RED, IconCategory.GENERAL),
    LABEL("Etikett", Icons.Filled.Label, ListColors.TEAL, IconCategory.GENERAL),
    CHECK("Erledigen", Icons.Filled.CheckCircle, ListColors.GREEN, IconCategory.GENERAL),
    IDEA("Ideen", Icons.Filled.Lightbulb, ListColors.YELLOW, IconCategory.GENERAL),
    EVENT("Termine", Icons.Filled.Event, ListColors.BLUE, IconCategory.GENERAL),
    // Arbeit & Büro
    WORK("Arbeit", Icons.Filled.Work, ListColors.BROWN, IconCategory.WORK),
    BRIEFCASE("Geschäftlich", Icons.Filled.BusinessCenter, ListColors.BROWN, IconCategory.WORK),
    COMPUTER("Computer", Icons.Filled.Computer, ListColors.INDIGO, IconCategory.WORK),
    DOCUMENT("Dokumente", Icons.Filled.Description, ListColors.BLUE, IconCategory.WORK),
    FOLDER("Ordner", Icons.Filled.Folder, ListColors.YELLOW, IconCategory.WORK),
    PRINT("Drucken", Icons.Filled.Print, ListColors.GRAY, IconCategory.WORK),
    CHART("Auswertung", Icons.Filled.BarChart, ListColors.TEAL, IconCategory.WORK),
    SLIDES("Präsentation", Icons.Filled.Slideshow, ListColors.ORANGE, IconCategory.WORK),
    BADGE("Ausweis", Icons.Filled.Badge, ListColors.INDIGO, IconCategory.WORK),
    PHONE("Anrufe", Icons.Filled.Phone, ListColors.TEAL, IconCategory.WORK),
    MAIL("Post & Mail", Icons.Filled.Email, ListColors.BLUE, IconCategory.WORK),
    CODE("Programmieren", Icons.Filled.Code, ListColors.TEAL, IconCategory.WORK),
    // Zuhause & Haushalt
    HOME("Zuhause", Icons.Filled.Home, ListColors.ORANGE, IconCategory.HOME),
    CLEANING("Putzen", Icons.Filled.CleaningServices, ListColors.TEAL, IconCategory.HOME),
    LAUNDRY("Wäsche", Icons.Filled.LocalLaundryService, ListColors.SKY, IconCategory.HOME),
    KITCHEN("Küche", Icons.Filled.Kitchen, ListColors.GRAY, IconCategory.HOME),
    BED("Schlafzimmer", Icons.Filled.Bed, ListColors.PURPLE, IconCategory.HOME),
    SOFA("Wohnzimmer", Icons.Filled.Weekend, ListColors.BROWN, IconCategory.HOME),
    CHAIR("Möbel", Icons.Filled.Chair, ListColors.BROWN, IconCategory.HOME),
    KEY("Schlüssel", Icons.Filled.Key, ListColors.YELLOW, IconCategory.HOME),
    REPAIR("Reparieren", Icons.Filled.Build, ListColors.GRAY, IconCategory.HOME),
    PLUMBING("Sanitär", Icons.Filled.Plumbing, ListColors.SKY, IconCategory.HOME),
    ELECTRIC("Elektrik", Icons.Filled.ElectricalServices, ListColors.YELLOW, IconCategory.HOME),
    TRASH("Müll", Icons.Filled.Delete, ListColors.GRAY, IconCategory.HOME),
    RECYCLING("Recycling", Icons.Filled.Recycling, ListColors.GREEN, IconCategory.HOME),
    GARDEN("Garten", Icons.Filled.Yard, ListColors.LIME, IconCategory.HOME),
    // Familie & Soziales
    FAMILY("Familie", Icons.Filled.Groups, ListColors.ORANGE, IconCategory.FAMILY),
    PERSON("Person", Icons.Filled.Person, ListColors.BLUE, IconCategory.FAMILY),
    KIDS("Kinder", Icons.Filled.ChildCare, ListColors.SKY, IconCategory.FAMILY),
    BABY("Baby", Icons.Filled.ChildFriendly, ListColors.PINK, IconCategory.FAMILY),
    ELDERLY("Eltern & Großeltern", Icons.Filled.Elderly, ListColors.BROWN, IconCategory.FAMILY),
    PARTY("Feiern", Icons.Filled.Celebration, ListColors.PINK, IconCategory.FAMILY),
    BIRTHDAY("Geburtstag", Icons.Filled.Cake, ListColors.PINK, IconCategory.FAMILY),
    GIFT("Geschenke", Icons.Filled.CardGiftcard, ListColors.RED, IconCategory.FAMILY),
    CHURCH("Kirche", Icons.Filled.Church, ListColors.PURPLE, IconCategory.FAMILY),
    VOLUNTEER("Ehrenamt", Icons.Filled.VolunteerActivism, ListColors.RED, IconCategory.FAMILY),
    // Einkaufen & Finanzen
    SHOPPING("Einkauf", Icons.Filled.ShoppingCart, ListColors.GREEN, IconCategory.MONEY),
    BAG("Shopping", Icons.Filled.ShoppingBag, ListColors.PINK, IconCategory.MONEY),
    STORE("Geschäft", Icons.Filled.Store, ListColors.ORANGE, IconCategory.MONEY),
    GROCERY("Lebensmittel", Icons.Filled.LocalGroceryStore, ListColors.LIME, IconCategory.MONEY),
    MONEY("Finanzen", Icons.Filled.Savings, ListColors.GREEN, IconCategory.MONEY),
    WALLET("Geldbörse", Icons.Filled.AccountBalanceWallet, ListColors.BROWN, IconCategory.MONEY),
    BANK("Bank", Icons.Filled.AccountBalance, ListColors.INDIGO, IconCategory.MONEY),
    CARD("Karte", Icons.Filled.CreditCard, ListColors.BLUE, IconCategory.MONEY),
    RECEIPT("Rechnungen", Icons.Filled.Receipt, ListColors.GRAY, IconCategory.MONEY),
    EURO("Euro", Icons.Filled.Euro, ListColors.GREEN, IconCategory.MONEY),
    SELL("Verkaufen", Icons.Filled.Sell, ListColors.ORANGE, IconCategory.MONEY),
    // Essen & Trinken
    FOOD("Essen", Icons.Filled.Restaurant, ListColors.ORANGE, IconCategory.FOOD),
    COFFEE("Café", Icons.Filled.LocalCafe, ListColors.BROWN, IconCategory.FOOD),
    WATER("Trinken", Icons.Filled.WaterDrop, ListColors.SKY, IconCategory.FOOD),
    BAKERY("Bäckerei", Icons.Filled.BakeryDining, ListColors.BROWN, IconCategory.FOOD),
    PIZZA("Pizza", Icons.Filled.LocalPizza, ListColors.RED, IconCategory.FOOD),
    FASTFOOD("Fast Food", Icons.Filled.Fastfood, ListColors.YELLOW, IconCategory.FOOD),
    RAMEN("Kochen", Icons.Filled.RamenDining, ListColors.ORANGE, IconCategory.FOOD),
    BRUNCH("Frühstück", Icons.Filled.BrunchDining, ListColors.YELLOW, IconCategory.FOOD),
    ICECREAM("Eis", Icons.Filled.Icecream, ListColors.PINK, IconCategory.FOOD),
    WINE("Wein", Icons.Filled.WineBar, ListColors.PURPLE, IconCategory.FOOD),
    BAR("Bar", Icons.Filled.LocalBar, ListColors.INDIGO, IconCategory.FOOD),
    // Gesundheit & Sport
    HEALTH("Gesundheit", Icons.Filled.LocalHospital, ListColors.RED, IconCategory.HEALTH),
    MEDICATION("Medikamente", Icons.Filled.Medication, ListColors.PINK, IconCategory.HEALTH),
    HEART("Herz & Kreislauf", Icons.Filled.MonitorHeart, ListColors.RED, IconCategory.HEALTH),
    MIND("Psyche", Icons.Filled.Psychology, ListColors.PURPLE, IconCategory.HEALTH),
    SLEEP("Schlafen", Icons.Filled.Bedtime, ListColors.INDIGO, IconCategory.HEALTH),
    WELLNESS("Wellness", Icons.Filled.Spa, ListColors.PURPLE, IconCategory.HEALTH),
    YOGA("Yoga", Icons.Filled.SelfImprovement, ListColors.TEAL, IconCategory.HEALTH),
    FITNESS("Sport", Icons.Filled.FitnessCenter, ListColors.RED, IconCategory.HEALTH),
    RUN("Laufen", Icons.Filled.DirectionsRun, ListColors.ORANGE, IconCategory.HEALTH),
    BIKE("Radfahren", Icons.AutoMirrored.Filled.DirectionsBike, ListColors.GREEN, IconCategory.HEALTH),
    SWIM("Schwimmen", Icons.Filled.Pool, ListColors.SKY, IconCategory.HEALTH),
    HIKING("Wandern", Icons.Filled.Hiking, ListColors.BROWN, IconCategory.HEALTH),
    SOCCER("Fußball", Icons.Filled.SportsSoccer, ListColors.GREEN, IconCategory.HEALTH),
    TENNIS("Tennis", Icons.Filled.SportsTennis, ListColors.LIME, IconCategory.HEALTH),
    BASKETBALL("Basketball", Icons.Filled.SportsBasketball, ListColors.ORANGE, IconCategory.HEALTH),
    // Lernen & Wissen
    SCHOOL("Lernen", Icons.Filled.School, ListColors.INDIGO, IconCategory.LEARN),
    BOOK("Lesen", Icons.AutoMirrored.Filled.MenuBook, ListColors.PURPLE, IconCategory.LEARN),
    NOTES("Notizen", Icons.Filled.EditNote, ListColors.YELLOW, IconCategory.LEARN),
    LANGUAGE("Sprachen", Icons.Filled.Translate, ListColors.INDIGO, IconCategory.LEARN),
    SCIENCE("Wissen", Icons.Filled.Science, ListColors.TEAL, IconCategory.LEARN),
    MATH("Mathe", Icons.Filled.Calculate, ListColors.BLUE, IconCategory.LEARN),
    HISTORY("Geschichte", Icons.Filled.HistoryEdu, ListColors.BROWN, IconCategory.LEARN),
    WORLD("Welt", Icons.Filled.Public, ListColors.SKY, IconCategory.LEARN),
    // Freizeit & Kultur
    MUSIC("Musik", Icons.Filled.MusicNote, ListColors.PURPLE, IconCategory.FUN),
    PIANO("Instrument", Icons.Filled.Piano, ListColors.GRAY, IconCategory.FUN),
    HEADPHONES("Podcasts", Icons.Filled.Headphones, ListColors.INDIGO, IconCategory.FUN),
    MOVIE("Filme", Icons.Filled.Movie, ListColors.RED, IconCategory.FUN),
    TV("Serien", Icons.Filled.Tv, ListColors.BLUE, IconCategory.FUN),
    THEATER("Theater", Icons.Filled.TheaterComedy, ListColors.PINK, IconCategory.FUN),
    GAMES("Spiele", Icons.Filled.SportsEsports, ListColors.INDIGO, IconCategory.FUN),
    PUZZLE("Rätsel", Icons.Filled.Extension, ListColors.TEAL, IconCategory.FUN),
    DICE("Brettspiele", Icons.Filled.Casino, ListColors.RED, IconCategory.FUN),
    PHOTO("Fotos", Icons.Filled.PhotoCamera, ListColors.GRAY, IconCategory.FUN),
    ART("Kreativ", Icons.Filled.Brush, ListColors.PINK, IconCategory.FUN),
    PALETTE("Malen", Icons.Filled.Palette, ListColors.ORANGE, IconCategory.FUN),
    // Reisen & Unterwegs
    TRAVEL("Reisen", Icons.Filled.Flight, ListColors.SKY, IconCategory.TRAVEL),
    VACATION("Urlaub", Icons.Filled.BeachAccess, ListColors.YELLOW, IconCategory.TRAVEL),
    LUGGAGE("Packliste", Icons.Filled.Luggage, ListColors.BROWN, IconCategory.TRAVEL),
    HOTEL("Hotel", Icons.Filled.Hotel, ListColors.INDIGO, IconCategory.TRAVEL),
    MAP("Ausflüge", Icons.Filled.Map, ListColors.GREEN, IconCategory.TRAVEL),
    PLACE("Orte", Icons.Filled.Place, ListColors.RED, IconCategory.TRAVEL),
    CAR("Auto", Icons.Filled.DirectionsCar, ListColors.GRAY, IconCategory.TRAVEL),
    FUEL("Tanken", Icons.Filled.LocalGasStation, ListColors.ORANGE, IconCategory.TRAVEL),
    EV("E-Auto", Icons.Filled.EvStation, ListColors.GREEN, IconCategory.TRAVEL),
    TRAIN("Bahn", Icons.Filled.Train, ListColors.RED, IconCategory.TRAVEL),
    BUS("Bus", Icons.Filled.DirectionsBus, ListColors.YELLOW, IconCategory.TRAVEL),
    BOAT("Boot", Icons.Filled.DirectionsBoat, ListColors.SKY, IconCategory.TRAVEL),
    // Natur & Tiere
    NATURE("Umwelt", Icons.Filled.Eco, ListColors.LIME, IconCategory.NATURE),
    PETS("Haustiere", Icons.Filled.Pets, ListColors.BROWN, IconCategory.NATURE),
    FLOWER("Blumen", Icons.Filled.LocalFlorist, ListColors.PINK, IconCategory.NATURE),
    PARK("Park", Icons.Filled.Park, ListColors.GREEN, IconCategory.NATURE),
    FOREST("Wald", Icons.Filled.Forest, ListColors.GREEN, IconCategory.NATURE),
    MOUNTAIN("Berge", Icons.Filled.Terrain, ListColors.BROWN, IconCategory.NATURE),
    FARM("Landwirtschaft", Icons.Filled.Agriculture, ListColors.LIME, IconCategory.NATURE),
    SUN("Wetter", Icons.Filled.WbSunny, ListColors.YELLOW, IconCategory.NATURE),
    // Technik
    SMARTPHONE("Handy", Icons.Filled.PhoneAndroid, ListColors.GRAY, IconCategory.TECH),
    WIFI("Internet", Icons.Filled.Wifi, ListColors.BLUE, IconCategory.TECH),
    CHIP("Hardware", Icons.Filled.Memory, ListColors.TEAL, IconCategory.TECH),
    SETTINGS("Einstellungen", Icons.Filled.Settings, ListColors.GRAY, IconCategory.TECH),
    BATTERY("Laden", Icons.Filled.BatteryChargingFull, ListColors.GREEN, IconCategory.TECH);

    companion object {
        fun of(name: String): ListIcon = entries.firstOrNull { it.name == name } ?: LIST

        val byCategory: Map<IconCategory, List<ListIcon>> = entries.groupBy { it.category }
    }
}
