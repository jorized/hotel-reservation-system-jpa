/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import util.enumeration.RoomTypeNameEnum;
import util.enumeration.RoomTypeStatusEnum;

/**
 *
 * @author lim_z
 */
@Entity
public class RoomType implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomTypeId;
    private RoomTypeNameEnum typeName;
    private String size;
    private String bed;
    private String description;
    private BigDecimal numOfAvailRooms;
    private BigDecimal capacity;
    private String amenities;
    private RoomTypeStatusEnum roomTypeStatus;
    
    @OneToMany(mappedBy="roomType")
    private List<RoomRate> roomRates;
    
    @OneToMany(mappedBy="roomType")
    private List<Reservation> reservations;
    
    @ManyToOne(optional = false)
    @JoinColumn
    private Room room;

    public RoomType() {
        this.roomRates = new ArrayList<RoomRate>();
        this.reservations = new ArrayList<Reservation>();
    }
    
    public RoomTypeNameEnum getTypeName() {
        return typeName;
    }

    public void setTypeName(RoomTypeNameEnum typeName) {
        this.typeName = typeName;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getBed() {
        return bed;
    }

    public void setBed(String bed) {
        this.bed = bed;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getNumOfAvailRooms() {
        return numOfAvailRooms;
    }

    public void setNumOfAvailRooms(BigDecimal numOfAvailRooms) {
        this.numOfAvailRooms = numOfAvailRooms;
    }

    public BigDecimal getCapacity() {
        return capacity;
    }

    public void setCapacity(BigDecimal capacity) {
        this.capacity = capacity;
    }

    public String getAmenities() {
        return amenities;
    }

    public void setAmenities(String amenities) {
        this.amenities = amenities;
    }

    public RoomTypeStatusEnum getRoomTypeStatus() {
        return roomTypeStatus;
    }

    public void setRoomTypeStatus(RoomTypeStatusEnum roomTypeStatus) {
        this.roomTypeStatus = roomTypeStatus;
    }
    
    

    public Long getRoomTypeId() {
        return roomTypeId;
    }

    public void setRoomTypeId(Long roomTypeId) {
        this.roomTypeId = roomTypeId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (roomTypeId != null ? roomTypeId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the roomTypeId fields are not set
        if (!(object instanceof RoomType)) {
            return false;
        }
        RoomType other = (RoomType) object;
        if ((this.roomTypeId == null && other.roomTypeId != null) || (this.roomTypeId != null && !this.roomTypeId.equals(other.roomTypeId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.RoomType[ id=" + roomTypeId + " ]";
    }
    
}
