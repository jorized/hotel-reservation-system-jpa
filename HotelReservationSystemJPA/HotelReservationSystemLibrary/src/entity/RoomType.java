/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
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
    
    @Column(length = 50, nullable = false)
    private String typeName;
    
    @Column(length = 50, nullable = false)
    private String size;
    
    @Column(length = 12, nullable = false) 
    private String bed;
    
    @Column(nullable = false)
    private String description;
    
    @Column(nullable = false)
    private Integer capacity;    
    
    @Column(nullable = false)
    private String amenities;
    
    @Column(nullable = false)
    private Integer tierNumber;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomTypeStatusEnum roomTypeStatus;
    
//    @OneToMany
//    @JoinColumn(nullable = false)
//    private RoomRate roomRate;

    public RoomType() {
    }

    public RoomType(String typeName, String size, String bed, String description, Integer capacity, String amenities, Integer tierNumber) {
        this();
        this.typeName = typeName;
        this.size = size;
        this.bed = bed;
        this.description = description;
        this.capacity = capacity;
        this.amenities = amenities;
        this.tierNumber = tierNumber;
        this.roomTypeStatus = RoomTypeStatusEnum.ACTIVE;
    }    

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Integer getTierNumber() {
        return tierNumber;
    }

    public void setTierNumber(Integer tierNumber) {
        this.tierNumber = tierNumber;
    }                    

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
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
