package takeABreak.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import takeABreak.exceptions.BadRequestException;

public class AbstractController {

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBadRequest(BadRequestException e){
        //todo Json Object for a return whit msg
        return e.getMessage();
    }
}
