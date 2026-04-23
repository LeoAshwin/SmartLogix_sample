package com.cognizant.smartlogix.integration;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.Assumptions;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * SmartLogix Phase 2: "Golden Path" End-to-End Integration Test
 *
 * <p>Validates the full lifecycle from Ingestion to Returns with JWT auth.
 * Each test acquires a Bearer token for the appropriate role before calling
 * the protected API endpoint.
 *
 * <p>Test user credentials are seeded by the Flyway migration
 * V20260424000000__jwt_roles_seed_users.sql.
 * All seed accounts share the password: {@code Password@123}
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class E2EIntegrationFlowTest {

        @Autowired
        private WebApplicationContext webApplicationContext;

        @Autowired
        private JdbcTemplate jdbcTemplate;

        private MockMvc mockMvc;

        // Static shared state to survive between @Order method instances
        private static String fulfillmentId;
        private static Long uniqueTransactionId;

        // Cached tokens per role — fetched lazily
        private static String adminToken;
        private static String dispatcherToken;
        private static String driverToken;
        private static String carrierToken;
        private static String customerToken;
        private static String merchantToken;

        @BeforeEach
        public void setup() {
                // Apply Spring Security filter chain to MockMvc
                this.mockMvc = MockMvcBuilders
                        .webAppContextSetup(this.webApplicationContext)
                        .apply(SecurityMockMvcConfigurers.springSecurity())
                        .build();

                if (uniqueTransactionId == null) {
                        uniqueTransactionId = System.currentTimeMillis() % 100000;
                }
        }

        // =====================================================================
        // Auth Helper
        // =====================================================================

        /**
         * Authenticates against {@code POST /api/auth/login} and returns the
         * Bearer JWT token string (without the "Bearer " prefix).
         *
         * @param email    seed user email
         * @param password shared test password
         * @return raw JWT token
         */
        private String loginAs(String email, String password) throws Exception {
                String body = """
                        {"email": "%s", "password": "%s"}
                        """.formatted(email, password);

                MvcResult result = mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body))
                        .andExpect(status().isOk())
                        .andReturn();

                return JsonPath.read(result.getResponse().getContentAsString(), "$.token");
        }

        /** Returns a cached admin token, fetching it on first call. */
        private String adminToken() throws Exception {
                if (adminToken == null) adminToken = loginAs("admin@smartlogix.io", "Password@123");
                return adminToken;
        }

        /** Returns a cached dispatcher token. */
        private String dispatcherToken() throws Exception {
                if (dispatcherToken == null) dispatcherToken = loginAs("dispatcher@smartlogix.io", "Password@123");
                return dispatcherToken;
        }

        /** Returns a cached driver token. */
        private String driverToken() throws Exception {
                if (driverToken == null) driverToken = loginAs("driver@smartlogix.io", "Password@123");
                return driverToken;
        }

        /** Returns a cached carrier token. */
        private String carrierToken() throws Exception {
                if (carrierToken == null) carrierToken = loginAs("carrier@smartlogix.io", "Password@123");
                return carrierToken;
        }

        /** Returns a cached customer token. */
        private String customerToken() throws Exception {
                if (customerToken == null) customerToken = loginAs("customer@smartlogix.io", "Password@123");
                return customerToken;
        }

        /** Returns a cached merchant token. */
        private String merchantToken() throws Exception {
                if (merchantToken == null) merchantToken = loginAs("merchant@smartlogix.io", "Password@123");
                return merchantToken;
        }

        // =====================================================================
        // Master Data Seeding
        // =====================================================================

        /**
         * Seeds essential master data to satisfy Foreign Key constraints.
         * Uses 'INSERT IGNORE' to ensure parent records exist without delete conflicts.
         */
        private void seedMasterData() {
                System.out.println(">>> Antigravity Phase: Ensuring Master Data exists...");

                // 1. Merchant (Required for Fulfillment)
                jdbcTemplate.execute("INSERT IGNORE INTO merchant (merchant_id, name, status) " +
                                "VALUES (1, 'Integrated Merchant', 'ACTIVE')");

                // 2. Service Zone (Required for Fulfillment)
                jdbcTemplate.execute(
                                "INSERT IGNORE INTO service_zone (zone_id, name, capacity_per_slot, status, time_zone) "
                                                +
                                                "VALUES ('Z-WEST', 'Western Zone', 100, 'ACTIVE', 'UTC')");

                // 3. Carriers (Required for Booking)
                jdbcTemplate.execute("INSERT IGNORE INTO carriers (carrier_id, name, status) " +
                                "VALUES ('C-001', 'Test Carrier', 'ACTIVE')");

                // 4. Drivers (Required for Manifest/POD)
                jdbcTemplate.execute("INSERT IGNORE INTO drivers (driver_id, license_number, status) " +
                                "VALUES ('D-001', 'LIC-12345', 'ACTIVE')");

                // 5. Vehicles (Required for Manifest)
                jdbcTemplate.execute("INSERT IGNORE INTO vehicles (vehicle_id, type, registration_number, status) " +
                                "VALUES ('V-001', 'TRUCK', 'REG-12345', 'ACTIVE')");

                // 6. Depot (Required for Manifest)
                jdbcTemplate.execute("INSERT IGNORE INTO depot (depot_id, name, status) " +
                                "VALUES (1, 'Central Hub', 'ACTIVE')");
        }

        // =====================================================================
        // Test Checkpoints
        // =====================================================================

        @Test
        @Order(1)
        @DisplayName("Checkpoint 1: Infrastructure Setup (LOGISTICS_MANAGER)")
        void checkpt1_setupMasterData() throws Exception {
                seedMasterData();

                String payload = """
                                {
                                  "zoneId": "Z-WEST",
                                  "name": "Western Zone",
                                  "capacityPerSlot": 100,
                                  "timeZone": "UTC",
                                  "status": "ACTIVE"
                                }
                                """;

                // ServiceZoneController requires LOGISTICS_MANAGER or ADMIN
                mockMvc.perform(post("/api/service-zones")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + adminToken())
                                .content(payload))
                                .andExpect(status().is2xxSuccessful());
        }

        @Test
        @Order(2)
        @DisplayName("Checkpoint 2: Order Ingestion (MERCHANT)")
        void checkpt2_orderIngestionModule() throws Exception {
                String payload = """
                                {
                                  "orderId": "ORD-%d",
                                  "merchantId": 1,
                                  "serviceZoneId": "Z-WEST",
                                  "serviceLevel": "STANDARD",
                                  "packageWeightKg": 2.5,
                                  "packageVolumeM3": 0.01,
                                  "dimensionsJson": "{}",
                                  "deliveryWindowStart": "2026-12-31T10:00:00",
                                  "deliveryWindowEnd": "2026-12-31T18:00:00"
                                }
                                """.formatted(uniqueTransactionId);

                MvcResult result = mockMvc.perform(post("/api/fulfillments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + merchantToken())
                                .content(payload))
                                .andReturn();

                // Debug log for troubleshooting 500 errors
                if (result.getResponse().getStatus() >= 400) {
                        System.out.println(">>> API ERROR LOG: " + result.getResponse().getContentAsString());
                }

                Assertions.assertEquals(201, result.getResponse().getStatus(), "Fulfillment creation failed!");

                fulfillmentId = JsonPath.read(result.getResponse().getContentAsString(), "$.fulfillmentId");
                System.out.println(">>> Generated Fulfillment ID: " + fulfillmentId);
                Assertions.assertNotNull(fulfillmentId, "FulfillmentID must be generated to proceed.");
        }

        @Test
        @Order(3)
        @DisplayName("Checkpoint 3: Carrier Booking (DISPATCHER)")
        void checkpt3_carrierPricingModule() throws Exception {
                String payload = """
                                {
                                  "carrierId": "C-001",
                                  "fulfillmentId": "%s",
                                  "currency": "USD"
                                }
                                """.formatted(fulfillmentId);

                mockMvc.perform(post("/api/carriers/bookings")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + dispatcherToken())
                                .content(payload))
                                .andExpect(status().is2xxSuccessful());
        }

        @Test
        @Order(4)
        @DisplayName("Checkpoint 4: Manifest Generation (DISPATCHER)")
        void checkpt4_fleetRoutingModule() throws Exception {
                String payload = """
                                {
                                  "depotId": 1,
                                  "vehicleId": "V-001",
                                  "driverId": "D-001",
                                  "scheduledDate": "2026-12-31",
                                  "maxCapacityKg": 1000.0,
                                  "orders": [
                                     { "orderId": "%s", "lat": 34.0, "lng": -118.0, "weight": 2.5 }
                                  ]
                                }
                                """.formatted(fulfillmentId);

                mockMvc.perform(post("/api/v1/manifests/generate")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + dispatcherToken())
                                .content(payload))
                                .andExpect(status().is2xxSuccessful());
        }

        @Test
        @Order(5)
        @DisplayName("Checkpoint 5: Tracking Event (DRIVER)")
        void checkpt5_trackingModule() throws Exception {
                String payload = "{\"lat\": 34.0, \"lng\": -118.0, \"timestamp\": \"2026-12-31T11:00:00Z\"}";

                mockMvc.perform(post("/api/v1/driver/events/" + fulfillmentId + "?type=OUT_FOR_DELIVERY")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + driverToken())
                                .content(payload))
                                .andExpect(status().is2xxSuccessful());
        }

        @Test
        @Order(6)
        @DisplayName("Checkpoint 6: Exception Report (DRIVER)")
        void checkpt6_exceptionModule() throws Exception {
                String payload = """
                                {
                                  "fulfillmentId": "%s",
                                  "driverId": "D-001",
                                  "reasonCode": "TRAFFIC_DELAY",
                                  "details": "Heavy traffic on main route"
                                }
                                """.formatted(fulfillmentId);

                mockMvc.perform(post("/api/v1/driver/exceptions/report")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + driverToken())
                                .content(payload))
                                .andExpect(status().is2xxSuccessful());
        }

        @Test
        @Order(7)
        @DisplayName("Checkpoint 7: Proof of Delivery (DRIVER)")
        void checkpt7_podModule() throws Exception {
                Assumptions.assumeTrue(fulfillmentId != null);

                // This payload satisfies all @NotNull and @NotEmpty constraints in your Record
                String payload = """
                                {
                                  "fulfillmentId": "%s",
                                  "driverId": "D-001",
                                  "photoUris": ["s3://smartlogix/pod-img.jpg"],
                                  "signatureUri": "s3://smartlogix/customer-sig.png",
                                  "quantityDelivered": 1,
                                  "notes": "Verified by E2E Test",
                                  "location": {
                                    "latitude": 34.0522,
                                    "longitude": -118.2437,
                                    "accuracyMeters": 5.0,
                                    "timestamp": "2026-04-19T21:00:00Z"
                                  },
                                  "metadata": {
                                    "deviceId": "TEST-DEV-01",
                                    "appVersion": "1.0.0"
                                  }
                                }
                                """.formatted(fulfillmentId);

                mockMvc.perform(post("/api/v1/driver/pod")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + driverToken())
                                .content(payload))
                                .andExpect(status().is2xxSuccessful());
        }

        @Test
        @Order(8)
        @DisplayName("Checkpoint 8: Returns Initiation (CUSTOMER)")
        void checkpt8_returnModule() throws Exception {
                String payload = """
                                {
                                  "fulfillmentId": "%s",
                                  "pickupWindowStart": "2027-01-01T09:00:00",
                                  "pickupWindowEnd": "2027-01-01T17:00:00"
                                }
                                """.formatted(fulfillmentId);

                mockMvc.perform(post("/api/returns")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + customerToken())
                                .content(payload))
                                .andExpect(status().is2xxSuccessful());
        }
}