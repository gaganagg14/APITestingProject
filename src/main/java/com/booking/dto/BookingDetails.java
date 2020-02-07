package com.booking.dto;

import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by Gagan on 02/05/2020.
 */

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookingDetails {
	public BookingDatesRequest bookingdates;
	public int bookingid;
	public boolean depositpaid;
	public String firstname;
	public String lastname;
	public int roomid;
}
