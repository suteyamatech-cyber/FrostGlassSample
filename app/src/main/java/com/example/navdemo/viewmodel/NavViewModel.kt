package com.example.navdemo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.navdemo.data.FakeRouteData
import com.example.navdemo.data.model.NavState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Owns navigation state and drives the fake-route demo animation.
 *
 * Architecture: MVVM – exposes [navState] as a [StateFlow] consumed by the UI;
 * all mutation happens inside [viewModelScope] coroutines.
 */
class NavViewModel : ViewModel() {

    private val _navState = MutableStateFlow(
        NavState(
            latitude     = FakeRouteData.route.first().latitude,
            longitude    = FakeRouteData.route.first().longitude,
            bearing      = FakeRouteData.route.first().bearing,
            speedKmh     = 0,
            nextInstruction = FakeRouteData.route.first().instruction
        )
    )
    val navState: StateFlow<NavState> = _navState.asStateFlow()

    private var demoJob: Job? = null

    /** Starts (or restarts) the fake-route demo playback. */
    fun startDemo() {
        demoJob?.cancel()
        demoJob = viewModelScope.launch {
            val route = FakeRouteData.route
            _navState.update { it.copy(isDemoRunning = true) }

            for (index in route.indices) {
                val point = route[index]
                val remaining = route.size - index - 1
                val etaMin = remaining * 45 / 60    // rough estimate

                _navState.update {
                    it.copy(
                        routeIndex      = index,
                        latitude        = point.latitude,
                        longitude       = point.longitude,
                        bearing         = point.bearing,
                        speedKmh        = point.speed,
                        nextInstruction = point.instruction,
                        distanceToNext  = remaining * 180,
                        eta             = buildEta(etaMin)
                    )
                }
                delay(STEP_DELAY_MS)
            }

            // Route finished
            _navState.update {
                it.copy(isDemoRunning = false, speedKmh = 0)
            }
        }
    }

    /** Pauses / resumes the demo. */
    fun toggleDemo() {
        if (_navState.value.isDemoRunning) {
            demoJob?.cancel()
            _navState.update { it.copy(isDemoRunning = false, speedKmh = 0) }
        } else {
            startDemo()
        }
    }

    override fun onCleared() {
        super.onCleared()
        demoJob?.cancel()
    }

    private fun buildEta(minutesFromNow: Int): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.MINUTE, minutesFromNow)
        return SimpleDateFormat("HH:mm", Locale.getDefault()).format(cal.time)
    }

    companion object {
        /** Milliseconds between route-point advances; controls animation speed. */
        private const val STEP_DELAY_MS = 3_000L
    }
}
