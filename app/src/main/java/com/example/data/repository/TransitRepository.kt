package com.example.data.repository

import com.example.data.model.BusOperatorType
import com.example.data.model.BusRoute
import com.example.data.model.BusStopSchedule
import com.example.data.model.BusTripDeparture
import com.example.data.model.BusType
import com.example.data.model.FareEstimate
import com.example.data.model.LiveBus
import com.example.data.model.OccupancyStatus
import com.example.data.model.TimeSlotSchedule
import com.example.data.model.TrafficAlert
import com.example.data.model.TrafficSeverity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

class TransitRepository {

  val cities = listOf(
    "Coimbatore (TNSTC & Private)",
    "Bengaluru (BMTC)",
    "Chennai (MTC)",
    "Mumbai (BEST)",
    "Delhi (DTC)"
  )

  // ===================== COIMBATORE ROUTES =====================

  // Route 11A: Gandhipuram <-> Ukkadam (Government TNSTC Ordinary - Free for Women)
  private val route11ACbeStops = listOf(
    BusStopSchedule("CBE_11A_1", "Gandhipuram Town Bus Stand", 1, "08:00 AM", "08:02 AM", "Bay 4 (Town Stand)", 0.0, 11.0183, 76.9669, TrafficSeverity.NORMAL, 0, isMajorJunction = true),
    BusStopSchedule("CBE_11A_2", "Cross Cut Road Signal", 2, "08:07 AM", "08:08 AM", "Bay 1", 1.2, 11.0142, 76.9660, TrafficSeverity.MODERATE, 3),
    BusStopSchedule("CBE_11A_3", "Lakshmi Mills Signal", 3, "08:14 AM", "08:16 AM", "Bay 2", 2.6, 11.0118, 76.9805, TrafficSeverity.HEAVY, 7, isMajorJunction = true),
    BusStopSchedule("CBE_11A_4", "Coimbatore Jn Railway Stn", 4, "08:24 AM", "08:26 AM", "Main Portico Bay", 4.5, 11.0016, 76.9668, TrafficSeverity.MODERATE, 4, isMajorJunction = true),
    BusStopSchedule("CBE_11A_5", "District Collectorate", 5, "08:31 AM", "08:32 AM", "Bay 1", 5.4, 10.9995, 76.9675, TrafficSeverity.NORMAL, 0),
    BusStopSchedule("CBE_11A_6", "Town Hall / Manikoondu", 6, "08:37 AM", "08:39 AM", "Platform 3", 6.3, 10.9950, 76.9630, TrafficSeverity.HEAVY, 8, isMajorJunction = true),
    BusStopSchedule("CBE_11A_7", "Oppanakara Street Market", 7, "08:44 AM", "08:45 AM", "Bay 2", 7.1, 10.9930, 76.9610, TrafficSeverity.MODERATE, 3),
    BusStopSchedule("CBE_11A_8", "Ukkadam Bus Stand", 8, "08:52 AM", "Terminus", "Platform 6", 8.4, 10.9852, 76.9573, TrafficSeverity.NORMAL, 0, isMajorJunction = true)
  )

  // Route 70A: Gandhipuram <-> Marudhamalai Temple (Private Operator - Sri Krishna / Cheran)
  private val route70ACbeStops = listOf(
    BusStopSchedule("CBE_70A_1", "Gandhipuram Town Stand", 1, "08:10 AM", "08:12 AM", "Bay 12 (Private Bay)", 0.0, 11.0183, 76.9669, TrafficSeverity.NORMAL, 0, isMajorJunction = true),
    BusStopSchedule("CBE_70A_2", "100 Feet Road / GP Signal", 2, "08:18 AM", "08:20 AM", "Bay 2", 1.8, 11.0210, 76.9610, TrafficSeverity.MODERATE, 4),
    BusStopSchedule("CBE_70A_3", "Poo Market (Flower Market)", 3, "08:26 AM", "08:27 AM", "Bay 1", 3.2, 11.0120, 76.9540, TrafficSeverity.HEAVY, 6),
    BusStopSchedule("CBE_70A_4", "Saibaba Colony Signal", 4, "08:34 AM", "08:36 AM", "Bay 3", 4.9, 11.0260, 76.9500, TrafficSeverity.NORMAL, 0, isMajorJunction = true),
    BusStopSchedule("CBE_70A_5", "Lawley Road (TNAU)", 5, "08:43 AM", "08:45 AM", "Agri Gate Bay", 7.2, 11.0135, 76.9360, TrafficSeverity.NORMAL, 0, isMajorJunction = true),
    BusStopSchedule("CBE_70A_6", "Bharathi Park / Forest College", 6, "08:51 AM", "08:52 AM", "Bay 1", 8.8, 11.0170, 76.9280, TrafficSeverity.NORMAL, 0),
    BusStopSchedule("CBE_70A_7", "Vadavalli Bus Stand", 7, "09:00 AM", "09:02 AM", "Bay 2", 11.5, 11.0265, 76.9015, TrafficSeverity.NORMAL, 0, isMajorJunction = true),
    BusStopSchedule("CBE_70A_8", "Mullai Nagar", 8, "09:08 AM", "09:09 AM", "Bay 1", 13.2, 11.0330, 76.8850, TrafficSeverity.NORMAL, 0),
    BusStopSchedule("CBE_70A_9", "IOB Colony / Kalveerampalayam", 9, "09:14 AM", "09:15 AM", "Bay 2", 14.8, 11.0385, 76.8720, TrafficSeverity.NORMAL, 0),
    BusStopSchedule("CBE_70A_10", "Marudhamalai Adivaram (Foothills)", 10, "09:22 AM", "09:24 AM", "Temple Arch", 17.0, 11.0450, 76.8520, TrafficSeverity.NORMAL, 0, isMajorJunction = true),
    BusStopSchedule("CBE_70A_11", "Marudhamalai Murugan Temple Hilltop", 11, "09:32 AM", "Terminus", "Hilltop Terminus", 18.5, 11.0465, 76.8505, TrafficSeverity.NORMAL, 0, isMajorJunction = true)
  )

  // Route 20A: Ukkadam <-> Saravanampatti IT SEZ (Private Operator - Hindusthan Fast Track)
  private val route20ACbeStops = listOf(
    BusStopSchedule("CBE_20A_1", "Ukkadam Bus Stand", 1, "08:15 AM", "08:18 AM", "Bay 1 (Private)", 0.0, 10.9852, 76.9573, TrafficSeverity.NORMAL, 0, isMajorJunction = true),
    BusStopSchedule("CBE_20A_2", "Town Hall Clock Tower", 2, "08:24 AM", "08:25 AM", "Bay 2", 1.8, 10.9950, 76.9630, TrafficSeverity.HEAVY, 7),
    BusStopSchedule("CBE_20A_3", "Coimbatore Jn Railway Stn", 3, "08:32 AM", "08:34 AM", "Station Circle", 3.2, 11.0016, 76.9668, TrafficSeverity.MODERATE, 4, isMajorJunction = true),
    BusStopSchedule("CBE_20A_4", "Gandhipuram Central Bus Stand", 4, "08:44 AM", "08:47 AM", "Bay 9", 5.6, 11.0183, 76.9669, TrafficSeverity.HEAVY, 9, isMajorJunction = true),
    BusStopSchedule("CBE_20A_5", "Ramakrishna Mandapam", 5, "08:54 AM", "08:55 AM", "Bay 1", 7.4, 11.0310, 76.9740, TrafficSeverity.NORMAL, 0),
    BusStopSchedule("CBE_20A_6", "Ganapathy Bus Stand", 6, "09:02 AM", "09:04 AM", "Bay 3", 9.1, 11.0410, 76.9810, TrafficSeverity.MODERATE, 5, isMajorJunction = true),
    BusStopSchedule("CBE_20A_7", "Sivanandhapuram Junction", 7, "09:12 AM", "09:14 AM", "Bay 2", 11.8, 11.0620, 76.9920, TrafficSeverity.NORMAL, 0),
    BusStopSchedule("CBE_20A_8", "Saravanampatti Checkpost", 8, "09:22 AM", "09:24 AM", "Bay 4", 14.5, 11.0790, 76.9995, TrafficSeverity.HEAVY, 8, isMajorJunction = true),
    BusStopSchedule("CBE_20A_9", "KGISL Campus / Keeranatham IT SEZ", 9, "09:34 AM", "Terminus", "SEZ Main Gate", 17.2, 11.0965, 77.0090, TrafficSeverity.NORMAL, 0, isMajorJunction = true)
  )

