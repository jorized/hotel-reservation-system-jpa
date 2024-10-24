/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;

/**
 *
 * @author JorJo
 */
@Entity
public class Customer extends Guest implements Serializable {

    @Column(length = 50, nullable = false, unique = true)
    private String username;
    
    @Column(nullable = false)
    private String password;

    public Customer() {
    }

    //Reservations is currently initialised by guest, so don't need in this constructor
    public Customer(String firstName, String lastName, String email, String phoneNumber, String passportNumber, String username, String password) {
        super(firstName, lastName, email, phoneNumber, passportNumber);
        this.username = username;
        this.password = password;
    }    
    

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }        
    

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        return super.equals(object);
    }

    @Override
    public String toString() {
        return super.toString();
    }
    
}
