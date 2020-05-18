package io.ao9.flow.api.user.shared;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import feign.Response;
import feign.codec.ErrorDecoder;

@Component
public class FeignErrorDecoder implements ErrorDecoder {

    private Environment env;

    @Autowired
    public FeignErrorDecoder(Environment env) {
        this.env = env;
    }

    @Override
    public Exception decode(String methodKey, Response response) {
        switch (response.status()) {
            case 400:
                // Do something
                // return new BadRequestException();
                break;
            case 404: {
                if (methodKey.contains("findAlbums")) {
                    return new ResponseStatusException(
                        HttpStatus.valueOf(response.status()), 
                        env.getProperty("albums.exceptions.albums-not-found")
                    );
                }
                break;
            }
            default:
                return new Exception(response.reason());
            }
            return null;
    }

}