  // Route 33A: Gandhipuram <-> Coimbatore Airport (CJB) (Government TNSTC Express / AC Low Floor)
  private val route33ACbeStops = listOf(
    BusStopSchedule("CBE_33A_1", "Gandhipuram Central Bus Stand", 1, "08:05 AM", "08:10 AM", "Bay 1 (FlyBus)", 0.0, 11.0183, 76.9669, TrafficSeverity.NORMAL, 0, isMajorJunction = true),
    BusStopSchedule("CBE_33A_2", "Women's Polytechnic College", 2, "08:16 AM", "08:17 AM", "Bay 1", 1.6, 11.0150, 76.9730, TrafficSeverity.NORMAL, 0),
    BusStopSchedule("CBE_33A_3", "Lakshmi Mills Avinashi Road", 3, "08:24 AM", "08:26 AM", "Bay 2", 3.0, 11.0118, 76.9805, TrafficSeverity.HEAVY, 8, isMajorJunction = true),
    BusStopSchedule("CBE_33A_4", "Nava India Junction", 4, "08:33 AM", "08:34 AM", "Bay 1", 4.8, 11.0135, 76.9910, TrafficSeverity.MODERATE, 4),
    BusStopSchedule("CBE_33A_5", "Peelamedu PSG Tech / Fun Republic", 5, "08:42 AM", "08:44 AM", "Bay 3", 6.8, 11.0230, 77.0035, TrafficSeverity.NORMAL, 0, isMajorJunction = true),
    BusStopSchedule("CBE_33A_6", "Hopes College Bus Stand", 6, "08:51 AM", "08:53 AM", "Bay 2", 8.9, 11.0285, 77.0175, TrafficSeverity.MODERATE, 5, isMajorJunction = true),
    BusStopSchedule("CBE_33A_7", "Coimbatore Medical College (CMC)", 7, "08:58 AM", "08:59 AM", "Bay 1", 10.4, 11.0315, 77.0250, TrafficSeverity.NORMAL, 0),
    BusStopSchedule("CBE_33A_8", "SITRA Junction", 8, "09:05 AM", "09:07 AM", "Bay 4", 12.1, 11.0350, 77.0345, TrafficSeverity.NORMAL, 0, isMajorJunction = true),
    BusStopSchedule("CBE_33A_9", "Coimbatore Airport Terminal (CJB)", 9, "09:15 AM", "Terminus", "Departure Gate 1", 13.8, 11.0305, 77.0435, TrafficSeverity.NORMAL, 0, isMajorJunction = true)
  )

  // Route 1C: Gandhipuram <-> Ondipudur (Government TNSTC Ordinary - Free for Women)
  private val route1CCbeStops = listOf(
    BusStopSchedule("CBE_1C_1", "Gandhipuram Central Bus Stand", 1, "08:20 AM", "08:22 AM", "Bay 5", 0.0, 11.0183, 76.9669, TrafficSeverity.NORMAL, 0, isMajorJunction = true),
    BusStopSchedule("CBE_1C_2", "Lakshmi Mills Signal", 2, "08:29 AM", "08:31 AM", "Bay 2", 2.8, 11.0118, 76.9805, TrafficSeverity.HEAVY, 7),
    BusStopSchedule("CBE_1C_3", "Ramanathapuram 80 Feet Road", 3, "08:38 AM", "08:40 AM", "Bay 1", 5.2, 11.0020, 76.9910, TrafficSeverity.MODERATE, 4),
    BusStopSchedule("CBE_1C_4", "Singanallur Bus Stand", 4, "08:49 AM", "08:52 AM", "Platform 4", 8.7, 11.0012, 77.0234, TrafficSeverity.HEAVY, 9, isMajorJunction = true),
    BusStopSchedule("CBE_1C_5", "Ondipudur Flyover Depot", 5, "09:02 AM", "Terminus", "Depot Bay 1", 11.5, 11.0040, 77.0510, TrafficSeverity.NORMAL, 0, isMajorJunction = true)
  )

  // Route 45B: Singanallur <-> Vadavalli (Private Operator - Vetrivel City Cruiser)
  private val route45BCbeStops = listOf(
    BusStopSchedule("CBE_45B_1", "Singanallur Bus Stand", 1, "08:00 AM", "08:03 AM", "Bay 2 (Private)", 0.0, 11.0012, 77.0234, TrafficSeverity.NORMAL, 0, isMajorJunction = true),
    BusStopSchedule("CBE_45B_2", "Ramanathapuram", 2, "08:12 AM", "08:14 AM", "Bay 1", 3.4, 11.0020, 76.9910, TrafficSeverity.MODERATE, 4),
    BusStopSchedule("CBE_45B_3", "Lakshmi Mills Signal", 3, "08:22 AM", "08:24 AM", "Bay 2", 5.8, 11.0118, 76.9805, TrafficSeverity.HEAVY, 7),
    BusStopSchedule("CBE_45B_4", "Gandhipuram Town Stand", 4, "08:34 AM", "08:38 AM", "Bay 11", 8.6, 11.0183, 76.9669, TrafficSeverity.HEAVY, 8, isMajorJunction = true),
    BusStopSchedule("CBE_45B_5", "100 Feet Road Signal", 5, "08:44 AM", "08:46 AM", "Bay 1", 10.4, 11.0210, 76.9610, TrafficSeverity.NORMAL, 0),
    BusStopSchedule("CBE_45B_6", "Saibaba Colony Junction", 6, "08:53 AM", "08:55 AM", "Bay 2", 12.8, 11.0260, 76.9500, TrafficSeverity.MODERATE, 3, isMajorJunction = true),
    BusStopSchedule("CBE_45B_7", "Lawley Road (TNAU)", 7, "09:03 AM", "09:05 AM", "Agri Gate", 14.9, 11.0135, 76.9360, TrafficSeverity.NORMAL, 0),
    BusStopSchedule("CBE_45B_8", "Forest College", 8, "09:11 AM", "09:12 AM", "Bay 1", 16.5, 11.0170, 76.9280, TrafficSeverity.NORMAL, 0),
    BusStopSchedule("CBE_45B_9", "Vadavalli Bus Stand", 9, "09:22 AM", "Terminus", "Platform 1", 19.2, 11.0265, 76.9015, TrafficSeverity.NORMAL, 0, isMajorJunction = true)
  )

