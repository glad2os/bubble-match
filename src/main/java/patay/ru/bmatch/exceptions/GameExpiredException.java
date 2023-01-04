package patay.ru.bmatch.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class GameExpiredException extends Exception{
        public GameExpiredException(String message){
        super(message);
    }
}

