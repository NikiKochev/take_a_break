package takeABreak.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import takeABreak.exceptions.AuthenticationException;
import takeABreak.exceptions.BadRequestException;
import takeABreak.model.dto.user.*;
import takeABreak.model.pojo.User;
import takeABreak.model.repository.UserRepository;
import takeABreak.service.UserService;
import javax.servlet.http.HttpSession;
import java.util.Optional;

import java.io.*;


@RestController
public class UserController extends AbstractController{

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Value("${file.path}")
    private String filePath;
    @Autowired
    private SessionManager sessionManager;
    @PutMapping("/user")
    public RegisterResponseUserDTO register(@RequestBody RegisterRequestUserDTO userDTO){
        //todo validation http 
        return userService.addUser(userDTO);
    }

    @PostMapping("/users/login")
    public LoginUserResponseDTO login(@RequestBody LoginUserRequestDTO dto, HttpSession ses){
        LoginUserResponseDTO responseDto = userService.login(dto);
        sessionManager.loginUser(ses, responseDto.getId());
        return responseDto;
    }

    @PostMapping("/users/logout")
    public void logout(HttpSession ses){
        sessionManager.logoutUser(ses);
    }

    @PutMapping("/user/{id}/avatar")
    public UploadAvatarDTO upload(@PathVariable(name = "id") int id, @RequestPart MultipartFile file, HttpSession session){
//        User user = sessionManager.getLoggedUser(session);//TODO fix it and delete the next row
        User user = userRepository.findById(id).get();
        if(user.getId() != id){
            throw new BadRequestException("You can't post an avatar to another user's post");
        }
        Optional<User> optionalUser = userRepository.findById(id);
        if(optionalUser.isPresent()){
            user = optionalUser.get();
        }else{
            throw new BadRequestException("Server Error. Try to logout and then login again.");
        }
        return userService.addAvatar(file, user);
    }

    @GetMapping("/users/{id}")
    public LoginUserResponseDTO getById(@PathVariable int id){
        return userService.getById(id);
    }

    @GetMapping(value = "/users/avatar/{id}", produces = "image/*")
    public byte[] downloadById(@PathVariable int id, HttpSession ses) throws IOException {
        sessionManager.getLoggedUser(ses);
        return userService.getAvatar(userService.findById(id));
    }

    @DeleteMapping("/user/{id}")
    public UserDeleteResponseDTO deleteUser(@PathVariable int id,HttpSession session){
        User user = sessionManager.getLoggedUser(session);
        if(user.getId() != id){
            throw new BadRequestException("You can't delete other profile");
        }
        return userService.deleteDate(user);
    }

    @PutMapping("/user/{id}")
    public LoginUserResponseDTO editUser(@RequestBody EditResponseUserDTO userDTO, HttpSession session){
        User user = sessionManager.getLoggedUser(session);
        return userService.editUser(user, userDTO);
    }

    @PostMapping("/users/search")
    public SearchForUsersResponseDTO findUsers(@RequestBody SearchForUsersRequestDTO searchDTO){
        return userService.findUsers(searchDTO);
    }

    @GetMapping("/verify")
    public LoginUserResponseDTO verifyEmail(){
        //todo
        return null;
    }
}