  // ===================== BENGALURU & OTHER METRO ROUTES =====================
  private val route500DStops = listOf(
    BusStopSchedule("S1", "Silk Board Junction", 1, "08:00 AM", "08:02 AM", "Bay 1", 0.0, 12.9176, 77.6233, TrafficSeverity.HEAVY, 12, isMajorJunction = true),
    BusStopSchedule("S2", "HSR BDA Complex", 2, "08:09 AM", "08:11 AM", "Platform 2", 2.4, 12.9116, 77.6388, TrafficSeverity.MODERATE, 5),
    BusStopSchedule("S3", "Agara Lake Junction", 3, "08:17 AM", "08:19 AM", "Bay 4", 4.8, 12.9248, 77.6505, TrafficSeverity.NORMAL, 0),
    BusStopSchedule("S4", "Bellandur Gate", 4, "08:26 AM", "08:28 AM", "Bay 2", 7.6, 12.9304, 77.6784, TrafficSeverity.HEAVY, 8, isMajorJunction = true),
    BusStopSchedule("S5", "Ecospace Tech Park", 5, "08:37 AM", "08:39 AM", "Bay 1", 9.8, 12.9260, 77.6842, TrafficSeverity.MODERATE, 4),
    BusStopSchedule("S6", "Kadubeesanahalli", 6, "08:45 AM", "08:46 AM", "Bay 3", 12.1, 12.9352, 77.6946, TrafficSeverity.NORMAL, 0),
    BusStopSchedule("S7", "Marathahalli Bridge", 7, "08:54 AM", "08:56 AM", "Bay 5", 14.9, 12.9569, 77.7011, TrafficSeverity.HEAVY, 10, isMajorJunction = true),
    BusStopSchedule("S8", "Karthik Nagar", 8, "09:05 AM", "09:06 AM", "Bay 2", 17.5, 12.9723, 77.7001, TrafficSeverity.NORMAL, 0),
    BusStopSchedule("S9", "Mahadevapura Ring Road", 9, "09:12 AM", "09:14 AM", "Bay 1", 20.2, 12.9880, 77.6975, TrafficSeverity.NORMAL, 0),
    BusStopSchedule("S10", "KR Puram Railway Stn", 10, "09:22 AM", "09:24 AM", "Platform 1", 23.0, 13.0012, 77.6953, TrafficSeverity.MODERATE, 6, isMajorJunction = true),
    BusStopSchedule("S11", "Tin Factory Junction", 11, "09:32 AM", "09:34 AM", "Bay 2", 24.8, 13.0041, 77.6719, TrafficSeverity.HEAVY, 9),
    BusStopSchedule("S12", "Kasturi Nagar", 12, "09:41 AM", "09:42 AM", "Bay 3", 26.9, 13.0118, 77.6531, TrafficSeverity.NORMAL, 0),
    BusStopSchedule("S13", "Manyata Tech Park Gate 1", 13, "09:50 AM", "09:52 AM", "Bay 4", 29.5, 13.0458, 77.6206, TrafficSeverity.NORMAL, 0, isMajorJunction = true),
    BusStopSchedule("S14", "Hebbal Flyover Depot", 14, "10:00 AM", "Terminus", "Platform 6", 32.0, 13.0358, 77.5970, TrafficSeverity.NORMAL, 0, isMajorJunction = true)
  )

  private val route335EStops = listOf(
    BusStopSchedule("K1", "Majestic Kempegowda BS", 1, "08:15 AM", "08:20 AM", "Platform 19", 0.0, 12.9767, 77.5713, TrafficSeverity.MODERATE, 4, isMajorJunction = true),
    BusStopSchedule("K2", "Corporation Circle", 2, "08:27 AM", "08:29 AM", "Bay 2", 2.5, 12.9654, 77.5878, TrafficSeverity.NORMAL, 0),
    BusStopSchedule("K3", "Richmond Circle", 3, "08:35 AM", "08:37 AM", "Bay 1", 4.3, 12.9678, 77.5991, TrafficSeverity.NORMAL, 0),
    BusStopSchedule("K4", "Mayo Hall MG Road", 4, "08:44 AM", "08:46 AM", "Bay 3", 6.2, 12.9734, 77.6095, TrafficSeverity.MODERATE, 5),
    BusStopSchedule("K5", "Domlur Flyover", 5, "08:55 AM", "08:57 AM", "Bay 2", 9.5, 12.9609, 77.6387, TrafficSeverity.HEAVY, 8),
    BusStopSchedule("K6", "HAL Main Gate", 6, "09:07 AM", "09:09 AM", "Bay 1", 13.0, 12.9562, 77.6685, TrafficSeverity.NORMAL, 0),
    BusStopSchedule("K7", "Marathahalli Bridge", 7, "09:20 AM", "09:22 AM", "Bay 4", 17.2, 12.9569, 77.7011, TrafficSeverity.HEAVY, 12, isMajorJunction = true),
    BusStopSchedule("K8", "Kundalahalli Gate", 8, "09:30 AM", "09:32 AM", "Bay 2", 19.8, 12.9689, 77.7145, TrafficSeverity.NORMAL, 0),
    BusStopSchedule("K9", "Graphite India Junction", 9, "09:40 AM", "09:42 AM", "Bay 1", 22.4, 12.9785, 77.7198, TrafficSeverity.NORMAL, 0),
    BusStopSchedule("K10", "ITPL Whitefield Main Gate", 10, "09:50 AM", "09:53 AM", "Platform 3", 24.5, 12.9863, 77.7346, TrafficSeverity.NORMAL, 0, isMajorJunction = true),
    BusStopSchedule("K11", "Kadugodi Bus Station", 11, "10:02 AM", "Terminus", "Platform 1", 27.0, 12.9984, 77.7601, TrafficSeverity.NORMAL, 0, isMajorJunction = true)
  )

  private val route23CChennaiStops = listOf(
    BusStopSchedule("C1", "Besant Nagar Beach Terminus", 1, "08:00 AM", "08:05 AM", "Bay 1", 0.0, 13.0002, 80.2668, TrafficSeverity.NORMAL, 0, isMajorJunction = true),
    BusStopSchedule("C2", "Adyar Signal / Depot", 2, "08:14 AM", "08:16 AM", "Bay 2", 2.8, 13.0064, 80.2575, TrafficSeverity.NORMAL, 0),
    BusStopSchedule("C3", "Kotturpuram Bridge", 3, "08:24 AM", "08:26 AM", "Bay 1", 5.1, 13.0189, 80.2442, TrafficSeverity.MODERATE, 5),
    BusStopSchedule("C4", "Nandanam Signal", 4, "08:35 AM", "08:37 AM", "Bay 3", 7.8, 13.0312, 80.2376, TrafficSeverity.HEAVY, 9, isMajorJunction = true),
    BusStopSchedule("C5", "T.Nagar Bus Stand", 5, "08:48 AM", "08:51 AM", "Platform 5", 9.9, 13.0418, 80.2337, TrafficSeverity.HEAVY, 12, isMajorJunction = true),
    BusStopSchedule("C6", "Panagal Park", 6, "08:58 AM", "09:00 AM", "Bay 2", 11.2, 13.0405, 80.2301, TrafficSeverity.NORMAL, 0),
    BusStopSchedule("C7", "Sterling Road Signal", 7, "09:12 AM", "09:14 AM", "Bay 1", 13.8, 13.0645, 80.2415, TrafficSeverity.MODERATE, 4),
    BusStopSchedule("C8", "Ayanavaram Bus Terminus", 8, "09:28 AM", "Terminus", "Platform 2", 17.2, 13.0978, 80.2324, TrafficSeverity.NORMAL, 0, isMajorJunction = true)
  )

  private val routeA340Stops = listOf(
    BusStopSchedule("M1", "Bandra Bus Station (W)", 1, "08:30 AM", "08:33 AM", "Bay 3", 0.0, 19.0544, 72.8402, TrafficSeverity.NORMAL, 0, isMajorJunction = true),
    BusStopSchedule("M2", "Linking Road Khar", 2, "08:42 AM", "08:44 AM", "Bay 1", 2.2, 19.0683, 72.8361, TrafficSeverity.MODERATE, 5),
    BusStopSchedule("M3", "Santacruz Station (W)", 3, "08:52 AM", "08:54 AM", "Bay 2", 4.5, 19.0815, 72.8398, TrafficSeverity.NORMAL, 0),
    BusStopSchedule("M4", "Milan Subway Junction", 4, "09:03 AM", "09:05 AM", "Bay 1", 6.8, 19.0886, 72.8447, TrafficSeverity.HEAVY, 9),
    BusStopSchedule("M5", "Vile Parle Station (E)", 5, "09:14 AM", "09:16 AM", "Bay 4", 9.1, 19.0988, 72.8524, TrafficSeverity.NORMAL, 0),
    BusStopSchedule("M6", "Western Express Hwy Metro", 6, "09:25 AM", "09:27 AM", "Bay 2", 11.5, 19.1158, 72.8569, TrafficSeverity.MODERATE, 6, isMajorJunction = true),
    BusStopSchedule("M7", "Andheri Station (E) Depot", 7, "09:36 AM", "Terminus", "Bay 5", 14.2, 19.1197, 72.8468, TrafficSeverity.NORMAL, 0, isMajorJunction = true)
  )

