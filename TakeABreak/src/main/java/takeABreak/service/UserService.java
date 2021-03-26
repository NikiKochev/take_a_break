package takeABreak.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import takeABreak.exceptions.AuthenticationException;
import takeABreak.exceptions.BadRequestException;
import takeABreak.exceptions.NotFoundException;
import takeABreak.model.dto.*;
import takeABreak.model.pojo.User;
import takeABreak.model.repository.UserRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;

    public RegisterResponseUserDTO addUser(RegisterRequestUserDTO userDTO) {
        if (!userDTO.getPassword().equals(userDTO.getConfirmPassword())) {
            throw new BadRequestException("Passwords are not equals");
        }
        if (!userDTO.getPassword().equals(userDTO.getPassword().toLowerCase())
                & !userDTO.getPassword().equals(userDTO.getPassword().toUpperCase())
                & userDTO.getPassword().matches("-?\\d+(\\.\\d+)?")) {
            throw new BadRequestException("Wrong credential. Must have digits, upper and lower character at password");
        }
        if (repository.findByEmail(userDTO.getEmail()) != null) {
            throw new BadRequestException("You already have account");
        }
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        userDTO.setPassword(encoder.encode(userDTO.getPassword()));
        User user = new User(userDTO);
        RegisterResponseUserDTO responseUserDTO = new RegisterResponseUserDTO(repository.save(user));
        return responseUserDTO;
    }

    public UploadAvatarDTO addAvatar(File file, User user) {
        user.setAvatar(file.getAbsolutePath());
        repository.save(user);
        UploadAvatarDTO avatar = new UploadAvatarDTO(repository.findById(user.getId()).get().getAvatar(), user.getId());
        return avatar;
    }

    public LoginUserResponseDTO login(LoginUserRequestDTO dto) {
        User user = repository.findByEmail(dto.getEmail());
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        if (user == null || !encoder.matches(dto.getPassword(), user.getPassword())) {
            throw new AuthenticationException("wrong credentials");
        }
        return new LoginUserResponseDTO(user);
    }

    public LoginUserResponseDTO getById(int id) {
        Optional<User> u = repository.findById(id);
        if (!u.isPresent()) {
            throw new NotFoundException("Not fount");
        }
        LoginUserResponseDTO user = new LoginUserResponseDTO(u.get());
        return user;
    }

    public byte[] getAvatar(Optional<User> user) throws IOException {
        if (!user.isPresent()) {
            throw new NotFoundException("Not such avatar");
        }
        System.out.println(user.get().getAvatar());
        File file = new File(user.get().getAvatar());
        return Files.readAllBytes(file.toPath());
    }
}
