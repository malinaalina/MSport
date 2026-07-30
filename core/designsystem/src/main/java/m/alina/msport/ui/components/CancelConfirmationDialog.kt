package m.alina.msport.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import m.alina.msport.designsystem.R
import m.alina.msport.ui.theme.Accent
import m.alina.msport.ui.theme.BloomBody
import m.alina.msport.ui.theme.BloomButton
import m.alina.msport.ui.theme.BloomModalTitle
import m.alina.msport.ui.theme.BloomRadius
import m.alina.msport.ui.theme.InputBorder
import m.alina.msport.ui.theme.Surface as BloomSurfaceColor
import m.alina.msport.ui.theme.TextMuted
import m.alina.msport.ui.theme.TextPrimary
import androidx.compose.ui.graphics.Color

@Composable
fun CancelConfirmationDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            color = BloomSurfaceColor,
            shape = RoundedCornerShape(BloomRadius),
            modifier = Modifier.fillMaxWidth(),
        ) {
            androidx.compose.foundation.layout.Column(modifier = Modifier.padding(24.dp)) {
                Text(text = stringResource(R.string.cancel_booking_question), style = BloomModalTitle, color = TextPrimary)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.cancel_booking_body),
                    style = BloomBody,
                    color = TextMuted,
                )
                Spacer(modifier = Modifier.height(22.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 13.dp),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, InputBorder),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                    ) {
                        Text(text = stringResource(R.string.no_action), style = BloomButton, maxLines = 1)
                    }
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 13.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Accent, contentColor = Color.White),
                    ) {
                        Text(
                            text = stringResource(R.string.yes_cancel_action),
                            style = BloomButton,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }
    }
}