  private val route522DelhiStops = listOf(
    BusStopSchedule("D1", "Shivaji Stadium (CP)", 1, "08:10 AM", "08:15 AM", "Bay 2", 0.0, 28.6315, 77.2167, TrafficSeverity.NORMAL, 0, isMajorJunction = true),
    BusStopSchedule("D2", "Patel Chowk Metro", 2, "08:22 AM", "08:24 AM", "Bay 1", 2.1, 28.6231, 77.2144, TrafficSeverity.NORMAL, 0),
    BusStopSchedule("D3", "India Gate National Stadium", 3, "08:33 AM", "08:35 AM", "Bay 3", 4.8, 28.6129, 77.2295, TrafficSeverity.NORMAL, 0, isMajorJunction = true),
    BusStopSchedule("D4", "Lodhi Garden / INA Market", 4, "08:45 AM", "08:47 AM", "Bay 2", 8.2, 28.5828, 77.2132, TrafficSeverity.MODERATE, 4),
    BusStopSchedule("D5", "AIIMS Hospital Gate 1", 5, "08:56 AM", "08:58 AM", "Bay 1", 10.5, 28.5672, 77.2100, TrafficSeverity.HEAVY, 11, isMajorJunction = true),
    BusStopSchedule("D6", "Green Park Main", 6, "09:07 AM", "09:09 AM", "Bay 3", 12.3, 28.5589, 77.2064, TrafficSeverity.NORMAL, 0),
    BusStopSchedule("D7", "Hauz Khas Metro Station", 7, "09:17 AM", "09:19 AM", "Bay 2", 14.6, 28.5432, 77.2065, TrafficSeverity.NORMAL, 0),
    BusStopSchedule("D8", "Saket Terminal / Select City", 8, "09:30 AM", "Terminus", "Platform 4", 17.8, 28.5244, 77.2178, TrafficSeverity.NORMAL, 0, isMajorJunction = true)
  )

