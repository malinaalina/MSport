package m.alina.msport.smscode.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import m.alina.msport.feature.auth.R
import m.alina.msport.smscode.SMS_CODE_LENGTH
import m.alina.msport.smscode.SmsCodeAction
import m.alina.msport.smscode.SmsCodeEffect
import m.alina.msport.smscode.viewmodel.SmsCodeViewModel
import m.alina.msport.ui.theme.Accent
import m.alina.msport.ui.theme.BloomBody
import m.alina.msport.ui.theme.BloomButton
import m.alina.msport.ui.theme.BloomBackgroundGradient
import m.alina.msport.ui.theme.BloomCode
import m.alina.msport.ui.theme.BloomLink
import m.alina.msport.ui.theme.BloomRadius
import m.alina.msport.ui.theme.BloomSectionHeading
import m.alina.msport.ui.theme.Divider
import m.alina.msport.ui.theme.InputBorder
import m.alina.msport.ui.theme.Surface
import m.alina.msport.ui.theme.TextMuted
import m.alina.msport.ui.theme.TextPrimary
import m.alina.msport.util.formatRussianPhone

@Composable
fun SmsCodeScreen(
    viewModel: SmsCodeViewModel,
    onBack: () -> Unit,
    onVerified: (interimToken: String, isNewUser: Boolean) -> Unit,
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is SmsCodeEffect.NavigateToPinSetup -> onVerified(effect.interimToken, effect.isNewUser)
            }
        }
    }

    if (state.error != null) {
        val error = state.error!!
        AlertDialog(
            onDismissRequest = { viewModel.onAction(SmsCodeAction.DismissError) },
            title = { Text(stringResource(R.string.error_title)) },
            text = { Text(error.message ?: stringResource(error.messageRes)) },
            confirmButton = {
                TextButton(onClick = { viewModel.onAction(SmsCodeAction.DismissError) }) { Text(stringResource(R.string.ok_action)) }
            },
        )
    }

    if (state.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BloomBackgroundGradient)
            .padding(horizontal = 24.dp)
            .padding(top = 28.dp, bottom = 28.dp),
    ) {
        TextButton(onClick = onBack) {
            Text(text = stringResource(R.string.back_link), style = BloomBody, color = TextMuted)
        }

        Text(text = stringResource(R.string.sms_code_heading), style = BloomSectionHeading, color = TextPrimary)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = stringResource(R.string.sms_code_sent_to, formatRussianPhone(state.phone)), style = BloomBody, color = TextMuted)
        Spacer(modifier = Modifier.height(36.dp))

        BasicTextField(
            value = state.code,
            onValueChange = { viewModel.onAction(SmsCodeAction.CodeChanged(it)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            decorationBox = { innerTextField ->
                Box {
                    Box(modifier = Modifier.size(1.dp).alpha(0f)) {
                        innerTextField()
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        repeat(SMS_CODE_LENGTH) { index ->
                            val filled = index < state.code.length
                            Box(
                                modifier = Modifier
                                    .size(width = 56.dp, height = 64.dp)
                                    .border(1.5.dp, if (filled) Accent else InputBorder, RoundedCornerShape(12.dp))
                                    .background(Surface, RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = if (filled) state.code[index].toString() else "",
                                    style = BloomCode,
                                    color = TextPrimary,
                                )
                            }
                        }
                    }
                }
            },
        )
        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .background(Divider, RoundedCornerShape(3.dp)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(state.code.length / SMS_CODE_LENGTH.toFloat())
                    .fillMaxSize()
                    .background(Accent, RoundedCornerShape(3.dp)),
            )
        }
        Spacer(modifier = Modifier.height(28.dp))

        TextButton(onClick = { viewModel.onAction(SmsCodeAction.ResendSms) }) {
            Text(text = stringResource(R.string.resend_code_action), style = BloomLink, color = Accent)
        }
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.onAction(SmsCodeAction.VerifyCode) },
            enabled = state.code.length == SMS_CODE_LENGTH,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(BloomRadius),
            contentPadding = PaddingValues(vertical = 17.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Accent,
                contentColor = Color.White,
                disabledContainerColor = Accent.copy(alpha = 0.4f),
                disabledContentColor = Color.White,
            ),
        ) {
            Text(text = stringResource(R.string.confirm_action), style = BloomButton)
        }
    }
}
