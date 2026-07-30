package m.alina.msport.schedule.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import m.alina.msport.feature.workout.R
import m.alina.msport.domain.model.WorkoutSession
import m.alina.msport.schedule.ScheduleAction
import m.alina.msport.schedule.viewmodel.ScheduleViewModel
import m.alina.msport.ui.components.ClassCard
import m.alina.msport.ui.components.PillChip
import m.alina.msport.ui.theme.Accent
import m.alina.msport.ui.theme.BloomBackgroundGradient
import m.alina.msport.ui.theme.BloomBadge
import m.alina.msport.ui.theme.BloomScreenTitle
import m.alina.msport.ui.theme.Surface
import m.alina.msport.ui.theme.TextMuted
import m.alina.msport.ui.theme.TextPrimary
import m.alina.msport.util.russianDayAbbrev
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun ScheduleScreen(
    viewModel: ScheduleViewModel,
    onWorkoutClick: (WorkoutSession) -> Unit,
) {
    val state by viewModel.uiState.collectAsState()

    val dayNumberFormat = remember { SimpleDateFormat("d", Locale("ru")) }
    val allTypesLabel = stringResource(R.string.filter_all)
    val classTypes = stringArrayResource(R.array.class_types)
    val filters = remember(classTypes) { listOf<String?>(null) + classTypes }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BloomBackgroundGradient)
            .statusBarsPadding(),
    ) {
        Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 8.dp)) {
            Text(text = stringResource(R.string.schedule_title), style = BloomScreenTitle, color = TextPrimary)
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = { viewModel.onAction(ScheduleAction.PreviousWeek) }) {
                Icon(
                    Icons.Default.KeyboardArrowLeft,
                    contentDescription = stringResource(R.string.previous_week_description),
                    tint = TextMuted,
                )
            }
            Text(text = state.currentMonth, style = BloomBadge.copy(fontSize = 14.sp), color = TextMuted)
            IconButton(onClick = { viewModel.onAction(ScheduleAction.NextWeek) }) {
                Icon(
                    Icons.Default.KeyboardArrowRight,
                    contentDescription = stringResource(R.string.next_week_description),
                    tint = TextMuted,
                )
            }
        }

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 22.dp, vertical = 6.dp),
        ) {
            items(state.visibleDates) { date ->
                DayChip(
                    date = date,
                    dayNumberFormat = dayNumberFormat,
                    isSelected = isSameDay(date, state.selectedDate),
                    onClick = { viewModel.onAction(ScheduleAction.SelectDate(date)) },
                )
            }
        }

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 22.dp, vertical = 6.dp),
        ) {
            items(filters) { filter ->
                PillChip(
                    label = filter ?: allTypesLabel,
                    isSelected = filter == state.activeTypeFilter,
                    onClick = { viewModel.onAction(ScheduleAction.SetFilter(filter)) },
                )
            }
        }

        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = { viewModel.onAction(ScheduleAction.Refresh) },
            modifier = Modifier.fillMaxSize(),
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 22.dp, end = 22.dp, top = 8.dp, bottom = 78.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(state.classes) { workout ->
                    ClassCard(workout = workout, onClick = { onWorkoutClick(workout) })
                }
            }
        }
    }
}

@Composable
private fun DayChip(
    date: Date,
    dayNumberFormat: SimpleDateFormat,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val calendar = remember(date) { Calendar.getInstance().apply { time = date } }
    val background = if (isSelected) Accent else Surface
    val contentColor = if (isSelected) Color.White else TextPrimary

    Column(
        modifier = Modifier
            .widthIn(min = 56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(background)
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = russianDayAbbrev(calendar.get(Calendar.DAY_OF_WEEK)),
            style = BloomBadge.copy(fontSize = 11.sp),
            color = contentColor.copy(alpha = 0.75f),
        )
        Text(
            text = dayNumberFormat.format(date),
            style = BloomBadge.copy(fontSize = 18.sp),
            color = contentColor,
            modifier = Modifier.padding(top = 2.dp),
        )
    }
}

private fun isSameDay(date1: Date, date2: Date): Boolean {
    val cal1 = Calendar.getInstance().apply { time = date1 }
    val cal2 = Calendar.getInstance().apply { time = date2 }
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
        cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}