  val allRoutes: List<BusRoute> = listOf(
    // === COIMBATORE 1: TNSTC Route 11A ===
    BusRoute(
      id = "cbe_tnstc_11a",
      routeNumber = "11A",
      routeName = "Gandhipuram <-> Ukkadam Bus Stand",
      city = "Coimbatore (TNSTC & Private)",
      agency = "TNSTC Coimbatore Division",
      operatorType = BusOperatorType.GOVERNMENT,
      operatorName = "TNSTC Govt Town Service",
      isWomenFreeTravel = true,
      permitNumber = "TN-38/GOVT-TOWN-11A",
      origin = "Gandhipuram Town Bus Stand",
      destination = "Ukkadam Bus Stand",
      busType = BusType.ORDINARY,
      totalDistanceKm = 8.4,
      averageDurationMin = 35,
      operatingHours = "05:00 AM - 11:30 PM",
      peakFrequencyMin = 5,
      offPeakFrequencyMin = 10,
      stops = route11ACbeStops,
      activeTrafficAlert = "Town Hall Manikoondu traffic congestion (+8 min delay)",
      isDelayed = true,
      currentDelayMin = 8,
      totalStopsCount = route11ACbeStops.size,
      stopsDescription = "${route11ACbeStops.size} Scheduled Stops (All Town Halts)"
    ),

    // === COIMBATORE 2: Private Bus Route 70A ===
    BusRoute(
      id = "cbe_pvt_70a",
      routeNumber = "70A",
      routeName = "Gandhipuram <-> Marudhamalai Murugan Temple",
      city = "Coimbatore (TNSTC & Private)",
      agency = "Coimbatore Private Bus Association",
      operatorType = BusOperatorType.PRIVATE,
      operatorName = "Sri Krishna / Cheran City Bus",
      isWomenFreeTravel = false,
      permitNumber = "TN-38/PVT-1994-70A",
      origin = "Gandhipuram Town Stand",
      destination = "Marudhamalai Temple Hilltop",
      busType = BusType.ORDINARY,
      totalDistanceKm = 18.5,
      averageDurationMin = 55,
      operatingHours = "05:15 AM - 11:15 PM",
      peakFrequencyMin = 6,
      offPeakFrequencyMin = 12,
      stops = route70ACbeStops,
      activeTrafficAlert = "Flower Market / Poo Market peak rush (+6 min delay)",
      isDelayed = true,
      currentDelayMin = 6,
      totalStopsCount = route70ACbeStops.size,
      stopsDescription = "${route70ACbeStops.size} Stops (Punctual Private Express)"
    ),

    // === COIMBATORE 3: Private Bus Route 20A ===
    BusRoute(
      id = "cbe_pvt_20a",
      routeNumber = "20A",
      routeName = "Ukkadam <-> Saravanampatti IT SEZ",
      city = "Coimbatore (TNSTC & Private)",
      agency = "Coimbatore Private City Trans",
      operatorType = BusOperatorType.PRIVATE,
      operatorName = "Hindusthan Fast Track Cruiser",
      isWomenFreeTravel = false,
      permitNumber = "TN-38/PVT-2008-20A",
      origin = "Ukkadam Bus Stand",
      destination = "KGISL Campus / Keeranatham IT SEZ",
      busType = BusType.EXPRESS,
      totalDistanceKm = 17.2,
      averageDurationMin = 50,
      operatingHours = "05:30 AM - 11:45 PM",
      peakFrequencyMin = 6,
      offPeakFrequencyMin = 12,
      stops = route20ACbeStops,
      activeTrafficAlert = "Saravanampatti checkpost peak IT hours bottleneck (+8 min delay)",
      isDelayed = true,
      currentDelayMin = 8,
      totalStopsCount = route20ACbeStops.size,
      stopsDescription = "${route20ACbeStops.size} Major Stops (Direct IT Corridor)"
    ),

    // === COIMBATORE 4: Government TNSTC Route 33A ===
    BusRoute(
      id = "cbe_tnstc_33a",
      routeNumber = "33A",
      routeName = "Gandhipuram <-> Coimbatore Airport (CJB)",
      city = "Coimbatore (TNSTC & Private)",
      agency = "TNSTC Coimbatore FlyBus",
      operatorType = BusOperatorType.GOVERNMENT,
      operatorName = "TNSTC Airport Deluxe / Low Floor",
      isWomenFreeTravel = false,
      permitNumber = "TN-38/GOVT-EXP-33A",
      origin = "Gandhipuram Central Bus Stand",
      destination = "Coimbatore Airport Terminal (CJB)",
      busType = BusType.VAJRA_AC,
      totalDistanceKm = 13.8,
      averageDurationMin = 40,
      operatingHours = "05:00 AM - 11:30 PM",
      peakFrequencyMin = 12,
      offPeakFrequencyMin = 20,
      stops = route33ACbeStops,
      activeTrafficAlert = "Avinashi Road Elevated Flyover work near Lakshmi Mills (+8 min delay)",
      isDelayed = true,
      currentDelayMin = 8,
      totalStopsCount = route33ACbeStops.size,
      stopsDescription = "${route33ACbeStops.size} Stops (Airport Express Corridor)"
    ),

    // === COIMBATORE 5: Government TNSTC Route 1C ===
    BusRoute(
      id = "cbe_tnstc_1c",
      routeNumber = "1C",
      routeName = "Gandhipuram <-> Ondipudur via Singanallur",
      city = "Coimbatore (TNSTC & Private)",
      agency = "TNSTC Coimbatore",
      operatorType = BusOperatorType.GOVERNMENT,
      operatorName = "TNSTC Ondipudur Depot",
      isWomenFreeTravel = true,
      permitNumber = "TN-38/GOVT-1C",
      origin = "Gandhipuram Central Bus Stand",
      destination = "Ondipudur Flyover Depot",
      busType = BusType.ORDINARY,
      totalDistanceKm = 11.5,
      averageDurationMin = 42,
      operatingHours = "05:00 AM - 11:00 PM",
      peakFrequencyMin = 8,
      offPeakFrequencyMin = 15,
      stops = route1CCbeStops,
      activeTrafficAlert = "Singanallur Bus Stand entry delay (+9 min delay)",
      isDelayed = true,
      currentDelayMin = 9,
      totalStopsCount = route1CCbeStops.size,
      stopsDescription = "${route1CCbeStops.size} Stops (Free for Women - Vidiyal Payanam)"
    ),

    // === COIMBATORE 6: Private Bus Route 45B ===
    BusRoute(
      id = "cbe_pvt_45b",
      routeNumber = "45B",
      routeName = "Singanallur <-> Vadavalli via Gandhipuram",
      city = "Coimbatore (TNSTC & Private)",
      agency = "Private City Bus Operator",
      operatorType = BusOperatorType.PRIVATE,
      operatorName = "Vetrivel City Express",
      isWomenFreeTravel = false,
      permitNumber = "TN-38/PVT-45B",
      origin = "Singanallur Bus Stand",
      destination = "Vadavalli Bus Stand",
      busType = BusType.ORDINARY,
      totalDistanceKm = 19.2,
      averageDurationMin = 60,
      operatingHours = "05:30 AM - 10:45 PM",
      peakFrequencyMin = 10,
      offPeakFrequencyMin = 18,
      stops = route45BCbeStops,
      activeTrafficAlert = null,
      isDelayed = false,
      currentDelayMin = 0,
      totalStopsCount = route45BCbeStops.size,
      stopsDescription = "${route45BCbeStops.size} Cross-City Stops"
    ),

    // === BENGALURU BMTC ===
    BusRoute(
      id = "bmtc_500d",
      routeNumber = "500-D",
      routeName = "Silk Board <-> Hebbal Flyover",
      city = "Bengaluru (BMTC)",
      agency = "BMTC Outer Ring Road",
      operatorType = BusOperatorType.GOVERNMENT,
      operatorName = "BMTC Govt Bangalore",
      isWomenFreeTravel = true, // Shakti Scheme in Karnataka
      permitNumber = "KA-01/GOVT-500D",
      origin = "Silk Board Junction",
      destination = "Hebbal Flyover Depot",
      busType = BusType.EXPRESS,
      totalDistanceKm = 32.0,
      averageDurationMin = 110,
      operatingHours = "05:00 AM - 11:45 PM",
      peakFrequencyMin = 8,
      offPeakFrequencyMin = 15,
      stops = route500DStops,
      activeTrafficAlert = "Heavy congestion near Silk Board & Marathahalli flyovers (+14 min delay)",
      isDelayed = true,
      currentDelayMin = 14,
      totalStopsCount = route500DStops.size,
      stopsDescription = "${route500DStops.size} Ring Road Stops"
    ),
    BusRoute(
      id = "bmtc_335e",
      routeNumber = "335-E",
      routeName = "Majestic <-> Kadugodi (ITPL)",
      city = "Bengaluru (BMTC)",
      agency = "BMTC Vajra Premium",
      operatorType = BusOperatorType.GOVERNMENT,
      operatorName = "BMTC Vajra AC",
      isWomenFreeTravel = false,
      permitNumber = "KA-01/GOVT-335E",
      origin = "Majestic Kempegowda BS",
      destination = "Kadugodi Bus Station",
      busType = BusType.VAJRA_AC,
      totalDistanceKm = 27.0,
      averageDurationMin = 95,
      operatingHours = "05:30 AM - 11:15 PM",
      peakFrequencyMin = 10,
      offPeakFrequencyMin = 20,
      stops = route335EStops,
      activeTrafficAlert = "Domlur Flyover slow moving traffic (+8 min delay)",
      isDelayed = true,
      currentDelayMin = 8,
      totalStopsCount = route335EStops.size,
      stopsDescription = "${route335EStops.size} ITPL Stops"
    ),

    // === CHENNAI MTC ===
    BusRoute(
      id = "mtc_23c",
      routeNumber = "23C",
      routeName = "Besant Nagar <-> Ayanavaram",
      city = "Chennai (MTC)",
      agency = "MTC Chennai City Express",
      operatorType = BusOperatorType.GOVERNMENT,
      operatorName = "MTC Govt Town Service",
      isWomenFreeTravel = true, // Vidiyal Payanam in TN
      permitNumber = "TN-01/GOVT-23C",
      origin = "Besant Nagar Beach Terminus",
      destination = "Ayanavaram Bus Terminus",
      busType = BusType.ORDINARY,
      totalDistanceKm = 17.2,
      averageDurationMin = 70,
      operatingHours = "05:00 AM - 10:45 PM",
      peakFrequencyMin = 12,
      offPeakFrequencyMin = 22,
      stops = route23CChennaiStops,
      activeTrafficAlert = "T.Nagar Panagal Park road repair bottleneck (+12 min delay)",
      isDelayed = true,
      currentDelayMin = 12,
      totalStopsCount = route23CChennaiStops.size,
      stopsDescription = "${route23CChennaiStops.size} Chennai Central Stops"
    ),

    // === MUMBAI BEST ===
    BusRoute(
      id = "best_a340",
      routeNumber = "A-340",
      routeName = "Bandra Station <-> Andheri Station (E)",
      city = "Mumbai (BEST)",
      agency = "BEST Mumbai Metro Connect",
      operatorType = BusOperatorType.GOVERNMENT,
      operatorName = "BEST Municipal AC",
      isWomenFreeTravel = false,
      permitNumber = "MH-01/BEST-A340",
      origin = "Bandra Bus Station (W)",
      destination = "Andheri Station (E) Depot",
      busType = BusType.VAJRA_AC,
      totalDistanceKm = 14.2,
      averageDurationMin = 55,
      operatingHours = "05:45 AM - 11:30 PM",
      peakFrequencyMin = 7,
      offPeakFrequencyMin = 14,
      stops = routeA340Stops,
      activeTrafficAlert = "Milan Subway water clearance (+9 min delay)",
      isDelayed = true,
      currentDelayMin = 9,
      totalStopsCount = routeA340Stops.size,
      stopsDescription = "${routeA340Stops.size} Western Suburb Stops"
    ),

    // === DELHI DTC ===
    BusRoute(
      id = "dtc_522",
      routeNumber = "522",
      routeName = "Connaught Place <-> Saket Terminal",
      city = "Delhi (DTC)",
      agency = "DTC Green Metro Route",
      operatorType = BusOperatorType.GOVERNMENT,
      operatorName = "DTC Delhi Govt",
      isWomenFreeTravel = true, // Pink ticket scheme
      permitNumber = "DL-1P/DTC-522",
      origin = "Shivaji Stadium (CP)",
      destination = "Saket Terminal / Select City",
      busType = BusType.ORDINARY,
      totalDistanceKm = 17.8,
      averageDurationMin = 65,
      operatingHours = "05:15 AM - 11:00 PM",
      peakFrequencyMin = 9,
      offPeakFrequencyMin = 16,
      stops = route522DelhiStops,
      activeTrafficAlert = "AIIMS Flyover construction diversion (+11 min delay)",
      isDelayed = true,
      currentDelayMin = 11,
      totalStopsCount = route522DelhiStops.size,
      stopsDescription = "${route522DelhiStops.size} Ring Road Stops"
    )
  )

