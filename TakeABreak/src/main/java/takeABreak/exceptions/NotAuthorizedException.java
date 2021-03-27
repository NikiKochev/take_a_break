package takeABreak.exceptions;

public class NotAuthorizedException extends RuntimeException{

    public NotAuthorizedException(String msg){
        super(msg);
    }
}
