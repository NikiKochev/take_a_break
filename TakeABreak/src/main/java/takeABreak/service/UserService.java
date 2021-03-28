package takeABreak.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import takeABreak.exceptions.AuthenticationException;
import takeABreak.exceptions.BadRequestException;
import takeABreak.exceptions.NotFoundException;
import takeABreak.model.dao.UserDAO;
import takeABreak.model.dto.user.*;
import takeABreak.model.pojo.User;
import takeABreak.model.repository.UserRepository;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;
    @Autowired
    private UserDAO userDAO;

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
        File file = new File(user.get().getAvatar());
        return Files.readAllBytes(file.toPath());
    }

    public UserDeleteResponseDTO deleteDate(User user) {
        user.setDeletedAt(LocalDate.now());
        user.setAvatar(null);
        user.setCity(null);
        user.setCountry(null);
        user.setEmail(null);
        user.setFirstName(null);
        user.setLastName(null);
        repository.save(user);
        return new UserDeleteResponseDTO(user);
    }

    public LoginUserResponseDTO editUser(User loggedUser, EditResponseUserDTO userDTO) {
        if(userDTO.getAge() != 0){
            loggedUser.setAge(userDTO.getAge());
        }
        if(userDTO.getFirstName() != null){
            loggedUser.setFirstName(userDTO.getFirstName());
        }
        if(userDTO.getLastName() != null){
            loggedUser.setLastName(userDTO.getLastName());
        }
        if(userDTO.getEmail() != null){
            loggedUser.setEmail(userDTO.getEmail());
        }
        if(userDTO.getCity() != null){
            loggedUser.setCity(userDTO.getCity());
        }
        if(userDTO.getCountry() != null){
            loggedUser.setCountry(userDTO.getCountry());
        }
        if(userDTO.getPassword() != null){
            PasswordEncoder encoder = new BCryptPasswordEncoder();
            loggedUser.setPassword(encoder.encode(userDTO.getPassword()));
        }
        repository.save(loggedUser);// дали да се пусне в друга нишка да се запише ако се сменя емейла,
        // която да чака да се потвърди и тогава да се запише в базата данни
        return new LoginUserResponseDTO(loggedUser);
    }
    public SearchForUsersResponseDTO findUsers(SearchForUsersRequestDTO searchDTO) {
        return new SearchForUsersResponseDTO(userDAO.findBy(searchDTO));
    }
}