  val activeTrafficAlerts = listOf(
    TrafficAlert(
      id = "cbe_alert_1",
      city = "Coimbatore (TNSTC & Private)",
      routeNumber = "33A",
      locationName = "Avinashi Road Flyover & Lakshmi Mills",
      title = "Elevated Expressway Pillar Construction",
      description = "Avinashi Road heavy crawling traffic between Lakshmi Mills and Peelamedu. Private and TNSTC buses delayed by 8-10 mins.",
      delayMinutes = 8,
      severity = TrafficSeverity.HEAVY,
      reportedTimeAgo = "3 mins ago"
    ),
    TrafficAlert(
      id = "cbe_alert_2",
      city = "Coimbatore (TNSTC & Private)",
      routeNumber = "11A",
      locationName = "Town Hall Manikoondu & Oppanakara St",
      title = "Market Commercial Rush & Slow Traffic",
      description = "Heavy pedestrian and two-wheeler movement near Clock Tower. Bus speed down to 10 km/h.",
      delayMinutes = 8,
      severity = TrafficSeverity.HEAVY,
      reportedTimeAgo = "6 mins ago"
    ),
    TrafficAlert(
      id = "cbe_alert_3",
      city = "Coimbatore (TNSTC & Private)",
      routeNumber = "20A",
      locationName = "Saravanampatti Junction & Ganapathy",
      title = "IT Corridor Peak Hour Jam",
      description = "Vehicle queue from Sivanandhapuram up to KGISL campus checkpost. Private city cruisers running with 8 min lag.",
      delayMinutes = 8,
      severity = TrafficSeverity.HEAVY,
      reportedTimeAgo = "11 mins ago"
    ),
    TrafficAlert(
      id = "cbe_alert_4",
      city = "Coimbatore (TNSTC & Private)",
      routeNumber = "70A",
      locationName = "Flower Market (Poo Market) Signal",
      title = "Flower Delivery Lorries Bottleneck",
      description = "Morning wholesale unloading causing single-lane operation near Mettupalayam road intersection.",
      delayMinutes = 6,
      severity = TrafficSeverity.MODERATE,
      reportedTimeAgo = "15 mins ago"
    ),
    TrafficAlert(
      id = "alert_bengaluru",
      city = "Bengaluru (BMTC)",
      routeNumber = "500-D",
      locationName = "Silk Board Flyover & Bellandur",
      title = "Outer Ring Road Metro Work Congestion",
      description = "Heavy crawling bumper-to-bumper traffic from HSR towards Bellandur. Average speed reduced to 12 km/h.",
      delayMinutes = 14,
      severity = TrafficSeverity.HEAVY,
      reportedTimeAgo = "4 mins ago"
    )
  )

  // Calculate ticket fare based on Indian bus stage system and Government vs Private rates
  fun calculateTicketFare(fromStop: BusStopSchedule, toStop: BusStopSchedule): FareEstimate {
    val dist = Math.abs(toStop.distanceFromOriginKm - fromStop.distanceFromOriginKm)
    val distanceKm = if (dist <= 0.4) 1.2 else dist
    val stopsTraversed = Math.abs(toStop.stopSequence - fromStop.stopSequence).coerceAtLeast(1)

    // Stage calculation (typically 1 stage = ~2 km in Tamil Nadu and Indian cities)
    val stages = (distanceKm / 2.0).toInt().coerceAtLeast(1)

    // Government Ordinary Bus: Starts at ₹5 for 1 stage, ₹6 for 2 stages, +₹1-2 per stage, max ₹15 in city
    val govtOrdinary = when (stages) {
      1 -> 5
      2 -> 6
      3 -> 8
      4 -> 10
      5 -> 12
      else -> (12 + (stages - 5) * 2).coerceIn(5, 20)
    }

    // Private Ordinary Bus: Starts at ₹6 for 1 stage, ₹8 for 2 stages, ₹11, ₹14, ₹18 max
    val pvtOrdinary = when (stages) {
      1 -> 6
      2 -> 8
      3 -> 11
      4 -> 14
      5 -> 16
      else -> (16 + (stages - 5) * 2).coerceIn(6, 25)
    }

    // Express / Fast Track
    val express = (10 + (distanceKm - 2).coerceAtLeast(0.0) * 1.8).roundToInt().coerceIn(10, 35)

    // AC Low Floor Electric / Airport Deluxe
    val acVajra = (25 + (distanceKm - 2).coerceAtLeast(0.0) * 3.2).roundToInt().coerceIn(25, 75)

    // Student Concession: 50% discount
    val student = (govtOrdinary * 0.5).roundToInt().coerceAtLeast(3)

    // Senior Citizen Concession: 50% discount
    val senior = (govtOrdinary * 0.5).roundToInt().coerceAtLeast(3)

    // Daily Transit Pass
    val dailyPass = 50 // ₹50 local city pass (or ₹120 AC)

    // Travel time estimate (approx 22 km/h in Coimbatore / city traffic)
    val travelTimeMin = ((distanceKm / 22.0) * 60).roundToInt().coerceAtLeast(5)

    return FareEstimate(
      fromStop = fromStop.stopName,
      toStop = toStop.stopName,
      distanceKm = Math.round(distanceKm * 10.0) / 10.0,
      stopsTraversed = stopsTraversed,
      isGovernmentBus = true,
      isWomenFreeEligible = true, // Free in TNSTC ordinary under Vidiyal Payanam!
      governmentOrdinaryFare = govtOrdinary,
      privateOrdinaryFare = pvtOrdinary,
      expressFare = express,
      acVajraFare = acVajra,
      studentConcessionFare = student,
      seniorCitizenFare = senior,
      dailyPassPrice = dailyPass,
      approxTravelTimeMin = travelTimeMin
    )
  }

