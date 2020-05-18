package io.ao9.flow.api.user.data;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import feign.FeignException;
import feign.hystrix.FallbackFactory;
import io.ao9.flow.api.user.ui.model.AlbumResponseModel;

@FeignClient(
    name = "album-api",
    fallbackFactory = AlbumFallbackFactory.class
)
public interface AlbumFeignClient {

    // method name can be different from album app
    @GetMapping("/users/{userId}/albums")
    List<AlbumResponseModel> findAlbumsByUserId(@PathVariable String userId);

}

@Component
class AlbumFallbackFactory implements FallbackFactory<AlbumFeignClient> {

    @Override
    public AlbumFeignClient create(Throwable cause) {
        return new AlbumFeignClientFallback(cause);
    }
    
}

class AlbumFeignClientFallback implements AlbumFeignClient {

    private final Throwable cause;
    Logger logger = LoggerFactory.getLogger(this.getClass());

    public AlbumFeignClientFallback(Throwable cause) {
        this.cause = cause;
    }

    @Override
    public List<AlbumResponseModel> findAlbumsByUserId(String userId) { 
        if(cause instanceof FeignException && ((FeignException) cause).status() == 404) {
            logger.error("404 error when findAlbumsByUserId called with userID " + userId + ". Error message: " + cause.getLocalizedMessage());
        } else {
            logger.error("Error when findAlbumsByUserId called with userID " + userId + ". Error message: " + cause.getLocalizedMessage());
        }
        
        List<AlbumResponseModel> albums = new ArrayList<>();

        AlbumResponseModel albumResponseModel = new AlbumResponseModel();
        albumResponseModel.setUserId(userId);
        albumResponseModel.setAlbumId("fallback albumId");
        albumResponseModel.setDescription("fallback album description");
        albumResponseModel.setTitle("fallback album title");
        albums.add(albumResponseModel);

        return albums;
    }

}