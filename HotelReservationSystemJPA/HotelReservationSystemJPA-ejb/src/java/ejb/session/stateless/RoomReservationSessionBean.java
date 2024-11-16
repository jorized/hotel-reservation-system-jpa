/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entity.Reservation;
import entity.Room;
import entity.RoomReservation;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import util.exception.InvalidRoomReservationException;
import util.exception.UpdateRoomReservationException;

/**
 *
 * @author JorJo
 */
@Stateless
public class RoomReservationSessionBean implements RoomReservationSessionBeanRemote, RoomReservationSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystemJPA-ejbPU")
    private EntityManager em;
        
    @Override
    public RoomReservation createNewRoomReservation(RoomReservation newRoomReservation) {

        em.persist(newRoomReservation);
        em.flush();

        return newRoomReservation;
    }
    
    @Override
    public List<RoomReservation> retrieveRoomReservationsByReservation(Reservation reservation) {
        return em.createQuery("SELECT rr FROM RoomReservation rr WHERE rr.reservation.reservationId = :reservationId", RoomReservation.class)
                .setParameter("reservationId", reservation.getReservationId())
                .getResultList();
    }
    
    @Override
    public RoomReservation retrieveRoomReservationByRoomAndReservation(Room room, Reservation reservation) throws InvalidRoomReservationException {
        try {
            if (room == null) {
                return em.createQuery(
                        "SELECT rr FROM RoomReservation rr WHERE rr.room IS NULL AND rr.reservation.reservationId = :reservationId", 
                        RoomReservation.class)
                        .setParameter("reservationId", reservation.getReservationId())
                        .getSingleResult();
            } else {
                return em.createQuery(
                        "SELECT rr FROM RoomReservation rr WHERE rr.room.roomId = :roomId AND rr.reservation.reservationId = :reservationId", 
                        RoomReservation.class)
                        .setParameter("roomId", room.getRoomId())
                        .setParameter("reservationId", reservation.getReservationId())
                        .getSingleResult();
            }
        } catch (NoResultException ex) {
            return null;
        }
    }

    
    @Override
    public void updateRoomReservation(RoomReservation roomReservation) throws UpdateRoomReservationException {
        try {
            RoomReservation existingRoomReservation = em.find(RoomReservation.class, roomReservation.getRoomReservationId());
            if (existingRoomReservation != null) {
                
                existingRoomReservation.setRoom(roomReservation.getRoom());
                existingRoomReservation.setReservation(roomReservation.getReservation());

                em.merge(existingRoomReservation);
            } else {
                throw new EntityNotFoundException("RoomReservation with ID " + roomReservation.getRoomReservationId() + " not found.");
            }
        } catch (Exception ex) {
            throw new UpdateRoomReservationException("An error occurred while updating the RoomReservation: " + ex.getMessage());
        }
    }

}
