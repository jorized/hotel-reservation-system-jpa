/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entity.RoomRate;
import entity.RoomRateReservation;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author JorJo
 */
@Stateless
public class RoomRateReservationSessionBean implements RoomRateReservationSessionBeanRemote, RoomRateReservationSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystemJPA-ejbPU")
    private EntityManager em;
    
    @Override
    public RoomRateReservation createNewRoomRateReservation(RoomRateReservation newRoomRateReservation) {

        em.persist(newRoomRateReservation);
        em.flush();

        return newRoomRateReservation;
    }
    
    @Override
    public List<RoomRateReservation> retrieveRoomRateReservationsByRoomRate(RoomRate roomRate) {
        return em.createQuery("SELECT rrr FROM RoomRateReservation rrr WHERE rrr.roomRate.roomRateId = :roomRateId", RoomRateReservation.class)
                .setParameter("roomRateId", roomRate.getRoomRateId())
                .getResultList();
    }
    
    
}
