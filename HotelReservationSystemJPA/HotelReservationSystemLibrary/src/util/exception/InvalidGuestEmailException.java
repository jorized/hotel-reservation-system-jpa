/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Exception.java to edit this template
 */
package util.exception;

/**
 *
 * @author lim_z
 */
public class InvalidGuestEmailException extends Exception {

    public InvalidGuestEmailException() {
    }

    public InvalidGuestEmailException(String msg) {
        super(msg);
    }
}
