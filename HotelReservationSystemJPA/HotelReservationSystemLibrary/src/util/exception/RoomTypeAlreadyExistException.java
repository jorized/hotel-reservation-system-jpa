/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util.exception;

/**
 *
 * @author JorJo
 */
public class RoomTypeAlreadyExistException extends Exception {

    public RoomTypeAlreadyExistException() {
    }
    
    public RoomTypeAlreadyExistException(String msg) {
        super(msg);
    }
}
