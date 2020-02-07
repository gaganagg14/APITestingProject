package com.booking.test;
import org.testng.annotations.Test;

import com.booking.dto.BookingDatesRequest;
import com.booking.dto.BookingDetails;
import com.booking.dto.BookingRequest;
import com.booking.dto.BookingResponse;
import com.booking.dto.GetBookingResponse;
import com.booking.dto.RoomBookings;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import org.testng.AssertJUnit;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import static io.restassured.RestAssured.given;


public class BookingApiTests {

	public static int bookingId;
	public static int roomId;
	BookingTestHelper helper = new BookingTestHelper();
	BookingResponse createBookingResponse = new BookingResponse();
	GetBookingResponse getBooking = new GetBookingResponse();
	BookingDetails bookingDetails = new BookingDetails();
	RoomBookings roomBooking = new RoomBookings();

	/**
	 * <pre>
	 * Verify createBooking API
	 * </pre>
	 * <p>
	 * <pre>
	 * Test:CREATE_BOOKING
	 *     Steps:
	 *     1. Create Request body for booking API
	 *     2. Make booking API call.
	 *     3. Validate API status code
	 *     4. validate bookingId, roomId sent in request is same returned in response
	 *     
	 * Test:CREATE_BOOKING_CHECKOUTIN_WITHOUTIME
	 * Steps
	 *     1. Create Request body for booking API Not adding time information
	 *     2. Make booking API call.
	 *     3. Validate API status code
	 *     4. validate bookingId, roomId sent in request is same returned in response
	 * </pre>
	 *</p>
	 * @param sTestId       TestCase ID
	 * @param sTestDesc     TestCase Description
	 * @throws Exception
	 */

	@Test(dataProvider = "createBooking", dataProviderClass = BookingDataProvider.class, priority=1)
	public void createBooking(String sTestId, String sTestDesc){	

		//Generating random roomIds/bookingId to avoid changing roomDetails everytime
		bookingId = helper.randomNumber();
		roomId = helper.randomNumber();

		//CreateBookingRequest
		BookingRequest createBooking = helper.createBookingObj(bookingId,roomId);
		
		//Booking Request Not adding time information
		if(sTestId.equalsIgnoreCase("CREATE_BOOKING_CHECKOUTIN_WITHOUTIME")){
			createBooking.getBookingdates().setCheckin("2020-02-07");
			createBooking.getBookingdates().setCheckout("2020-02-08");
		}

		// Building request using requestSpecBuilder
		RequestSpecBuilder builder = new RequestSpecBuilder();

		//Setting content type as application/json
		builder.setContentType("application/json");
		builder.addHeader("Accept", "application/json");
		builder.setBody(createBooking);

		RequestSpecification requestSpec = builder.build();
		Response res = given().spec(requestSpec).when().post(TestConstants.BOOKING_URL);
		Assert.assertEquals(201, res.getStatusCode());
		createBookingResponse = res.as(BookingResponse.class);
		bookingId = createBookingResponse.getBookingid();
		roomId = createBookingResponse.getBooking().getRoomid();
		System.out.println(bookingId);
		System.out.println(roomId);

	}
	
	/**
	 * <pre>
	 * Verify getBooking API
	 * </pre>
	 * <p>
	 * <pre>
	 * Test:GET_BOOKING
	 *     Steps:
	 *     1. Make getBooking API call for bookingId created.
	 *     2. Validate API status code
	 *     3. validate bookingId, roomId sent in request is same returned in response
	 *     
	 * </pre>
	 *</p>
	 * @param sTestId       TestCase ID
	 * @param sTestDesc     TestCase Description
	 * @throws Exception
	 */

	@Test(dataProvider = "getBooking", dataProviderClass = BookingDataProvider.class, priority=2)
	public void getBookingDetails(String sTestId, String sTestDesc){	

		//Calling createBooking Method
		createBooking("CREATE_BOOKING", "Positive test scenario creating booking for room");
		
		// Building request using requestSpecBuilder
		RequestSpecBuilder builder = new RequestSpecBuilder();

		//Setting content type as application/json
		builder.setContentType("application/json");
		builder.addHeader("Accept", "application/json");

		RequestSpecification requestSpec = builder.build();
		Response res = given().spec(requestSpec).when().get(TestConstants.BOOKING_URL+bookingId);
		Assert.assertEquals(200, res.getStatusCode());
		getBooking = res.as(GetBookingResponse.class);
		
		//Verifying created booking Details
		Assert.assertEquals(getBooking.getBookingid(), bookingId);
		Assert.assertEquals(getBooking.getRoomid(), roomId);

	}
	
	/**
	 * <pre>
	 * Verify getBookingRoomDetails API
	 * </pre>
	 * <p>
	 * <pre>
	 * Test:GET_ROOM_BOOKING
	 *     Steps:
	 *     1. Make getBookingRoom API call for bookingId created.
	 *     2. Validate API status code
	 *     3. validate bookingId, roomId sent in request is same returned in response
	 *     
	 * Test:GET_ROOM_BOOKING_TWICE
	 *     Steps:
	 *     1. Create 2 bookings for same roomId different dates
	 *     2. Make getBookingRoom API call for bookingId created.
	 *     3. Validate API status code
	 *     4. Validate bookingId, roomId sent in request is same returned in response
	 *     5. Validate 2 bookings are there for particular roomId
	 *     
	 * </pre>
	 *</p>
	 * @param sTestId       TestCase ID
	 * @param sTestDesc     TestCase Description
	 * @throws Exception
	 */

