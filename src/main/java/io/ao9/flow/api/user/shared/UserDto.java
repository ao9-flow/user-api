package io.ao9.flow.api.user.shared;

import java.io.Serializable;
import java.util.List;

import io.ao9.flow.api.user.ui.model.AlbumResponseModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto implements Serializable{

    private static final long serialVersionUID = 7845086189181196219L;

    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String encryptedPassword;
    private List<AlbumResponseModel> albums;
    
}