  // Get frequency and available buses for any route and time slot (Government & Private breakdown)
  fun getTimeSlotSchedules(route: BusRoute): List<TimeSlotSchedule> {
    val isCoimbatore = route.city.contains("Coimbatore")
    val govtPrefix = if (isCoimbatore) "TN-38-N" else "KA-01-F"
    val pvtPrefix = if (isCoimbatore) "TN-38-AQ" else "KA-01-P"

    val peakHeadway = route.peakFrequencyMin
    val offPeakHeadway = route.offPeakFrequencyMin

    return listOf(
      TimeSlotSchedule(
        slotId = "slot_early_morning",
        slotTitle = "Early Morning Service",
        timeRange = "05:00 AM - 07:00 AM",
        totalBusesCount = (120 / (offPeakHeadway + 4)).coerceAtLeast(8),
        governmentBusesCount = 5,
        privateBusesCount = 3,
        frequencySummary = "Every ${offPeakHeadway + 3} - ${offPeakHeadway + 8} mins",
        activeBusesCount = 3,
        seatsAvailableCount = 6,
        averageHeadwayMin = offPeakHeadway + 3,
        busTrips = listOf(
          BusTripDeparture("EM1", route.routeNumber, "$govtPrefix-1021", "05:15 AM", "05:50 AM", route.busType, BusOperatorType.GOVERNMENT, "TNSTC", true, "Departed", 0, OccupancyStatus.SEATS_AVAILABLE),
          BusTripDeparture("EM2", route.routeNumber, "$pvtPrefix-2044", "05:35 AM", "06:10 AM", route.busType, BusOperatorType.PRIVATE, "Sri Krishna", false, "Departed", 0, OccupancyStatus.SEATS_AVAILABLE),
          BusTripDeparture("EM3", route.routeNumber, "$govtPrefix-1088", "05:55 AM", "06:30 AM", route.busType, BusOperatorType.GOVERNMENT, "TNSTC", true, "On Route", 0, OccupancyStatus.SEATS_AVAILABLE),
          BusTripDeparture("EM4", route.routeNumber, "$pvtPrefix-3130", "06:15 AM", "06:50 AM", route.busType, BusOperatorType.PRIVATE, "Cheran Trans", false, "On Route", 2, OccupancyStatus.SEATS_AVAILABLE),
          BusTripDeparture("EM5", route.routeNumber, "$govtPrefix-1192", "06:35 AM", "07:10 AM", route.busType, BusOperatorType.GOVERNMENT, "TNSTC", true, "Approaching", 0, OccupancyStatus.STANDING_ROOM),
          BusTripDeparture("EM6", route.routeNumber, "$pvtPrefix-4180", "06:50 AM", "07:25 AM", route.busType, BusOperatorType.PRIVATE, "Hindusthan", false, "Scheduled", 0, OccupancyStatus.SEATS_AVAILABLE)
        )
      ),
      TimeSlotSchedule(
        slotId = "slot_morning_peak",
        slotTitle = "Morning Peak Rush",
        timeRange = "07:00 AM - 10:30 AM",
        totalBusesCount = (210 / peakHeadway).coerceAtLeast(22),
        governmentBusesCount = 13,
        privateBusesCount = 9,
        frequencySummary = "Every $peakHeadway - ${peakHeadway + 2} mins",
        activeBusesCount = 8,
        seatsAvailableCount = 3,
        averageHeadwayMin = peakHeadway,
        busTrips = listOf(
          BusTripDeparture("MP1", route.routeNumber, "$govtPrefix-4219", "07:10 AM", "07:45 AM", route.busType, BusOperatorType.GOVERNMENT, "TNSTC Govt", true, "Departed", 0, OccupancyStatus.CROWDED),
          BusTripDeparture("MP2", route.routeNumber, "$pvtPrefix-8812", "07:18 AM", "07:53 AM", route.busType, BusOperatorType.PRIVATE, "Sri Krishna", false, "Departed", 3, OccupancyStatus.CROWDED),
          BusTripDeparture("MP3", route.routeNumber, "$govtPrefix-3104", "07:26 AM", "08:01 AM", route.busType, BusOperatorType.GOVERNMENT, "TNSTC Govt", true, "On Route", 6, OccupancyStatus.STANDING_ROOM),
          BusTripDeparture("MP4", route.routeNumber, "$pvtPrefix-9920", "07:34 AM", "08:09 AM", route.busType, BusOperatorType.PRIVATE, "Hindusthan", false, "On Route", 8, OccupancyStatus.CROWDED),
          BusTripDeparture("MP5", route.routeNumber, "$govtPrefix-5531", "07:42 AM", "08:17 AM", route.busType, BusOperatorType.GOVERNMENT, "TNSTC Govt", true, "Approaching", 5, OccupancyStatus.STANDING_ROOM),
          BusTripDeparture("MP6", route.routeNumber, "$pvtPrefix-1142", "07:50 AM", "08:25 AM", route.busType, BusOperatorType.PRIVATE, "Cheran Trans", false, "Boarding at Terminal", 0, OccupancyStatus.SEATS_AVAILABLE),
          BusTripDeparture("MP7", route.routeNumber, "$govtPrefix-7764", "08:00 AM", "08:35 AM", route.busType, BusOperatorType.GOVERNMENT, "TNSTC Govt", true, "Scheduled", 0, OccupancyStatus.SEATS_AVAILABLE),
          BusTripDeparture("MP8", route.routeNumber, "$pvtPrefix-2299", "08:10 AM", "08:45 AM", route.busType, BusOperatorType.PRIVATE, "Vetrivel", false, "Scheduled", 0, OccupancyStatus.SEATS_AVAILABLE),
          BusTripDeparture("MP9", route.routeNumber, "$govtPrefix-3312", "08:20 AM", "08:55 AM", route.busType, BusOperatorType.GOVERNMENT, "TNSTC Govt", true, "Scheduled", 0, OccupancyStatus.STANDING_ROOM),
          BusTripDeparture("MP10", route.routeNumber, "$pvtPrefix-4489", "08:30 AM", "09:05 AM", route.busType, BusOperatorType.PRIVATE, "SRS Cruiser", false, "Scheduled", 0, OccupancyStatus.CROWDED)
        )
      ),
      TimeSlotSchedule(
        slotId = "slot_midday",
        slotTitle = "Midday Regular Hours",
        timeRange = "11:00 AM - 04:00 PM",
        totalBusesCount = (300 / offPeakHeadway).coerceAtLeast(16),
        governmentBusesCount = 9,
        privateBusesCount = 7,
        frequencySummary = "Every $offPeakHeadway - ${offPeakHeadway + 3} mins",
        activeBusesCount = 5,
        seatsAvailableCount = 8,
        averageHeadwayMin = offPeakHeadway,
        busTrips = listOf(
          BusTripDeparture("MD1", route.routeNumber, "$govtPrefix-4421", "11:05 AM", "11:40 AM", route.busType, BusOperatorType.GOVERNMENT, "TNSTC Govt", true, "Departed", 0, OccupancyStatus.SEATS_AVAILABLE),
          BusTripDeparture("MD2", route.routeNumber, "$pvtPrefix-1120", "11:20 AM", "11:55 AM", route.busType, BusOperatorType.PRIVATE, "Sri Krishna", false, "On Route", 0, OccupancyStatus.SEATS_AVAILABLE),
          BusTripDeparture("MD3", route.routeNumber, "$govtPrefix-7650", "11:35 AM", "12:10 PM", route.busType, BusOperatorType.GOVERNMENT, "TNSTC Govt", true, "On Route", 2, OccupancyStatus.STANDING_ROOM),
          BusTripDeparture("MD4", route.routeNumber, "$pvtPrefix-9099", "11:50 AM", "12:25 PM", route.busType, BusOperatorType.PRIVATE, "Hindusthan", false, "Approaching", 0, OccupancyStatus.SEATS_AVAILABLE),
          BusTripDeparture("MD5", route.routeNumber, "$govtPrefix-3412", "12:10 PM", "12:45 PM", route.busType, BusOperatorType.GOVERNMENT, "TNSTC Govt", true, "Scheduled", 0, OccupancyStatus.SEATS_AVAILABLE),
          BusTripDeparture("MD6", route.routeNumber, "$pvtPrefix-5510", "12:30 PM", "01:05 PM", route.busType, BusOperatorType.PRIVATE, "Cheran Trans", false, "Scheduled", 0, OccupancyStatus.SEATS_AVAILABLE)
        )
      ),
      TimeSlotSchedule(
        slotId = "slot_evening_peak",
        slotTitle = "Evening Peak Rush",
        timeRange = "04:30 PM - 08:30 PM",
        totalBusesCount = (240 / (peakHeadway - 1).coerceAtLeast(5)).coerceAtLeast(26),
        governmentBusesCount = 15,
        privateBusesCount = 11,
        frequencySummary = "Every ${(peakHeadway - 1).coerceAtLeast(5)} - ${peakHeadway + 2} mins",
        activeBusesCount = 9,
        seatsAvailableCount = 2,
        averageHeadwayMin = (peakHeadway - 1).coerceAtLeast(5),
        busTrips = listOf(
          BusTripDeparture("EP1", route.routeNumber, "$govtPrefix-4219", "04:40 PM", "05:15 PM", route.busType, BusOperatorType.GOVERNMENT, "TNSTC Govt", true, "Departed", 5, OccupancyStatus.CROWDED),
          BusTripDeparture("EP2", route.routeNumber, "$pvtPrefix-8772", "04:47 PM", "05:22 PM", route.busType, BusOperatorType.PRIVATE, "Sri Krishna", false, "Departed", 8, OccupancyStatus.CROWDED),
          BusTripDeparture("EP3", route.routeNumber, "$govtPrefix-1204", "04:54 PM", "05:29 PM", route.busType, BusOperatorType.GOVERNMENT, "TNSTC Govt", true, "On Route", 7, OccupancyStatus.CROWDED),
          BusTripDeparture("EP4", route.routeNumber, "$pvtPrefix-6632", "05:01 PM", "05:36 PM", route.busType, BusOperatorType.PRIVATE, "Hindusthan", false, "On Route", 9, OccupancyStatus.STANDING_ROOM),
          BusTripDeparture("EP5", route.routeNumber, "$govtPrefix-9901", "05:08 PM", "05:43 PM", route.busType, BusOperatorType.GOVERNMENT, "TNSTC Govt", true, "Approaching", 4, OccupancyStatus.SEATS_AVAILABLE),
          BusTripDeparture("EP6", route.routeNumber, "$pvtPrefix-4410", "05:15 PM", "05:50 PM", route.busType, BusOperatorType.PRIVATE, "Cheran Trans", false, "Boarding at Terminal", 0, OccupancyStatus.SEATS_AVAILABLE),
          BusTripDeparture("EP7", route.routeNumber, "$govtPrefix-7712", "05:22 PM", "05:57 PM", route.busType, BusOperatorType.GOVERNMENT, "TNSTC Govt", true, "Scheduled", 0, OccupancyStatus.STANDING_ROOM),
          BusTripDeparture("EP8", route.routeNumber, "$pvtPrefix-8890", "05:30 PM", "06:05 PM", route.busType, BusOperatorType.PRIVATE, "Vetrivel", false, "Scheduled", 0, OccupancyStatus.CROWDED)
        )
      ),
      TimeSlotSchedule(
        slotId = "slot_night",
        slotTitle = "Night Town Service",
        timeRange = "09:00 PM - 11:45 PM",
        totalBusesCount = (165 / (offPeakHeadway + 6)).coerceAtLeast(10),
        governmentBusesCount = 6,
        privateBusesCount = 4,
        frequencySummary = "Every ${offPeakHeadway + 6} - ${offPeakHeadway + 10} mins",
        activeBusesCount = 4,
        seatsAvailableCount = 6,
        averageHeadwayMin = offPeakHeadway + 6,
        busTrips = listOf(
          BusTripDeparture("NS1", route.routeNumber, "$govtPrefix-7721", "09:10 PM", "09:45 PM", route.busType, BusOperatorType.GOVERNMENT, "TNSTC Govt", true, "Departed", 0, OccupancyStatus.SEATS_AVAILABLE),
          BusTripDeparture("NS2", route.routeNumber, "$pvtPrefix-3390", "09:30 PM", "10:05 PM", route.busType, BusOperatorType.PRIVATE, "Sri Krishna", false, "On Route", 0, OccupancyStatus.SEATS_AVAILABLE),
          BusTripDeparture("NS3", route.routeNumber, "$govtPrefix-5509", "09:55 PM", "10:30 PM", route.busType, BusOperatorType.GOVERNMENT, "TNSTC Govt", true, "Approaching", 0, OccupancyStatus.SEATS_AVAILABLE),
          BusTripDeparture("NS4", route.routeNumber, "$pvtPrefix-1100", "10:20 PM", "10:55 PM", route.busType, BusOperatorType.PRIVATE, "Hindusthan", false, "Scheduled", 0, OccupancyStatus.SEATS_AVAILABLE),
          BusTripDeparture("NS5", route.routeNumber, "$govtPrefix-8822", "10:50 PM", "11:25 PM", route.busType, BusOperatorType.GOVERNMENT, "TNSTC Govt", true, "Scheduled", 0, OccupancyStatus.SEATS_AVAILABLE)
        )
      )
    )
  }

