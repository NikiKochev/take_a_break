package takeABreak.exceptions;

import takeABreak.model.dto.init.InitResponseDTO;

public class InitException extends  RuntimeException{

    public InitException(String msg){
        super(msg);
    }
}
