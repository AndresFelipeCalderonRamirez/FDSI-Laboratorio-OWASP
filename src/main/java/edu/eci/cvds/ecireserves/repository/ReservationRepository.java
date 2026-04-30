package edu.eci.cvds.ecireserves.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import edu.eci.cvds.ecireserves.enums.ReservationStatus;
import edu.eci.cvds.ecireserves.model.Reservation;


@Repository
public interface ReservationRepository extends MongoRepository<Reservation, String> {
    List<Reservation> findByUserId(String userId);
    List<Reservation> findByLaboratoryId(String laboratoryId);
    List<Reservation> findByStatus(ReservationStatus status);
    List<Reservation> findByUserIdAndStatus(String userId, ReservationStatus status);
    List<Reservation> findByDate(LocalDate date);
    List<Reservation> findByStartTime(LocalTime startTime);
    List<Reservation> findByDuration(Integer duration);
    List<Reservation> findByEndTimeBeforeAndStatus(LocalTime endTime, ReservationStatus status);
}