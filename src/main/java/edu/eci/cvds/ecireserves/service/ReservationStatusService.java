package edu.eci.cvds.ecireserves.service;

import java.time.LocalTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import edu.eci.cvds.ecireserves.enums.ReservationStatus;
import edu.eci.cvds.ecireserves.exception.EciReservesException;
import edu.eci.cvds.ecireserves.model.Laboratory;
import edu.eci.cvds.ecireserves.model.Reservation;
import edu.eci.cvds.ecireserves.repository.LaboratoryRepository;
import edu.eci.cvds.ecireserves.repository.ReservationRepository;

@Service
public class ReservationStatusService {

    private final ReservationRepository reservationRepository;
    private final LaboratoryRepository laboratoryRepository;

    public ReservationStatusService(ReservationRepository reservationRepository, LaboratoryRepository laboratoryRepository) {
        this.reservationRepository = reservationRepository;
        this.laboratoryRepository = laboratoryRepository;
    }

    /**
     * This method is used to change the status of the reservations that have already expired
     * and update the status of the laboratory
     * @throws EciReservesException
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void cleanupReservations() throws EciReservesException {
        LocalTime now = LocalTime.now();
        List<Reservation> expired = reservationRepository.findByEndTimeBeforeAndStatus(now, ReservationStatus.AGENDADA);
        if(!expired.isEmpty()) {
            for(Reservation reservation : expired) {
                reservation.setStatus(ReservationStatus.FINALIZADA);
                Laboratory lab = laboratoryRepository.findById(reservation.getLaboratoryId())
                        .orElseThrow(() -> new EciReservesException(EciReservesException.LABORATORY_NOT_FOUND));
                lab.removeTimeSlot(reservation.getDate(), reservation.getStartTime(), reservation.getEndTime());
                laboratoryRepository.save(lab);
                reservationRepository.save(reservation);
            }
        }
    } 
}

