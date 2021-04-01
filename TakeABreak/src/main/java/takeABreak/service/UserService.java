package takeABreak.service;

import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import takeABreak.exceptions.AuthenticationException;
import takeABreak.exceptions.BadRequestException;
import takeABreak.exceptions.NotFoundException;
import takeABreak.model.dao.UserDao;
import takeABreak.model.dto.user.*;
import takeABreak.model.pojo.User;
import takeABreak.model.repository.UserRepository;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;
    @Autowired
    private UserDao userDAO;
    @Autowired
    private EmailService emailService;
    @Autowired
    private CountryService countryService;

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
        String randomCode = RandomString.make(64);
        user.setVerification(randomCode);
        repository.save(user);
        Thread t= new Thread(() -> emailService.sendSimpleMessage(user));
        t.start();
        RegisterResponseUserDTO responseUserDTO = new RegisterResponseUserDTO(user);
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
        PasswordEncoder encoder = new BCryptPasswordEncoder();;
        if (user == null || !encoder.matches(dto.getPassword(), user.getPassword())) {
            throw new AuthenticationException("wrong credentials");
        }
        return new LoginUserResponseDTO(user);
    }

    public LoginUserResponseDTO getById(int id) {
        return new LoginUserResponseDTO(findById(id));
    }

    public byte[] getAvatar(User user) throws IOException {
        File file = new File(user.getAvatar());
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
        if(userDTO.getCountry() >= 0){
            loggedUser.setCountry(countryService.findById(userDTO.getCountry()));
        }
        if(userDTO.getPassword() != null){
            PasswordEncoder encoder = new BCryptPasswordEncoder();
            loggedUser.setPassword(encoder.encode(userDTO.getPassword()));
        }
        repository.save(loggedUser);
        return new LoginUserResponseDTO(loggedUser);
    }

    public SearchForUsersResponseDTO findUsers(SearchForUsersRequestDTO searchDTO) {
        return new SearchForUsersResponseDTO(userDAO.findBy(searchDTO));
    }

    public void save(User user) {
        repository.save(user);
    }

    public User findById(int userId) {
        Optional<User> user = repository.findById(userId);
        if(user.isPresent()){
            return user.get();
        }
        throw  new BadRequestException("No such person");
    }
}
