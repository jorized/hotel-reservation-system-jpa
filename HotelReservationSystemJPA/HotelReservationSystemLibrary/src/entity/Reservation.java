/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import util.enumeration.ReservationStatusEnum;
import util.enumeration.ReservationTypeEnum;

/**
 *
 * @author JorJo
 */
@Entity
public class Reservation implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationId;
    
    @Column(nullable = false)
    private Date checkInDate;
    
    @Column(nullable = false)
    private Date checkOutDate;
    
    @Column(nullable = false)
    private Integer numOfRooms;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationTypeEnum reservationType;
    
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal reservationAmount;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatusEnum reservationStatusEnum;
    
    @ManyToOne
    @JoinColumn
    private Guest guest;
    
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private RoomType roomType;
    
    @ManyToOne
    @JoinColumn
    private Partner partner;

    public Reservation() {
    }

    public Reservation(Date checkInDate, Date checkOutDate, Integer numOfRooms, ReservationTypeEnum reservationType, BigDecimal reservationAmount, ReservationStatusEnum reservationStatusEnum, Guest guest, RoomType roomType, Partner partner) {
        this();
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.numOfRooms = numOfRooms;
        this.reservationType = reservationType;
        this.reservationStatusEnum = reservationStatusEnum;
        this.reservationAmount = reservationAmount;
        this.guest = guest;
        this.roomType = roomType;
        this.partner = partner;
    }
    
    public ReservationStatusEnum getReservationStatusEnum() {
        return reservationStatusEnum;
    }

    public void setReservationStatusEnum(ReservationStatusEnum reservationStatusEnum) {
        this.reservationStatusEnum = reservationStatusEnum;
    }
    

    public Integer getNumOfRooms() {
        return numOfRooms;
    }

    public void setNumOfRooms(Integer numOfRooms) {
        this.numOfRooms = numOfRooms;
    }        
    
    public Guest getGuest() {
        return guest;
    }

    public void setGuest(Guest guest) {
        this.guest = guest;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public Partner getPartner() {
        return partner;
    }

    public void setPartner(Partner partner) {
        this.partner = partner;
    }                

    public ReservationTypeEnum getReservationType() {
        return reservationType;
    }

    public void setReservationType(ReservationTypeEnum reservationType) {
        this.reservationType = reservationType;
    }

    public BigDecimal getReservationAmount() {
        return reservationAmount;
    }

    public void setReservationAmount(BigDecimal reservationAmount) {
        this.reservationAmount = reservationAmount;
    }        

    public Date getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(Date checkInDate) {
        this.checkInDate = checkInDate;
    }

    public Date getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(Date checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (reservationId != null ? reservationId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the reservationId fields are not set
        if (!(object instanceof Reservation)) {
            return false;
        }
        Reservation other = (Reservation) object;
        if ((this.reservationId == null && other.reservationId != null) || (this.reservationId != null && !this.reservationId.equals(other.reservationId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Reservation[ id=" + reservationId + " ]";
    }
    
}
