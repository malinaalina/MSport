package m.alina.msport.pinsetup.ui

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
import androidx.compose.foundation.layout.imePadding
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import m.alina.msport.feature.auth.R
import m.alina.msport.pinsetup.PIN_LENGTH
import m.alina.msport.pinsetup.PinSetupAction
import m.alina.msport.pinsetup.PinSetupEffect
import m.alina.msport.pinsetup.viewmodel.PinSetupViewModel
import m.alina.msport.ui.theme.Accent
import m.alina.msport.ui.theme.BloomBackgroundGradient
import m.alina.msport.ui.theme.BloomButton
import m.alina.msport.ui.theme.BloomRadius
import m.alina.msport.ui.theme.BloomSectionHeading
import m.alina.msport.ui.theme.InputBorder
import m.alina.msport.ui.theme.Surface
import m.alina.msport.ui.theme.TextPrimary

@Composable
fun PinSetupScreen(
    viewModel: PinSetupViewModel,
    onAuthorized: (accessToken: String) -> Unit,
) {
    val state by viewModel.uiState.collectAsState()
    val titleText = stringResource(if (state.isReentry) R.string.pin_login_heading else R.string.pin_setup_heading)
    val buttonText = stringResource(if (state.isReentry) R.string.login_action else R.string.confirm_action)
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is PinSetupEffect.Authorized -> onAuthorized(effect.accessToken)
            }
        }
    }

    if (state.error != null) {
        val error = state.error!!
        AlertDialog(
            onDismissRequest = { viewModel.onAction(PinSetupAction.DismissError) },
            title = { Text(stringResource(R.string.error_title)) },
            text = { Text(error.message ?: stringResource(error.messageRes)) },
            confirmButton = {
                TextButton(onClick = { viewModel.onAction(PinSetupAction.DismissError) }) { Text(stringResource(R.string.ok_action)) }
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
            .imePadding()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = titleText,
            style = BloomSectionHeading,
            color = TextPrimary,
            modifier = Modifier.padding(bottom = 32.dp),
        )

        BasicTextField(
            value = state.pin,
            onValueChange = { viewModel.onAction(PinSetupAction.PinChanged(it)) },
            modifier = Modifier.focusRequester(focusRequester),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            decorationBox = { innerTextField ->
                Box(contentAlignment = Alignment.Center) {
                    Box(modifier = Modifier.size(1.dp).alpha(0f)) {
                        innerTextField()
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        repeat(PIN_LENGTH) { index ->
                            val isFilled = index < state.pin.length
                            val isFocused = index == state.pin.length

                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .border(
                                        1.5.dp,
                                        if (isFocused) Accent else InputBorder,
                                        RoundedCornerShape(12.dp),
                                    )
                                    .background(Surface, RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center,
                            ) {
                                if (isFilled) {
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .background(color = TextPrimary, shape = RoundedCornerShape(50)),
                                    )
                                }
                            }
                        }
                    }
                }
            },
        )

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = { viewModel.onAction(PinSetupAction.Submit) },
            enabled = state.pin.length == PIN_LENGTH,
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
            Text(text = buttonText, style = BloomButton)
        }
    }
}
