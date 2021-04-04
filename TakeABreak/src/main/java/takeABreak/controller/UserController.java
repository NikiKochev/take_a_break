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
import javax.validation.Valid;
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
    @PutMapping("/users")
    public RegisterResponseUserDTO register(@Valid @RequestBody RegisterRequestUserDTO userDTO){
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
        User user = sessionManager.getLoggedUser(session);
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

    @DeleteMapping("/users")
    public UserDeleteResponseDTO deleteUser(HttpSession session){
        User user = sessionManager.getLoggedUser(session);
        UserDeleteResponseDTO responseDTO = userService.deleteDate(user);
        logout(session);
        return responseDTO;
    }

    @PutMapping("/users/account")
    public LoginUserResponseDTO editUser(@Valid @RequestBody EditResponseUserDTO userDTO, HttpSession session){
        User user = sessionManager.getLoggedUser(session);
        LoginUserResponseDTO responseDTO = userService.editUser(user, userDTO);
        if(userDTO.getPassword() != null){
            logout(session);
        }
        return responseDTO;
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
