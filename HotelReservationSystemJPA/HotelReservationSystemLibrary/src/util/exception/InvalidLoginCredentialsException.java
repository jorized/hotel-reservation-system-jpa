/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util.exception;

/**
 *
 * @author JorJo
 */
public class InvalidLoginCredentialsException extends Exception {
    
    public InvalidLoginCredentialsException() {
    }
    
    public InvalidLoginCredentialsException(String msg) {
        super(msg);
    }
}
