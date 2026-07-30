package m.alina.msport.phone.ui

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import m.alina.msport.util.formatRussianPhone

private const val MASK_PREFIX_LENGTH = 3

class PhoneNumberVisualTransformation : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text.filter { it.isDigit() }
        if (digits.isEmpty()) {
            return TransformedText(AnnotatedString(""), OffsetMapping.Identity)
        }

        val formatted = formatRussianPhone(digits)

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                val target = offset.coerceIn(0, digits.length)
                if (target == 0) return MASK_PREFIX_LENGTH
                var seen = 0
                for (index in MASK_PREFIX_LENGTH until formatted.length) {
                    if (formatted[index].isDigit()) {
                        seen++
                        if (seen == target) return index + 1
                    }
                }
                return formatted.length
            }

            override fun transformedToOriginal(offset: Int): Int {
                val clamped = offset.coerceIn(0, formatted.length)
                if (clamped <= MASK_PREFIX_LENGTH) return 0
                return formatted.substring(MASK_PREFIX_LENGTH, clamped).count { it.isDigit() }
            }
        }

        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }
}
