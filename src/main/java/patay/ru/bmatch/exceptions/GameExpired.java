package patay.ru.bmatch.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class GameExpired extends Exception{

    private static final long GameExpired = 1L;

    public GameExpired(String message){
        super(message);
    }
}