  // Real-time bus tracker simulator flow with live GPS, stop count, and speed
  fun observeLiveBusTracking(route: BusRoute): Flow<LiveBus> = flow {
    val stops = route.stops
    if (stops.size < 2) return@flow

    var currentStopIdx = (stops.size / 3).coerceIn(0, stops.lastIndex - 1)
    var progressBetweenStops = 0.25f
    val vehicleNumber = when (route.routeNumber) {
      "11A" -> "TN-38-N-2419"
      "70A" -> "TN-38-AQ-6612"
      "20A" -> "TN-38-AS-9904"
      "33A" -> "TN-38-N-4410"
      "1C" -> "TN-38-N-1102"
      "45B" -> "TN-38-AR-3390"
      "500-D" -> "KA-01-FA-4219"
      "335-E" -> "KA-53-F-1288"
      "A-340" -> "MH-01-CV-7834"
      "522" -> "DL-1P-D-9012"
      else -> "TN-38-N-5541"
    }

    while (true) {
      val stopA = stops[currentStopIdx]
      val nextStopIdx = (currentStopIdx + 1).coerceAtMost(stops.lastIndex)
      val stopB = stops[nextStopIdx]

      val lat = stopA.latitude + (stopB.latitude - stopA.latitude) * progressBetweenStops
      val lng = stopA.longitude + (stopB.longitude - stopA.longitude) * progressBetweenStops

      // Bearing angle
      val dLat = Math.toRadians(stopB.latitude - stopA.latitude)
      val dLng = Math.toRadians(stopB.longitude - stopA.longitude)
      val y = sin(dLng) * cos(Math.toRadians(stopB.latitude))
      val x = cos(Math.toRadians(stopA.latitude)) * sin(Math.toRadians(stopB.latitude)) -
              sin(Math.toRadians(stopA.latitude)) * cos(Math.toRadians(stopB.latitude)) * cos(dLng)
      val bearing = ((Math.toDegrees(atan2(y, x)) + 360) % 360).toFloat()

      // Speed & ETA
      val baseSpeed = when (stopA.trafficCondition) {
        TrafficSeverity.HEAVY -> 12
        TrafficSeverity.MODERATE -> 22
        TrafficSeverity.NORMAL -> 38
      }
      val speed = (baseSpeed + (-2..3).random()).coerceIn(8, 48)
      val remainingFraction = 1f - progressBetweenStops
      val distKm = Math.abs(stopB.distanceFromOriginKm - stopA.distanceFromOriginKm) * remainingFraction
      val etaMinutes = ((distKm / speed) * 60).roundToInt().coerceAtLeast(1)

      val occupancy = when {
        currentStopIdx < 2 -> OccupancyStatus.SEATS_AVAILABLE
        currentStopIdx in 2..6 -> OccupancyStatus.CROWDED
        else -> OccupancyStatus.STANDING_ROOM
      }

      val totalRouteProgress = (currentStopIdx.toFloat() + progressBetweenStops) / (stops.size - 1).toFloat()
      val remainingStops = (stops.size - 1 - currentStopIdx).coerceAtLeast(0)

      emit(
        LiveBus(
          busId = "live_${route.id}",
          routeNumber = route.routeNumber,
          vehicleNumber = vehicleNumber,
          busType = route.busType,
          operatorType = route.operatorType,
          operatorName = route.operatorName,
          isWomenFreeTravel = route.isWomenFreeTravel,
          latitude = lat,
          longitude = lng,
          headingDeg = bearing,
          speedKmph = speed,
          nextStopId = stopB.stopId,
          nextStopName = stopB.stopName,
          etaNextStopMin = etaMinutes,
          occupancy = occupancy,
          progressPercent = totalRouteProgress.coerceIn(0f, 1f),
          currentStopIndex = currentStopIdx,
          totalStopsCount = stops.size,
          remainingStopsCount = remainingStops,
          delayMinutes = route.currentDelayMin
        )
      )

      progressBetweenStops += 0.05f
      if (progressBetweenStops >= 1.0f) {
        progressBetweenStops = 0.0f
        currentStopIdx++
        if (currentStopIdx >= stops.lastIndex) {
          currentStopIdx = 0
        }
      }

      delay(1200)
    }
  }
}
