/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
    private String rateName;
    private RoomRateTypeEnum rateType;
    private BigDecimal ratePerNight;
    private RoomRateStatusEnum roomRateStatus;
    private Date promotionStartDate;
    private Date promotionEndDate;
    private Date peakStartDate;
    private Date peakEndDate;
    
    @ManyToOne(optional = false)
    @JoinColumn
    private Reservation reservation;
    
    @ManyToOne(optional = false)
    @JoinColumn
    private RoomType roomType;

    public RoomRate() {
    }

    public RoomRate(String rateName, RoomRateTypeEnum rateType, BigDecimal ratePerNight, RoomRateStatusEnum roomRateStatus,
            Date promotionStartDate, Date promotionEndDate, Date peakStartDate, Date peakEndDate) {

        this.rateName = rateName;
        this.rateType = rateType;
        this.ratePerNight = ratePerNight;
        this.roomRateStatus = roomRateStatus;

        // Handle Promotion Case
        if (promotionStartDate != null && promotionEndDate != null) {
            this.promotionStartDate = promotionStartDate;
            this.promotionEndDate = promotionEndDate;
        }

        // Handle Peak Case
        if (peakStartDate != null && peakEndDate != null) {
            this.peakStartDate = peakStartDate;
            this.peakEndDate = peakEndDate;
        }
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
