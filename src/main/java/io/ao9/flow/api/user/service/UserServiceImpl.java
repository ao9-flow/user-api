package io.ao9.flow.api.user.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import io.ao9.flow.api.user.data.AlbumFeignClient;
import io.ao9.flow.api.user.data.UserEntity;
import io.ao9.flow.api.user.data.UserRepository;
import io.ao9.flow.api.user.shared.UserDto;
import io.ao9.flow.api.user.ui.model.AlbumResponseModel;

@Service
public class UserServiceImpl implements UserService {

    // private Environment env;
    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    // private RestTemplate restTemplate;
    private AlbumFeignClient albumFeignClient;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, AlbumFeignClient albumFeignClient) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.albumFeignClient = albumFeignClient;
    }

    @Override
    public UserDto findUserByUserId(String userId) {
        UserEntity userEntity = userRepository.findByUserId(userId);
        if(userEntity == null) throw new UsernameNotFoundException(userId);

        UserDto outputUserDto = new ModelMapper().map(userEntity, UserDto.class);

        // String albumsUrl = String.format(env.getProperty("albums.url.path") , userId);
        // ResponseEntity<List<AlbumResponseModel>> albumsResponse = restTemplate.exchange(albumsUrl, HttpMethod.GET, null, new ParameterizedTypeReference<List<AlbumResponseModel>>() {
        // });
        // List<AlbumResponseModel> albums = albumsResponse.getBody();
        logger.info("====before calling album microservice=======");
        List<AlbumResponseModel> albums = albumFeignClient.findAlbumsByUserId(userId);

        outputUserDto.setAlbums(albums);

        return outputUserDto;
    }

    @Override
    public UserDto findUserByEmail(String email) {
        UserEntity userEntity = userRepository.findByEmail(email);
        if(userEntity == null) throw new UsernameNotFoundException(email);

        UserDto outputUserDto = new ModelMapper().map(userEntity, UserDto.class);

        return outputUserDto;
    }

    @Override
    public UserDto createUser(UserDto inputUserDto) {
        inputUserDto.setUserId(UUID.randomUUID().toString());

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        UserEntity userEntity = modelMapper.map(inputUserDto, UserEntity.class);
        userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(inputUserDto.getPassword()));

        userRepository.save(userEntity);

        UserDto outputUserDto = modelMapper.map(userEntity, UserDto.class);

        return outputUserDto;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(username);
        if(userEntity == null) throw new UsernameNotFoundException(username);
        
        return new User(
            userEntity.getEmail(),
            userEntity.getEncryptedPassword(),
            true, true, true, true, new ArrayList<>()
        );
    }

}