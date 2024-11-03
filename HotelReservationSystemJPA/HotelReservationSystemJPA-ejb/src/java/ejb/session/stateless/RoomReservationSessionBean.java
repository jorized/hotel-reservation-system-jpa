/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entity.Reservation;
import entity.RoomReservation;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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

}
