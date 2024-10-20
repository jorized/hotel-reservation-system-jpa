/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
    private Date checkInDate;
    private Date checkOutDate;
    private ReservationTypeEnum reservationType;
    private BigDecimal reservationAmount;
    
    @ManyToOne(optional = false) // reservation cannot exist without being linked to User
    @JoinColumn
    private User user;
    
    @OneToMany(mappedBy = "reservation")
    private List<RoomRate> roomRates;
    
    @ManyToOne
    @JoinColumn
    private RoomType roomType;
    
    @OneToMany(mappedBy = "reservation")
    private List<RoomReservation> roomReservations;
    
    @ManyToMany
    private List<Partner> partners;

    public Reservation() {
        this.roomRates = new ArrayList<RoomRate>();
        this.roomReservations = new ArrayList<RoomReservation>();
        this.partners = new ArrayList<Partner>();
    }

    public Reservation(Date checkInDate, Date checkOutDate, ReservationTypeEnum reservationType, BigDecimal reservationAmount) {
        this();
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.reservationType = reservationType;
        this.reservationAmount = reservationAmount;
        this.user = user;
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
