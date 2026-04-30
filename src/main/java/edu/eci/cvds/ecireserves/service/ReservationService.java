package edu.eci.cvds.ecireserves.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;

import edu.eci.cvds.ecireserves.dto.ReservationDTO;
import edu.eci.cvds.ecireserves.enums.ReservationStatus;
import edu.eci.cvds.ecireserves.exception.EciReservesException;
import edu.eci.cvds.ecireserves.model.Laboratory;
import edu.eci.cvds.ecireserves.model.Reservation;
import edu.eci.cvds.ecireserves.repository.LaboratoryRepository;
import edu.eci.cvds.ecireserves.repository.ReservationRepository;

@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final LaboratoryRepository laboratoryRepository;

    public ReservationService(ReservationRepository reservationRepository, LaboratoryRepository laboratoryRepository) {
        this.reservationRepository = reservationRepository;
        this.laboratoryRepository = laboratoryRepository;
    }

    /**
     * Get all reservations
     * @return List of reservations
     */
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    /**
     * Get reservation by id
     * @param id Reservation id
     * @return Reservation
     * @throws EciReservesException
     */
    public Reservation getReservationById(String id) throws EciReservesException {
        return reservationRepository.findById(id).orElseThrow(() -> new EciReservesException(EciReservesException.RESERVATION_NOT_FOUND));
    }

    /**
     * Get reservations by user id
     * @param userId User id
     * @return List of reservations
     */
    public List<Reservation> getReservationsByUserId(String userId) {
        return reservationRepository.findByUserId(userId);
    }

    /**
     * Get reservations by laboratory id
     * @param laboratoryId Laboratory id
     * @return List of reservations
     */
    public List<Reservation> getReservationsByLaboratoryId(String laboratoryId) {
        return reservationRepository.findByLaboratoryId(laboratoryId);
    }

    /**
     * Get reservations by status
     * @param status Reservation status
     * @return List of reservations
     */
    public List<Reservation> getReservationsByStatus(ReservationStatus status) {
        return reservationRepository.findByStatus(status);
    }

    /**
     * Get reservations by user id and status
     * @param userId User id
     * @param status Reservation status
     * @return List of reservations
     */
    public List<Reservation> getReservationsByUserIdAndStatus(String userId, ReservationStatus status) {
        return reservationRepository.findByUserIdAndStatus(userId, status);
    }

    /**
     * Get reservations by date
     * @param date Reservation date
     * @return List of reservations
     */
    public List<Reservation> getReservationsByDate(LocalDate date) {
        return reservationRepository.findByDate(date);
    }

    /**
     * Get reservations by start time
     * @param startTime Reservation start time
     * @return List of reservations
     */
    public List<Reservation> getReservationsByStartTime(LocalTime startTime) {
        return reservationRepository.findByStartTime(startTime);
    }

    /**
     * Get reservations by duration
     * @param duration Reservation duration
     * @return List of reservations
     */
    public List<Reservation> getReservationsByDuration(Integer duration) {
        return reservationRepository.findByDuration(duration);
    }

    /**
     * Create a new reservation
     * @param reservationDTO Reservation data
     * @return Reservation
     * @throws EciReservesException
     */
    public Reservation createReservation(ReservationDTO reservationDTO) throws EciReservesException{
        Laboratory laboratory = laboratoryRepository.findById(reservationDTO.getLaboratoryId()).orElseThrow(() -> new EciReservesException(EciReservesException.LABORATORY_NOT_FOUND));
        laboratory.addTimeSlot(reservationDTO.getDate(), reservationDTO.getStartTime(), reservationDTO.getStartTime().plusMinutes(reservationDTO.getDuration()));
        laboratoryRepository.save(laboratory);
        Reservation reservation = new Reservation();
        reservation.setUserId(reservationDTO.getUserId());
        reservation.setLaboratoryId(reservationDTO.getLaboratoryId());
        reservation.setDate(reservationDTO.getDate());
        reservation.setStartTime(reservationDTO.getStartTime());
        reservation.setEndTime(reservationDTO.getStartTime().plusMinutes(reservationDTO.getDuration()));
        reservation.setDuration(reservationDTO.getDuration());
        reservation.setPurpose(reservationDTO.getPurpose());
        reservation.setStatus(ReservationStatus.AGENDADA);
        return reservationRepository.save(reservation);
    }

    /**
     * Update reservation
     * @param id Reservation id
     * @param reservationDTO Reservation data
     * @return Reservation
     * @throws EciReservesException
     */
    public Reservation updateReservation(String id, ReservationDTO reservationDTO) throws EciReservesException {
        Reservation reservation = reservationRepository.findById(id).orElseThrow(() -> new EciReservesException(EciReservesException.RESERVATION_NOT_FOUND));
        Laboratory laboratory = laboratoryRepository.findById(reservationDTO.getLaboratoryId()).orElseThrow(() -> new EciReservesException(EciReservesException.LABORATORY_NOT_FOUND));
        reservation.setStatus(ReservationStatus.PENDIENTE);
        if(reservationDTO.getNewStartTime() != null && reservationDTO.getNewDuration() != null){
            laboratory.removeTimeSlot(reservationDTO.getDate(), reservation.getStartTime(), reservation.getStartTime().plusMinutes(reservation.getDuration()), reservationDTO.getNewStartTime(), reservationDTO.getNewStartTime().plusMinutes(reservationDTO.getNewDuration()));
            reservation.setStartTime(reservationDTO.getNewStartTime());
            reservation.setDuration(reservationDTO.getNewDuration());
            reservation.setEndTime(reservationDTO.getNewStartTime().plusMinutes(reservationDTO.getNewDuration()));
            laboratoryRepository.save(laboratory);
        }
        if(reservationDTO.getLaboratoryId() != null) reservation.setLaboratoryId(reservationDTO.getLaboratoryId());
        if(reservationDTO.getDate() != null) reservation.setDate(reservationDTO.getDate());
        if(reservationDTO.getPurpose() != null) reservation.setPurpose(reservationDTO.getPurpose());
        reservation.setStatus(ReservationStatus.AGENDADA);
        return reservationRepository.save(reservation);
    }

    /**
     * Delete reservation
     * @param id Reservation id
     * @throws EciReservesException
     */
    public void deleteReservation(String id) throws EciReservesException {
        Reservation reservation = reservationRepository.findById(id).orElseThrow(() -> new EciReservesException(EciReservesException.RESERVATION_NOT_FOUND));
        reservation.setStatus(ReservationStatus.CANCELADA);
        Laboratory laboratory = laboratoryRepository.findById(reservation.getLaboratoryId()).orElseThrow(() -> new EciReservesException(EciReservesException.LABORATORY_NOT_FOUND));
        laboratory.removeTimeSlot(reservation.getDate(), reservation.getStartTime(), reservation.getStartTime().plusMinutes(reservation.getDuration()));
        laboratoryRepository.save(laboratory);

        reservationRepository.deleteById(id);
    }
}
