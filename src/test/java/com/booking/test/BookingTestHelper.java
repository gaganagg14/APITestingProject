package com.booking.test;

import java.util.Random;

import com.booking.dto.BookingDatesRequest;
import com.booking.dto.BookingRequest;

public class BookingTestHelper {
	
	public BookingRequest createBookingObj(int bookingId,int roomId) {
		BookingRequest createBooking = new BookingRequest();
		createBooking.setBookingid(bookingId);
		createBooking.setFirstname("Testing");
		createBooking.setLastname("XYZ");
		createBooking.setPhone("12345678901");
		createBooking.setEmail("test@gmail.com");
		createBooking.setDepositpaid(true);
		createBooking.setRoomid(roomId);
		BookingDatesRequest bookDates= new BookingDatesRequest();
		bookDates.setCheckin("2020-02-05T16:09:41.772Z");
		bookDates.setCheckout("2020-02-05T17:09:41.772Z");
		createBooking.setBookingdates(bookDates);
		
		return createBooking;
		
	}
	
	public int randomNumber() {
		Random rand = new Random(); 
		int num = rand.nextInt(1000)*10; 
	    return num;
	}

}
