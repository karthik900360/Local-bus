package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.BusOperatorType
import com.example.data.repository.TransitRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Yatri Bus", appName)
  }

  @Test
  fun `transit repository loads coimbatore government and private routes`() {
    val repository = TransitRepository()
    assertTrue(repository.allRoutes.isNotEmpty())

    // Coimbatore Route 11A (TNSTC Government Ordinary - Free for Women)
    val route11A = repository.allRoutes.firstOrNull { it.routeNumber == "11A" }
    assertNotNull(route11A)
    assertEquals(BusOperatorType.GOVERNMENT, route11A?.operatorType)
    assertTrue(route11A?.isWomenFreeTravel == true)
    assertEquals(8, route11A?.stops?.size)

    // Coimbatore Route 70A (Private Bus Operator - Sri Krishna / Cheran)
    val route70A = repository.allRoutes.firstOrNull { it.routeNumber == "70A" }
    assertNotNull(route70A)
    assertEquals(BusOperatorType.PRIVATE, route70A?.operatorType)
    assertEquals(11, route70A?.stops?.size)

    // Verify stage fare calculation between Gandhipuram and Ukkadam
    val fare = repository.calculateTicketFare(route11A!!.stops.first(), route11A.stops.last())
    assertTrue(fare.distanceKm > 0.0)
    assertTrue(fare.stopsTraversed == 7)
    assertTrue(fare.governmentOrdinaryFare in 5..20)
    assertTrue(fare.privateOrdinaryFare >= fare.governmentOrdinaryFare)
    assertTrue(fare.expressFare >= fare.privateOrdinaryFare)
    assertTrue(fare.isWomenFreeEligible)
  }

  @Test
  fun `transit repository returns time slot frequency analysis with govt and private breakdown`() {
    val repository = TransitRepository()
    val route = repository.allRoutes.first()
    val slots = repository.getTimeSlotSchedules(route)
    assertTrue(slots.isNotEmpty())
    val morningPeak = slots.first { it.slotId == "slot_morning_peak" }
    assertTrue(morningPeak.totalBusesCount > 0)
    assertTrue(morningPeak.governmentBusesCount > 0)
    assertTrue(morningPeak.privateBusesCount > 0)
    assertTrue(morningPeak.busTrips.isNotEmpty())
  }

  @Test
  fun `transit repository calculates available buses for any route and time slot`() {
    val repository = TransitRepository()
    for (route in repository.allRoutes) {
      val schedules = repository.getTimeSlotSchedules(route)
      assertTrue("Route ${route.routeNumber} should have time slots", schedules.size >= 4)

      for (slot in schedules) {
        assertTrue("Slot ${slot.slotId} on ${route.routeNumber} must have positive bus count", slot.totalBusesCount > 0)
        assertTrue("Active buses must be non-negative", slot.activeBusesCount >= 0)
        assertTrue("Seats available count must be non-negative", slot.seatsAvailableCount >= 0)
        assertTrue("Headway frequency must be positive", slot.averageHeadwayMin > 0)
        assertTrue("Bus trips list must not be empty", slot.busTrips.isNotEmpty())
      }
    }
  }
}
