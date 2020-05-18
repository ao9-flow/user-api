package io.ao9.flow.api.user.ui.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.ao9.flow.api.user.service.UserService;
import io.ao9.flow.api.user.shared.UserDto;
import io.ao9.flow.api.user.ui.model.CreateUserRequestModel;
import io.ao9.flow.api.user.ui.model.CreateUserResponseModel;
import io.ao9.flow.api.user.ui.model.UserResponseModel;

@RestController
@RequestMapping("/users")
public class UserController {

    private Environment env;
    private UserService userService;
    
    @Autowired
    public UserController(Environment env, UserService userService) {
        this.env = env;
        this.userService = userService;
    }

    @GetMapping("/status")
    public String status(HttpServletRequest request) {
        return "working on port "
                + env.getProperty("local.server.port")
                + "\nip: " 
                + request.getRemoteAddr();
    }

    @GetMapping(
        value = "/{userId}",
        produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE 
        }
    )
    public ResponseEntity<UserResponseModel> findUserByUserId(@PathVariable String userId) {
        UserDto outputUserDto = userService.findUserByUserId(userId);
        UserResponseModel userResponseModel = new ModelMapper().map(outputUserDto, UserResponseModel.class);

        return ResponseEntity.status(HttpStatus.OK).body(userResponseModel);
    }

    @PostMapping(
        consumes = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE
        }, 
        produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE
        }
    )
    public ResponseEntity<CreateUserResponseModel> createUser(@Valid @RequestBody CreateUserRequestModel createUserRequestModel) {
        ModelMapper modelMapper = new ModelMapper();

        UserDto inputUserDto = modelMapper.map(createUserRequestModel, UserDto.class);
        UserDto outputUserDto = userService.createUser(inputUserDto);
        CreateUserResponseModel createUserResponseModel = modelMapper.map(outputUserDto, CreateUserResponseModel.class);

        return ResponseEntity.status(HttpStatus.CREATED).body(createUserResponseModel);
    }
    
}