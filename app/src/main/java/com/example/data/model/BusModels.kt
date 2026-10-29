package com.example.data.model

enum class BusOperatorType(val displayName: String, val badgeColorHex: Long) {
  GOVERNMENT("TNSTC / Government", 0xFF0D47A1),
  PRIVATE("Private Operator", 0xFFE65100)
}

enum class BusType(val displayName: String, val badgeColorHex: Long) {
  ORDINARY("Town Ordinary", 0xFF0D47A1),
  EXPRESS("Express / Fast Track", 0xFFE65100),
  VAJRA_AC("AC Low Floor Electric", 0xFF00897B),
  METRO_FEEDER("Suburban Feeder", 0xFF6A1B9A)
}

enum class OccupancyStatus(val label: String, val colorHex: Long) {
  SEATS_AVAILABLE("Seats Available", 0xFF2E7D32),
  STANDING_ROOM("Standing Room", 0xFFEF6C00),
  CROWDED("Heavy Rush / Packed", 0xFFC62828)
}

enum class TrafficSeverity(val label: String, val delayMin: Int, val colorHex: Long) {
  NORMAL("Clear Flow", 0, 0xFF2E7D32),
  MODERATE("Slow Moving", 6, 0xFFEF6C00),
  HEAVY("Heavy Bottleneck", 14, 0xFFC62828)
}

data class BusStopSchedule(
  val stopId: String,
  val stopName: String,
  val stopSequence: Int,
  val scheduledArrival: String,
  val scheduledDeparture: String,
  val platformBay: String,
  val distanceFromOriginKm: Double,
  val latitude: Double,
  val longitude: Double,
  val trafficCondition: TrafficSeverity = TrafficSeverity.NORMAL,
  val delayMinutes: Int = 0,
  val isMajorJunction: Boolean = false
)

data class BusRoute(
  val id: String,
  val routeNumber: String,
  val routeName: String,
  val city: String,
  val agency: String,
  val operatorType: BusOperatorType = BusOperatorType.GOVERNMENT,
  val operatorName: String = "TNSTC Coimbatore",
  val isWomenFreeTravel: Boolean = false,
  val permitNumber: String = "TN-38/GOVT-2024",
  val origin: String,
  val destination: String,
  val busType: BusType,
  val totalDistanceKm: Double,
  val averageDurationMin: Int,
  val operatingHours: String,
  val peakFrequencyMin: Int,
  val offPeakFrequencyMin: Int,
  val stops: List<BusStopSchedule>,
  val activeTrafficAlert: String? = null,
  val isDelayed: Boolean = false,
  val currentDelayMin: Int = 0,
  val totalStopsCount: Int = stops.size,
  val stopsDescription: String = "${stops.size} Total Stops"
)

data class LiveBus(
  val busId: String,
  val routeNumber: String,
  val vehicleNumber: String,
  val busType: BusType,
  val operatorType: BusOperatorType = BusOperatorType.GOVERNMENT,
  val operatorName: String = "TNSTC Coimbatore",
  val isWomenFreeTravel: Boolean = false,
  val latitude: Double,
  val longitude: Double,
  val headingDeg: Float,
  val speedKmph: Int,
  val nextStopId: String,
  val nextStopName: String,
  val etaNextStopMin: Int,
  val occupancy: OccupancyStatus,
  val progressPercent: Float,
  val currentStopIndex: Int,
  val totalStopsCount: Int,
  val remainingStopsCount: Int,
  val delayMinutes: Int = 0
)

data class TrafficAlert(
  val id: String,
  val city: String,
  val routeNumber: String,
  val locationName: String,
  val title: String,
  val description: String,
  val delayMinutes: Int,
  val severity: TrafficSeverity,
  val reportedTimeAgo: String
)

data class BusTripDeparture(
  val tripId: String,
  val routeNumber: String,
  val vehicleNumber: String,
  val departureTime: String,
  val expectedArrivalAtDest: String,
  val busType: BusType,
  val operatorType: BusOperatorType = BusOperatorType.GOVERNMENT,
  val operatorName: String = "TNSTC",
  val isWomenFree: Boolean = false,
  val status: String,
  val delayMin: Int,
  val occupancy: OccupancyStatus
)

data class TimeSlotSchedule(
  val slotId: String,
  val slotTitle: String,
  val timeRange: String,
  val totalBusesCount: Int,
  val governmentBusesCount: Int = (totalBusesCount * 0.6).toInt().coerceAtLeast(1),
  val privateBusesCount: Int = (totalBusesCount * 0.4).toInt().coerceAtLeast(1),
  val frequencySummary: String,
  val busTrips: List<BusTripDeparture>,
  val activeBusesCount: Int = 0,
  val seatsAvailableCount: Int = 0,
  val averageHeadwayMin: Int = 10
)

data class FareEstimate(
  val fromStop: String,
  val toStop: String,
  val distanceKm: Double,
  val stopsTraversed: Int,
  val isGovernmentBus: Boolean = true,
  val isWomenFreeEligible: Boolean = false,
  val governmentOrdinaryFare: Int,
  val privateOrdinaryFare: Int,
  val expressFare: Int,
  val acVajraFare: Int,
  val studentConcessionFare: Int,
  val seniorCitizenFare: Int,
  val dailyPassPrice: Int,
  val approxTravelTimeMin: Int
)

data class PushNotificationEvent(
  val id: String,
  val title: String,
  val message: String,
  val timestamp: String,
  val type: String // "ARRIVAL", "DELAY", "DEPARTURE"
)
