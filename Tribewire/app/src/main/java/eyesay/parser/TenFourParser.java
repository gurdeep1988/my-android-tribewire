package eyesay.parser;

public abstract class TenFourParser {
   protected boolean isError;
   protected String errorMessage;

   public TenFourParser() {
      isError = false;
      errorMessage = "";
   }

   protected boolean isError() {
      return isError;
   }

   public String getErrorMessage() {
      return errorMessage;
   }

   protected void parceForError(String xmlResponse) {
      try {
         if (!getTag(xmlResponse, "status").equals("1")) {
            isError = true;
            errorMessage = getTag(xmlResponse, "message");
         } else {
            isError = false;
            errorMessage = "";
         }
      } catch (ArrayIndexOutOfBoundsException e) {
         isError = true;
         errorMessage = "Server Problem.Please try again after some time.";
      } catch (NullPointerException e) {
         isError = true;
         errorMessage = "Server Problem.Please try again after some time.";
      }
   }

   protected String getTag(String XMLString, String tag) {
      String startTag = "<" + tag + ">";
      String endTag = "</" + tag + ">";
      return XMLString.substring(XMLString.indexOf(startTag) + startTag.length(), XMLString.indexOf(endTag)).trim();
   }

   public abstract void parceXML(String XMLResponse);
}
