package com.pedrorenzo.booking.repositories;

import com.pedrorenzo.booking.entities.Booking;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends MongoRepository<Booking, String> {

}
