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
import javax.persistence.OneToOne;
import util.enumeration.ExceptionTypeReportEnum;

/**
 *
 * @author lim_z
 */
@Entity
public class ExceptionReport implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;    
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExceptionTypeReportEnum exceptionTypeReport;
    
    @OneToOne
    @JoinColumn
    private RoomReservation roomReservation;

    public ExceptionReport() {
    }

    public ExceptionReport(ExceptionTypeReportEnum exceptionTypeReport, RoomReservation roomReservation) {
        this.exceptionTypeReport = exceptionTypeReport;
        this.roomReservation = roomReservation;
    }

    public RoomReservation getRoomReservation() {
        return roomReservation;
    }

    public void setRoomReservation(RoomReservation roomReservation) {
        this.roomReservation = roomReservation;
    }        

    public ExceptionTypeReportEnum getExceptionTypeReport() {
        return exceptionTypeReport;
    }

    public void setExceptionTypeReport(ExceptionTypeReportEnum exceptionTypeReport) {
        this.exceptionTypeReport = exceptionTypeReport;
    }
       
    public Long getReportId() {
        return reportId;
    }

    public void setReportId(Long reportId) {
        this.reportId = reportId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (reportId != null ? reportId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the reportId fields are not set
        if (!(object instanceof ExceptionReport)) {
            return false;
        }
        ExceptionReport other = (ExceptionReport) object;
        if ((this.reportId == null && other.reportId != null) || (this.reportId != null && !this.reportId.equals(other.reportId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.ExceptionReport[ id=" + reportId + " ]";
    }
    
}
