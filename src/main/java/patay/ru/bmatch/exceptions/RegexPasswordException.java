package patay.ru.bmatch.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.I_AM_A_TEAPOT)
public class RegexPasswordException extends RuntimeException{
    public RegexPasswordException(String message){
        super(message);
    }
}

