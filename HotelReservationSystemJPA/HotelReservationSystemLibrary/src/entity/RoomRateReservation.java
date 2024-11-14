/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 *
 * @author JorJo
 */
@Entity
public class RoomRateReservation implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomRateReservationId;
    
    @ManyToOne
    @JoinColumn
    private RoomRate roomRate;
    
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private Reservation reservation;       

    public RoomRateReservation() {
    }

    public RoomRateReservation(RoomRate roomRate, Reservation reservation) {
        this.roomRate = roomRate;
        this.reservation = reservation;
    }        

    public RoomRate getRoomRate() {
        return roomRate;
    }

    public void setRoomRate(RoomRate roomRate) {
        this.roomRate = roomRate;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }
    
    
        
    public Long getRoomRateReservationId() {
        return roomRateReservationId;
    }

    public void setRoomRateReservationId(Long roomRateReservationId) {
        this.roomRateReservationId = roomRateReservationId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (roomRateReservationId != null ? roomRateReservationId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the roomRateReservationId fields are not set
        if (!(object instanceof RoomRateReservation)) {
            return false;
        }
        RoomRateReservation other = (RoomRateReservation) object;
        if ((this.roomRateReservationId == null && other.roomRateReservationId != null) || (this.roomRateReservationId != null && !this.roomRateReservationId.equals(other.roomRateReservationId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.RoomRateReservation[ id=" + roomRateReservationId + " ]";
    }
    
}
