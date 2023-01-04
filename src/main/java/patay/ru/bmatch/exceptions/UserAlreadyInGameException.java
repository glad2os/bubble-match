package patay.ru.bmatch.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class UserAlreadyInGameException extends RuntimeException{
    public UserAlreadyInGameException(String message){
        super(message);
    }
}

