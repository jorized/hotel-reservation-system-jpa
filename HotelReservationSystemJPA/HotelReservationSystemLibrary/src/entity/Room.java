/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import util.enumeration.RoomStatusEnum;

/**
 *
 * @author lim_z
 */
@Entity
public class Room implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;
    private RoomStatusEnum roomStatus;
    private String floorNum;
    private String roomNum;
    
    @OneToMany(mappedBy = "room")
    private List<RoomType> roomTypes;
    
    @OneToMany(mappedBy = "room")
    private List<RoomReservation> roomReservations;

    public Room() {
        this.roomTypes = new ArrayList<RoomType>();
        this.roomReservations = new ArrayList<RoomReservation>();
    }

    public Room(String floorNum, String roomNum) {
        this();
        this.floorNum = floorNum;
        this.roomNum = roomNum;
    }
    
    

    public RoomStatusEnum getRoomStatus() {
        return roomStatus;
    }

    public void setRoomStatus(RoomStatusEnum roomStatus) {
        this.roomStatus = roomStatus;
    }

    public String getFloorNum() {
        return floorNum;
    }

    public void setFloorNum(String floorNum) {
        this.floorNum = floorNum;
    }

    public String getRoomNum() {
        return roomNum;
    }

    public void setRoomNum(String roomNum) {
        this.roomNum = roomNum;
    }
    
    

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (roomId != null ? roomId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the roomId fields are not set
        if (!(object instanceof Room)) {
            return false;
        }
        Room other = (Room) object;
        if ((this.roomId == null && other.roomId != null) || (this.roomId != null && !this.roomId.equals(other.roomId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Room[ id=" + roomId + " ]";
    }
    
}
