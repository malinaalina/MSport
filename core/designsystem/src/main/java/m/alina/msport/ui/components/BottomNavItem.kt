package m.alina.msport.ui.components

data class BottomNavItem(
    val label: String,
    val selected: Boolean,
    val onClick: () -> Unit,
)
