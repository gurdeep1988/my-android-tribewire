package com.eyesayapp.Utils;

public class InValidResponseException extends Exception {

   /**
    * 
    */
   private static final long serialVersionUID = -8867226626553819366L;
   String message = "Invalid Response :";
   String response = "";

   public InValidResponseException(String res) {
      this.response = res;
   }

   @Override
   public String getMessage() {

      return message + ":" + response;
   }

   @Override
   public String toString() {

      return message + ":" + response;
   }

}
