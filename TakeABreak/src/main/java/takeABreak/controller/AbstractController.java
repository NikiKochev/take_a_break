package takeABreak.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import takeABreak.exceptions.*;
import takeABreak.model.dto.ExceptionResponseDTO;

public class AbstractController {
    @Autowired
    private ExceptionResponseDTO responseDTO;

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponseDTO handleBadRequest(BadRequestException e){
        return new ExceptionResponseDTO(e.getMessage());
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ExceptionResponseDTO handleBadRequest(AuthenticationException e){
        return new ExceptionResponseDTO(e.getMessage());
    }

    @ExceptionHandler(NotAuthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ExceptionResponseDTO handleBadRequest(NotAuthorizedException e){
        return new ExceptionResponseDTO(e.getMessage());
    }
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionResponseDTO handleBadRequest(NotFoundException e){
        return new ExceptionResponseDTO(e.getMessage());
    }

    @ExceptionHandler(InitException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponseDTO handleInitException(InitException e){
        return new ExceptionResponseDTO(e.getMessage());
    }

    @ExceptionHandler(InternalServerErrorException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponseDTO InternalServerErrorException(InternalServerErrorException e){
        return new ExceptionResponseDTO(e.getMessage());
    }


}
