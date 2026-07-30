package m.alina.msport.phone.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import m.alina.msport.designsystem.R as designsystemR
import m.alina.msport.feature.auth.R
import m.alina.msport.phone.PhoneLoginAction
import m.alina.msport.phone.PhoneLoginEffect
import m.alina.msport.phone.viewmodel.PhoneLoginViewModel
import m.alina.msport.ui.theme.Accent
import m.alina.msport.ui.theme.BloomAppTitle
import m.alina.msport.ui.theme.BloomBody
import m.alina.msport.ui.theme.BloomButton
import m.alina.msport.ui.theme.BloomRadius
import m.alina.msport.ui.theme.BloomSectionHeading
import m.alina.msport.ui.theme.BloomBackgroundGradient
import m.alina.msport.ui.theme.InputBorder
import m.alina.msport.ui.theme.Surface
import m.alina.msport.ui.theme.TextMuted
import m.alina.msport.ui.theme.TextPrimary
import m.alina.msport.util.PHONE_MAX_DIGITS

@Composable
fun PhoneLoginScreen(
    viewModel: PhoneLoginViewModel,
    onSmsSent: (sessionId: String, retryDelay: Int, phone: String) -> Unit,
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is PhoneLoginEffect.NavigateToSmsCode ->
                    onSmsSent(effect.sessionId, effect.retryDelaySeconds, effect.phone)
            }
        }
    }

    if (state.error != null) {
        val error = state.error!!
        AlertDialog(
            onDismissRequest = { viewModel.onAction(PhoneLoginAction.DismissError) },
            title = { Text(stringResource(R.string.error_title)) },
            text = { Text(error.message ?: stringResource(error.messageRes)) },
            confirmButton = {
                TextButton(onClick = { viewModel.onAction(PhoneLoginAction.DismissError) }) { Text(stringResource(R.string.ok_action)) }
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
        Text(text = stringResource(designsystemR.string.app_name), style = BloomAppTitle, color = TextPrimary)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.app_tagline),
            style = BloomBody,
            color = TextMuted,
        )
        Spacer(modifier = Modifier.height(48.dp))

        Text(text = stringResource(R.string.phone_entry_heading), style = BloomSectionHeading, color = TextPrimary)
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(R.string.phone_label),
            style = BloomBody.copy(fontSize = 12.sp, letterSpacing = 0.3.sp),
            color = TextMuted,
        )
        Spacer(modifier = Modifier.height(8.dp))

        BasicTextField(
            value = state.phoneDigits,
            onValueChange = { input -> viewModel.onAction(PhoneLoginAction.PhoneDigitsChanged(input)) },
            textStyle = BloomBody.copy(fontSize = 17.sp, color = TextPrimary),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine = true,
            visualTransformation = PhoneNumberVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.5.dp, InputBorder, RoundedCornerShape(BloomRadius))
                        .background(Surface, RoundedCornerShape(BloomRadius))
                        .padding(horizontal = 18.dp, vertical = 16.dp),
                ) {
                    if (state.phoneDigits.isEmpty()) {
                        Text(text = stringResource(R.string.phone_placeholder), style = BloomBody.copy(fontSize = 17.sp), color = TextMuted)
                    }
                    innerTextField()
                }
            },
        )
        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = { viewModel.onAction(PhoneLoginAction.SendSms) },
            enabled = state.phoneDigits.length == PHONE_MAX_DIGITS,
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
            Text(text = stringResource(R.string.send_code_action), style = BloomButton)
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = stringResource(R.string.legal_notice),
            style = BloomBody.copy(fontSize = 12.sp, lineHeight = 19.sp),
            color = TextMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
        )
    }
}
