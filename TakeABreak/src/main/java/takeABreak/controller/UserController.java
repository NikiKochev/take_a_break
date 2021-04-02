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
import javax.websocket.server.PathParam;
import java.util.Optional;

import java.io.*;


@RestController
public class UserController extends AbstractController{

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SessionManager sessionManager;
    @PutMapping("/user")
    public RegisterResponseUserDTO register(@RequestBody RegisterRequestUserDTO userDTO){
        return userService.addUser(userDTO);
    }

    @PostMapping("/users/login")
    public LoginUserResponseDTO login(@RequestBody LoginUserRequestDTO dto, HttpSession ses){
        LoginUserResponseDTO responseDto = userService.login(dto);
        sessionManager.loginUser(ses, responseDto.getId());
        return responseDto;
    }

    @PostMapping("/user/logout")
    public void logout(HttpSession ses){
        sessionManager.logoutUser(ses);
    }

    @PutMapping("/user/{id}/avatar")
    public UploadAvatarDTO upload(@PathVariable(name = "id") int id, @RequestPart MultipartFile file, HttpSession ses){
//        User user = sessionManager.getLoggedUser(ses);

        //TODO debugging mode use the line above instead the following lines after login is working
        Optional<User> optionalUser = userRepository.findById(5);
        User user = null;
        if(optionalUser.isPresent()){
            user = optionalUser.get();
        }
        if(user.getId() != id){
            throw new BadRequestException("You can't post an avatar to another user's post");
        }
        ///<-end debugging mode

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

    @GetMapping("/users/verify{code}")
    public LoginUserResponseDTO verifyEmail(@PathParam("code") String code){
        return userService.findByVerificationCode(code);
    }
}
