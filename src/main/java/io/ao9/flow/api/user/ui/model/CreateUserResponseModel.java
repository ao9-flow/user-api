package io.ao9.flow.api.user.ui.model;

import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@XmlRootElement
public class CreateUserResponseModel {

    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    
}