package com.bk.trafficcontrol.ui.schedule

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.bk.trafficcontrol.domain.model.Track
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    navController: NavController,
    playlistId: Long? = null,
    viewModel: ScheduleViewModel = hiltViewModel()
) {
    // Initialize the viewModel with the playlistId if provided
    LaunchedEffect(playlistId) {
        if (playlistId != null) {
            viewModel.setPlaylistId(playlistId)
        }
    }
    
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = uiState.playlistName.ifEmpty { "Schedule" },
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    if (playlistId != null) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (uiState.showFab) {
                FloatingActionButton(
                    onClick = { 
                        viewModel.addSampleTrack()
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Sample track added")
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Track")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Schedule Mode Selector (Uniform / Per-day)
            ScheduleModeSelector(
                mode = uiState.mode,
                onModeChange = viewModel::onModeChange,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                when (uiState.mode) {
                    ScheduleMode.UNIFORM -> {
                        UniformScheduleView(
                            tracks = uiState.uniformTracks,
                            onTrackToggle = { trackId, enabled ->
                                viewModel.onTrackToggle(trackId, enabled)
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(
                                        if (enabled) "Track scheduled" else "Track unscheduled"
                                    )
                                }
                            },
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                    ScheduleMode.PER_DAY -> {
                        PerDayScheduleView(
                            days = uiState.days,
                            selectedDay = uiState.selectedDay,
                            onDaySelected = viewModel::onDaySelected,
                            onTrackToggle = { trackId, enabled ->
                                viewModel.onTrackToggle(trackId, enabled)
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(
                                        if (enabled) "Track scheduled for ${getDayName(uiState.selectedDay)}" 
                                        else "Track unscheduled for ${getDayName(uiState.selectedDay)}"
                                    )
                                }
                            },
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleModeSelector(
    mode: ScheduleMode,
    onModeChange: (ScheduleMode) -> Unit,
    modifier: Modifier = Modifier
) {
    SingleChoiceSegmentedButtonRow(
        modifier = modifier.fillMaxWidth()
    ) {
        SegmentedButton(
            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
            onClick = { onModeChange(ScheduleMode.UNIFORM) },
            selected = mode == ScheduleMode.UNIFORM
        ) {
            Text(
                text = "Uniform",
                style = MaterialTheme.typography.titleMedium
            )
        }
        SegmentedButton(
            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
            onClick = { onModeChange(ScheduleMode.PER_DAY) },
            selected = mode == ScheduleMode.PER_DAY
        ) {
            Text(
                text = "Per-day",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun UniformScheduleView(
    tracks: List<TrackItem>,
    onTrackToggle: (Long, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            Text(
                text = "Same schedule every day",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        
        items(tracks) { track ->
            TrackScheduleCard(
                trackItem = track,
                onToggle = { enabled -> onTrackToggle(track.id, enabled) }
            )
        }
        
        // Add spacing for FAB
        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerDayScheduleView(
    days: List<DaySchedule>,
    selectedDay: Int,
    onDaySelected: (Int) -> Unit,
    onTrackToggle: (Long, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Weekday tab selector
        ScrollableTabRow(
            selectedTabIndex = selectedDay - 1,
            modifier = Modifier.fillMaxWidth(),
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ) {
            val dayNames = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
            dayNames.forEachIndexed { index, dayName ->
                Tab(
                    selected = selectedDay == index + 1,
                    onClick = { onDaySelected(index + 1) },
                    text = {
                        Text(
                            text = dayName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = if (selectedDay == index + 1) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Track list for selected day
        val selectedDaySchedule = days.find { it.weekday == selectedDay }
        if (selectedDaySchedule != null) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                item {
                    Text(
                        text = "Schedule for ${getDayName(selectedDay)}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                
                items(selectedDaySchedule.tracks) { track ->
                    TrackScheduleCard(
                        trackItem = track,
                        onToggle = { enabled -> onTrackToggle(track.id, enabled) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackScheduleCard(
    trackItem: TrackItem,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (trackItem.enabled) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = trackItem.time.format(DateTimeFormatter.ofPattern("HH:mm")),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (trackItem.enabled) 
                        MaterialTheme.colorScheme.onPrimaryContainer 
                    else 
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = trackItem.title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (trackItem.enabled) 
                        MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    else 
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }
            
            Switch(
                checked = trackItem.enabled,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                )
            )
        }
    }
}

private fun getDayName(dayOfWeek: Int): String {
    return when (dayOfWeek) {
        1 -> "Mon"
        2 -> "Tue"
        3 -> "Wed"
        4 -> "Thu"
        5 -> "Fri"
        6 -> "Sat"
        7 -> "Sun"
        else -> "?"
    }
}