	@Test(dataProvider = "getBookingRoomDetails", dataProviderClass = BookingDataProvider.class, priority=3)
	public void getBookingRoomDetails(String sTestId, String sTestDesc){	
		
		createBooking("CREATE_BOOKING", "Positive test scenario creating booking for room");

		// Building request using requestSpecBuilder
		RequestSpecBuilder builder = new RequestSpecBuilder();
		Map<String,Integer> queryParam = new HashMap<String,Integer>();
		queryParam.put("roomid", roomId);

		//Setting content type as application/json
		builder.setContentType("application/json");
		builder.addHeader("Accept", "application/json");

		//Creating one more booking for same room with different dates
		if(sTestId.equalsIgnoreCase("GET_ROOM_BOOKING_TWICE")){
			BookingRequest createBooking = helper.createBookingObj(bookingId,roomId);
			createBooking.getBookingdates().setCheckin("2020-02-10");
			createBooking.getBookingdates().setCheckout("2020-02-12");
			RequestSpecBuilder builderRequest = new RequestSpecBuilder();

			//Setting content type as application/json
			builderRequest.setContentType("application/json");
			builderRequest.addHeader("Accept", "application/json");
			builderRequest.setBody(createBooking);

			RequestSpecification requestSpec = builderRequest.build();
			Response res = given().spec(requestSpec).when().post(TestConstants.BOOKING_URL);
			Assert.assertEquals(201, res.getStatusCode());
		}


		RequestSpecification requestSpec = builder.build();
		Response res = given().queryParams(queryParam).spec(requestSpec).when().get(TestConstants.BOOKING_URL);
		Assert.assertEquals(200, res.getStatusCode());
		roomBooking = res.as(RoomBookings.class);

		//Assert when room is booked once or more times
		if(roomBooking.getBookings().size() > 0) {
			try {
				Assert.assertEquals(roomBooking.getBookings().get(0).getBookingid(), bookingId);
				Assert.assertEquals(roomBooking.getBookings().get(0).getRoomid(), roomId);
			}catch(Exception e) {
				System.out.println("BookingId created not found for roomId");
			}

		}
		
		//Verifying 2 bookings are found for same room
		if(sTestId.equalsIgnoreCase("GET_ROOM_BOOKING_TWICE")){
			Assert.assertEquals(2,roomBooking.getBookings().size());
		}
	}
	
	/**
	 * <pre>
	 * Verify createBooking API
	 * </pre>
	 * <p>
	 * <pre>
	 * Test:INVALID_BOOKINGROOM_AGAIN
	 *     Steps:
	 *     1. Create Request body for booking API
	 *     2. Make booking API call.
	 *     3. Validate API status code
	 *     4. Booking same room again and Validate API status - 409 
	 *     
	 * Test:INVALID_BOOKING_CHECKOUT_LESSER_CHECKIN
	 * Steps
	 *     1. Create Request body for booking API with checkout time less than checkin time
	 *     2. Make booking API call.
	 *     3. Validate API status code - 409
	 * </pre>
	 *</p>
	 * @param sTestId       TestCase ID
	 * @param sTestDesc     TestCase Description
	 * @throws Exception
	 */

	@Test(dataProvider = "createBookingInvalid", dataProviderClass = BookingDataProvider.class, priority=4)
	public void negativeCreateBooking(String sTestId, String sTestDesc){	

		//Generating random roomIds
		int bookingId = helper.randomNumber();
		int roomId = helper.randomNumber();

		//CreateBookingRequest
		BookingRequest createBooking = helper.createBookingObj(bookingId,roomId);

		// Building request using requestSpecBuilder
		RequestSpecBuilder builder = new RequestSpecBuilder();

		//Setting content type as application/json
		builder.setContentType("application/json");
		builder.addHeader("Accept", "application/json");
		builder.setBody(createBooking);

		if(sTestId.equalsIgnoreCase("INVALID_BOOKING_CHECKOUT_LESSER_CHECKIN")){
			createBooking.getBookingdates().setCheckout("2020-02-01T17:09:41.772Z");
		}

		RequestSpecification requestSpec = builder.build();

		//Invalid Test Cases
		if(sTestId.equalsIgnoreCase("INVALID_BOOKING_CHECKOUT_LESSER_CHECKIN")){
			createBooking.getBookingdates().setCheckout("2020-02-01T17:09:41.772Z");
			builder.setBody(createBooking);
			Response res = given().spec(requestSpec).when().post(TestConstants.BOOKING_URL);
			Assert.assertEquals(409, res.getStatusCode());
		}else {
			Response res = given().spec(requestSpec).when().post(TestConstants.BOOKING_URL);
			Assert.assertEquals(201, res.getStatusCode());
			createBookingResponse = res.as(BookingResponse.class);
			bookingId = createBookingResponse.getBookingid();
			roomId = createBookingResponse.getBooking().getRoomid();
		}

		if(sTestId.equalsIgnoreCase("INVALID_BOOKINGROOM_AGAIN")){
			Response bookingAgain = given().spec(requestSpec).when().post(TestConstants.BOOKING_URL);
			Assert.assertEquals(409, bookingAgain.getStatusCode());
		}

	}

}
