package takeABreak.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import takeABreak.exceptions.*;

public class AbstractController {

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBadRequest(BadRequestException e){
        //todo Json Object for a return whit msg
        return e.getMessage();
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleBadRequest(AuthenticationException e){
        //todo Json Object for a return whit msg
        return e.getMessage();
    }

    @ExceptionHandler(NotAuthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String handleBadRequest(NotAuthorizedException e){
        //todo Json Object for a return whit msg
        return e.getMessage();
    }
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleBadRequest(NotFoundException e){
        //todo Json Object for a return whit msg
        return e.getMessage();
    }

    @ExceptionHandler(InitException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleInitException(InitException e){
        //todo Json Object for a return whit msg
        return e.getMessage();
    }
}
