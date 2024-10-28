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
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import util.enumeration.RoomRateStatusEnum;
import util.enumeration.RoomRateTypeEnum;

/**
 *
 * @author lim_z
 */
@Entity
public class RoomRate implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomRateId;

    @Column(length = 50, nullable = false)
    private String rateName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomRateTypeEnum rateType;

    @Column(precision = 7, scale = 2, nullable = false)
    private BigDecimal ratePerNight;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomRateStatusEnum roomRateStatus;

    //All of these are optional because there will either be promotional or peak
    private Date promotionStartDate;
    private Date promotionEndDate;
    private Date peakStartDate;
    private Date peakEndDate;

    @OneToMany(mappedBy = "roomRate")
    private List<Reservation> reservations;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private RoomType roomType;

    public RoomRate() {
        this.reservations = new ArrayList<Reservation>();
    }

    public RoomRate(String rateName, RoomRateTypeEnum rateType, BigDecimal ratePerNight,
            Date promotionStartDate, Date promotionEndDate, Date peakStartDate, Date peakEndDate, RoomType roomType) {
        this();
        this.rateName = rateName;
        this.rateType = rateType;
        this.ratePerNight = ratePerNight;
        this.roomRateStatus = RoomRateStatusEnum.ACTIVE;
        this.promotionStartDate = promotionStartDate;
        this.promotionEndDate = promotionEndDate;
        this.peakStartDate = peakStartDate;
        this.peakEndDate = peakEndDate;
        this.roomType = roomType;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public String getRateName() {
        return rateName;
    }

    public void setRateName(String rateName) {
        this.rateName = rateName;
    }

    public RoomRateTypeEnum getRateType() {
        return rateType;
    }

    public void setRateType(RoomRateTypeEnum rateType) {
        this.rateType = rateType;
    }

    public BigDecimal getRatePerNight() {
        return ratePerNight;
    }

    public void setRatePerNight(BigDecimal ratePerNight) {
        this.ratePerNight = ratePerNight;
    }

    public RoomRateStatusEnum getRoomRateStatus() {
        return roomRateStatus;
    }

    public void setRoomRateStatus(RoomRateStatusEnum roomRateStatus) {
        this.roomRateStatus = roomRateStatus;
    }

    public Date getPromotionStartDate() {
        return promotionStartDate;
    }

    public void setPromotionStartDate(Date promotionStartDate) {
        this.promotionStartDate = promotionStartDate;
    }

    public Date getPromotionEndDate() {
        return promotionEndDate;
    }

    public void setPromotionEndDate(Date promotionEndDate) {
        this.promotionEndDate = promotionEndDate;
    }

    public Date getPeakStartDate() {
        return peakStartDate;
    }

    public void setPeakStartDate(Date peakStartDate) {
        this.peakStartDate = peakStartDate;
    }

    public Date getPeakEndDate() {
        return peakEndDate;
    }

    public void setPeakEndDate(Date peakEndDate) {
        this.peakEndDate = peakEndDate;
    }

    public Long getRoomRateId() {
        return roomRateId;
    }

    public void setRoomRateId(Long roomRateId) {
        this.roomRateId = roomRateId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (roomRateId != null ? roomRateId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the roomRateId fields are not set
        if (!(object instanceof RoomRate)) {
            return false;
        }
        RoomRate other = (RoomRate) object;
        if ((this.roomRateId == null && other.roomRateId != null) || (this.roomRateId != null && !this.roomRateId.equals(other.roomRateId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.RoomRate[ id=" + roomRateId + " ]";
    }

}
