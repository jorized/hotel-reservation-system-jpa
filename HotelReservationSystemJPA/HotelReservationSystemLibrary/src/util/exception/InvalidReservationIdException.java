/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util.exception;

/**
 *
 * @author JorJo
 */
public class InvalidReservationIdException extends Exception {

    public InvalidReservationIdException() {
    }
    
    public InvalidReservationIdException(String msg) {
        super(msg);
    }
    
}
