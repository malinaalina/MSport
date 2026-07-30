package m.alina.msport.myclasses.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import m.alina.msport.feature.workout.R
import m.alina.msport.domain.model.WorkoutSession
import m.alina.msport.myclasses.MyClassesAction
import m.alina.msport.myclasses.MyClassesSubTab
import m.alina.msport.myclasses.viewmodel.MyClassesViewModel
import m.alina.msport.ui.components.CancelConfirmationDialog
import m.alina.msport.ui.components.ClassCard
import m.alina.msport.ui.components.PillChip
import m.alina.msport.ui.theme.Accent
import m.alina.msport.ui.theme.BloomBackgroundGradient
import m.alina.msport.ui.theme.BloomBody
import m.alina.msport.ui.theme.BloomLink
import m.alina.msport.ui.theme.BloomScreenTitle
import m.alina.msport.ui.theme.TextMuted
import m.alina.msport.ui.theme.TextPrimary

@Composable
fun MyClassesScreen(
    viewModel: MyClassesViewModel,
    onWorkoutClick: (WorkoutSession) -> Unit,
) {
    val state by viewModel.uiState.collectAsState()

    var pendingCancelId by remember { mutableStateOf<String?>(null) }
    pendingCancelId?.let { id ->
        CancelConfirmationDialog(
            onDismiss = { pendingCancelId = null },
            onConfirm = {
                pendingCancelId = null
                viewModel.onAction(MyClassesAction.Cancel(id))
            },
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BloomBackgroundGradient)
            .statusBarsPadding(),
    ) {
        Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 8.dp)) {
            Text(text = stringResource(R.string.my_classes_title), style = BloomScreenTitle, color = TextPrimary)
        }

        Row(
            modifier = Modifier.padding(horizontal = 22.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            PillChip(
                label = stringResource(R.string.tab_upcoming),
                isSelected = state.subTab == MyClassesSubTab.UPCOMING,
                onClick = { viewModel.onAction(MyClassesAction.SelectUpcoming) },
            )
            PillChip(
                label = stringResource(R.string.tab_past),
                isSelected = state.subTab == MyClassesSubTab.PAST,
                onClick = { viewModel.onAction(MyClassesAction.SelectPast) },
            )
        }

        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = { viewModel.onAction(MyClassesAction.RefreshAll) },
            modifier = Modifier.fillMaxSize(),
        ) {
            when (state.subTab) {
                MyClassesSubTab.UPCOMING -> {
                    if (state.upcoming.isEmpty()) {
                        Text(
                            text = stringResource(R.string.empty_upcoming_classes),
                            style = BloomBody,
                            color = TextMuted,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth().padding(60.dp),
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(start = 22.dp, end = 22.dp, top = 8.dp, bottom = 78.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            items(state.upcoming) { workout ->
                                ClassCard(
                                    workout = workout,
                                    onClick = { onWorkoutClick(workout) },
                                    trailing = { CancelLink { pendingCancelId = workout.id } },
                                )
                            }
                        }
                    }
                }

                MyClassesSubTab.PAST -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 22.dp, end = 22.dp, top = 8.dp, bottom = 78.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(state.past) { workout ->
                            ClassCard(workout = workout, muted = true, modifier = Modifier.alpha(0.75f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RowScope.CancelLink(onClick: () -> Unit) {
    TextButton(onClick = onClick) {
        Text(text = stringResource(R.string.cancel_link_label), style = BloomLink.copy(fontSize = 12.sp), color = Accent)
    }
}
