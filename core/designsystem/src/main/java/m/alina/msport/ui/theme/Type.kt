package m.alina.msport.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import m.alina.msport.designsystem.R

@OptIn(ExperimentalTextApi::class)
private fun variableFont(resId: Int, weight: Int) = Font(
    resId = resId,
    weight = FontWeight(weight),
    variationSettings = FontVariation.Settings(FontVariation.weight(weight)),
)

val Sora = FontFamily(
    variableFont(R.font.sora_variable, 600),
    variableFont(R.font.sora_variable, 700),
)

val NunitoSans = FontFamily(
    variableFont(R.font.nunitosans_variable, 400),
    variableFont(R.font.nunitosans_variable, 600),
    variableFont(R.font.nunitosans_variable, 700),
    variableFont(R.font.nunitosans_variable, 800),
)

val BloomAppTitle = TextStyle(fontFamily = Sora, fontWeight = FontWeight.W600, fontSize = 30.sp)
val BloomScreenTitle = TextStyle(fontFamily = Sora, fontWeight = FontWeight.W600, fontSize = 26.sp)
val BloomSectionHeading = TextStyle(fontFamily = Sora, fontWeight = FontWeight.W600, fontSize = 22.sp)
val BloomDetailTitle = TextStyle(fontFamily = Sora, fontWeight = FontWeight.W700, fontSize = 24.sp)
val BloomModalTitle = TextStyle(fontFamily = Sora, fontWeight = FontWeight.W700, fontSize = 18.sp)
val BloomCardTitle = TextStyle(fontFamily = Sora, fontWeight = FontWeight.W700, fontSize = 16.sp)

val BloomBody = TextStyle(fontFamily = NunitoSans, fontWeight = FontWeight.W400, fontSize = 14.sp)
val BloomBodyLarge = TextStyle(fontFamily = NunitoSans, fontWeight = FontWeight.W400, fontSize = 15.sp)
val BloomBodySmall = TextStyle(fontFamily = NunitoSans, fontWeight = FontWeight.W400, fontSize = 13.sp)
val BloomLabel = TextStyle(fontFamily = NunitoSans, fontWeight = FontWeight.W700, fontSize = 12.sp)
val BloomBadge = TextStyle(fontFamily = NunitoSans, fontWeight = FontWeight.W700, fontSize = 11.sp)
val BloomButton = TextStyle(fontFamily = NunitoSans, fontWeight = FontWeight.W700, fontSize = 16.sp)
val BloomLink = TextStyle(fontFamily = NunitoSans, fontWeight = FontWeight.W700, fontSize = 14.sp)
val BloomCode = TextStyle(fontFamily = NunitoSans, fontWeight = FontWeight.W600, fontSize = 24.sp)

val Typography = Typography(
    displaySmall = BloomAppTitle,
    headlineSmall = BloomScreenTitle,
    titleLarge = BloomSectionHeading,
    titleMedium = BloomCardTitle,
    bodyLarge = BloomBodyLarge,
    bodyMedium = BloomBody,
    bodySmall = BloomBodySmall,
    labelLarge = BloomButton,
    labelMedium = BloomLabel,
    labelSmall = BloomBadge,
)
