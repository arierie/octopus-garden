package work.arie.octopusgarden.core

import com.google.firebase.Firebase
import com.google.firebase.perf.performance
import javax.inject.Inject

internal class PerformanceMonitor @Inject constructor() {
    private val isTraceSent = hashMapOf<String, Boolean>()

    private val coldStart = Firebase.performance.newTrace(COLD_START)
    private val timeToFirstToken = Firebase.performance.newTrace(TIME_TO_FIRST_TOKEN)

    fun startColdStartTrace() {
        if (isTraceSent[COLD_START] == true) {
            return
        }
        coldStart.start()
    }

    fun stopColdStartTrace() {
        if (isTraceSent[COLD_START] == true) {
            return
        }
        coldStart.stop()
        isTraceSent[COLD_START] = true
    }

    fun startFirstTokenTrace() {
        if (isTraceSent[TIME_TO_FIRST_TOKEN] == true) {
            return
        }
        timeToFirstToken.start()
    }

    fun stopFirstTokenTrace() {
        if (isTraceSent[TIME_TO_FIRST_TOKEN] == true) {
            return
        }
        timeToFirstToken.stop()
        isTraceSent[TIME_TO_FIRST_TOKEN] = true
    }

    private companion object {

        const val TIME_TO_FIRST_TOKEN = "ttft_trace"
        const val COLD_START = "cold_start_trace"
    }
}
