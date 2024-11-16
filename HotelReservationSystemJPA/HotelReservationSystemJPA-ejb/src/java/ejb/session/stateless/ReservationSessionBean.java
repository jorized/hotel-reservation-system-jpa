/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entity.Guest;
import entity.Partner;
import entity.Reservation;
import entity.RoomRate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.InvalidReservationIdException;

/**
 *
 * @author JorJo
 */
@Stateless
public class ReservationSessionBean implements ReservationSessionBeanRemote, ReservationSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystemJPA-ejbPU")
    private EntityManager em;

    @Override
    public Reservation createNewReservation(Reservation newReservation) {
        em.persist(newReservation);
        em.flush();

        return newReservation;
    }
    
    @Override
    public List<Reservation> retrieveAllReservations() {
        Query query = em.createQuery("SELECT r FROM Reservation r");

        return query.getResultList();

    }

    @Override
    public List<RoomRate> retrieveAllReservationsByRoomRate(RoomRate roomRate) {
        return em.createQuery("SELECT r FROM Reservation r WHERE r.roomRate = :roomRate", RoomRate.class)
                .setParameter("roomRate", roomRate)
                .getResultList();
    }

    @Override
    public boolean isSameDayCheckIn(Date checkInDate, Date currentDate) {
        Calendar checkInCal = Calendar.getInstance();
        checkInCal.setTime(checkInDate);

        Calendar currentCal = Calendar.getInstance();
        currentCal.setTime(currentDate);

        return checkInCal.get(Calendar.YEAR) == currentCal.get(Calendar.YEAR)
                && checkInCal.get(Calendar.DAY_OF_YEAR) == currentCal.get(Calendar.DAY_OF_YEAR);
    }
    
    @Override
    public Reservation getReservationByReservationId(Long reservationId) throws InvalidReservationIdException {
        try {
            return em.createQuery("SELECT r FROM Reservation r WHERE r.reservationId = :reservationId", Reservation.class)
                .setParameter("reservationId", reservationId)
                .getSingleResult();
        } catch (NoResultException ex) {
            throw new InvalidReservationIdException("Invalid reservation ID");
        }

    }
    
    @Override
    public Reservation updateReservation(Reservation updatedReservation) throws InvalidReservationIdException {
        try {

            return em.merge(updatedReservation);

        } catch (Exception ex) {
            throw new InvalidReservationIdException("Unable to update reservation: " + ex.getMessage());
        }
    }

    @Override
    public List<Reservation> retrieveAllReservationsByGuest(Guest guest) {
        return em.createQuery("SELECT r FROM Reservation r WHERE r.guest = :guest", Reservation.class)
                 .setParameter("guest", guest)
                 .getResultList();
    }
    
    @Override
    public List<Reservation> retrieveAllReservationsByPartner(Partner partner) {
        return em.createQuery("SELECT r FROM Reservation r WHERE r.partner = :partner", Reservation.class)
                 .setParameter("partner", partner)
                 .getResultList();
    }        

}
