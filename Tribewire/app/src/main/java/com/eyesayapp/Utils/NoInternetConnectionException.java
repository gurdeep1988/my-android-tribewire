package com.eyesayapp.Utils;

public class NoInternetConnectionException extends Exception {

   /**
    * 
    */
   private static final long serialVersionUID = -8867226626553819366L;
   String message = "No internet Connection";

   public NoInternetConnectionException() {

   }

   @Override
   public String getMessage() {

      return message;
   }

   @Override
   public String toString() {

      return message;
   }

}
