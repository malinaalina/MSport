package m.alina.msport.util

fun initialsOf(name: String): String =
    name.split(" ")
        .filter { it.isNotBlank() }
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
        .joinToString("")
