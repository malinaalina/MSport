package m.alina.msport.classdetail.ui

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import m.alina.msport.feature.workout.R
import m.alina.msport.domain.model.BookingStatus
import m.alina.msport.domain.model.WorkoutSession
import m.alina.msport.classdetail.ClassDetailAction
import m.alina.msport.classdetail.ClassDetailEffect
import m.alina.msport.classdetail.viewmodel.ClassDetailViewModel
import m.alina.msport.ui.components.CancelConfirmationDialog
import m.alina.msport.ui.theme.Accent
import m.alina.msport.ui.theme.AccentSoft
import m.alina.msport.ui.theme.BloomBackgroundGradient
import m.alina.msport.ui.theme.BloomBody
import m.alina.msport.ui.theme.BloomBodySmall
import m.alina.msport.ui.theme.BloomButton
import m.alina.msport.ui.theme.BloomDetailTitle
import m.alina.msport.ui.theme.BloomRadius
import m.alina.msport.ui.theme.Divider
import m.alina.msport.ui.theme.Surface as BloomSurfaceColor
import m.alina.msport.ui.theme.SurfaceAlt
import m.alina.msport.ui.theme.TextMuted
import m.alina.msport.ui.theme.TextPrimary
import m.alina.msport.ui.theme.Warning
import m.alina.msport.util.initialsOf

@Composable
fun ClassDetailScreen(
    workout: WorkoutSession,
    viewModel: ClassDetailViewModel,
    onBack: () -> Unit,
    onBooked: () -> Unit,
    onCancelled: () -> Unit,
) {
    val state by viewModel.uiState.collectAsState()
    var showCancelDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                ClassDetailEffect.Booked -> onBooked()
                ClassDetailEffect.Cancelled -> onCancelled()
            }
        }
    }

    if (showCancelDialog) {
        CancelConfirmationDialog(
            onDismiss = { showCancelDialog = false },
            onConfirm = {
                showCancelDialog = false
                viewModel.onAction(ClassDetailAction.Cancel(workout.id))
            },
        )
    }

    state.error?.let { error ->
        AlertDialog(
            onDismissRequest = { viewModel.onAction(ClassDetailAction.DismissError) },
            title = { Text(stringResource(R.string.error_title)) },
            text = { Text(error.message ?: stringResource(error.messageRes)) },
            confirmButton = {
                TextButton(onClick = { viewModel.onAction(ClassDetailAction.DismissError) }) {
                    Text(stringResource(R.string.ok_action))
                }
            },
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BloomBackgroundGradient)
            .verticalScroll(rememberScrollState()),
    ) {
        IconButton(onClick = onBack, modifier = Modifier.padding(start = 12.dp, top = 12.dp)) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back_description), tint = TextPrimary)
        }

        Box(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
                .height(160.dp)
                .background(
                    Brush.linearGradient(listOf(AccentSoft, SurfaceAlt)),
                    RoundedCornerShape(BloomRadius),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Surface(color = BloomSurfaceColor.copy(alpha = 0.85f), shape = RoundedCornerShape(8.dp)) {
                Text(
                    text = stringResource(R.string.class_photo_placeholder),
                    style = BloomBodySmall,
                    color = TextPrimary,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                )
            }
        }

        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = workout.title, style = BloomDetailTitle, color = TextPrimary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "${workout.date} · ${workout.time}", style = BloomBody, color = TextMuted)

            Spacer(modifier = Modifier.height(20.dp))
            Surface(color = BloomSurfaceColor, shape = RoundedCornerShape(BloomRadius)) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier.size(44.dp).background(AccentSoft, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = initialsOf(workout.instructor),
                            style = BloomBody.copy(fontWeight = BloomDetailTitle.fontWeight),
                            color = Accent,
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = workout.instructor,
                            style = BloomBody.copy(fontWeight = BloomDetailTitle.fontWeight, fontSize = 15.sp),
                            color = TextPrimary,
                        )
                        Text(text = stringResource(R.string.trainer_role_and_hall, workout.room), style = BloomBodySmall, color = TextMuted)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text(text = workout.description, style = BloomBody.copy(lineHeight = 22.sp), color = TextPrimary)

            Spacer(modifier = Modifier.height(20.dp))
            val isFull = workout.taken >= workout.capacity
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = stringResource(R.string.occupied_seats_label), style = BloomBodySmall, color = TextMuted)
                Text(
                    text = stringResource(R.string.capacity_progress, workout.taken, workout.capacity),
                    style = BloomBodySmall,
                    color = TextMuted,
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .background(Divider, RoundedCornerShape(4.dp)),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth((workout.taken.toFloat() / workout.capacity).coerceIn(0f, 1f))
                        .fillMaxSize()
                        .background(if (isFull) Warning else Accent, RoundedCornerShape(4.dp)),
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            when (workout.bookingStatus) {
                BookingStatus.NONE -> Button(
                    onClick = { viewModel.onAction(ClassDetailAction.Book(workout.id)) },
                    enabled = !state.isBooking,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(BloomRadius),
                    contentPadding = PaddingValues(vertical = 17.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Accent, contentColor = Color.White),
                ) {
                    if (state.isBooking) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Text(
                            text = stringResource(if (isFull) R.string.action_join_waitlist else R.string.action_book),
                            style = BloomButton,
                        )
                    }
                }

                BookingStatus.BOOKED -> OutlinedButton(
                    onClick = { showCancelDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(BloomRadius),
                    contentPadding = PaddingValues(vertical = 17.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Accent),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Accent),
                ) {
                    Text(text = stringResource(R.string.action_cancel_booking), style = BloomButton)
                }

                BookingStatus.WAITLISTED -> OutlinedButton(
                    onClick = { showCancelDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(BloomRadius),
                    contentPadding = PaddingValues(vertical = 17.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Accent),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Accent),
                ) {
                    Text(text = stringResource(R.string.action_leave_waitlist), style = BloomButton)
                }
            }
        }
    }
}
