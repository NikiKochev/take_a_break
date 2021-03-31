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
import java.io.*;

@RestController
public class UserController extends AbstractController{

    @Autowired
    private UserService userService;
    @Value("C:\\Users\\Public\\Pictures")
    private String filePath;
    @Autowired
    private UserRepository repo;
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

    @PostMapping("/user/logout")
    public void logout(HttpSession ses){
        sessionManager.logoutUser(ses);
    }

    @PutMapping("/user/{id}/avatar")
    public UploadAvatarDTO upload(@PathVariable(name = "id") int id, @RequestPart MultipartFile file, HttpSession ses)  {
        User user = sessionManager.getLoggedUser(ses);
        if(user.getId() != id){
            throw new BadRequestException("You can't post avatar to other users");
        }
        File f = new File(filePath + File.separator + id+"_"+System.nanoTime() +".png");
        try(OutputStream os = new FileOutputStream(f);) {
            os.write(file.getBytes());
        } catch (IOException e) {
            throw new AuthenticationException("Sorry, we could not upload this file. Try saving it in a different format and upload again");
        }
        UploadAvatarDTO avatar = userService.addAvatar(f, repo.findById(id).get());
        return avatar;
    }

    @GetMapping("/users/{id}")
    public LoginUserResponseDTO getById(@PathVariable int id){
        return userService.getById(id);
    }

    @GetMapping(value = "/users/avatar/{id}", produces = "image/*")
    public byte[] downloadById(@PathVariable int id, HttpSession ses) throws IOException {
        sessionManager.getLoggedUser(ses);
        return userService.getAvatar(repo.findById(id));
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
        return userService.editUser(sessionManager.getLoggedUser(session), userDTO);
    }

    @PostMapping("/users/search")
    public SearchForUsersResponseDTO findUsers(@RequestBody SearchForUsersRequestDTO searchDTO){
        return userService.findUsers(searchDTO);
    }//todo

    @GetMapping("/verify")
    public LoginUserResponseDTO verifyEmail(){
        //todo
        return null;
    }
}
