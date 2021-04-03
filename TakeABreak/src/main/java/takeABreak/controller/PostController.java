package takeABreak.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import takeABreak.exceptions.NotAuthorizedException;
import takeABreak.exceptions.NotFoundException;
import takeABreak.model.dto.post.*;
import takeABreak.model.pojo.FileType;
import takeABreak.model.pojo.User;
import takeABreak.model.repository.FileTypeRepository;
import takeABreak.model.repository.FormatTypeRepository;
import takeABreak.model.repository.UserRepository;
import takeABreak.service.PostService;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;


@RestController
public class PostController extends AbstractController{

    @Autowired
    private SessionManager sessionManager;
    @Autowired
    private PostService postService;
    @Autowired
    private FileTypeRepository typeRepository;
    @Value("${file.path}")
    private String filePath;

    @PutMapping("/posts")
    public AddingResponsePostDTO addPost(@RequestBody AddingRequestPostDTO postDTO, HttpSession session){
        User user = sessionManager.getLoggedUser(session);
        String sessionId = session.getId();
        return postService.addPost(postDTO, user, sessionId);
    }

    @PutMapping("/posts/add/image")
    public AddMediaToPostResponseDTO addImageToPost(@RequestPart MultipartFile file, HttpSession session){
        sessionManager.getLoggedUser(session);
        String sessionId = session.getId();
        return postService.addImageToPost(file, sessionId);
    }



    @PutMapping("/posts/add/video")
    public AddMediaToPostResponseDTO addVideoToPost(@RequestPart MultipartFile file, HttpSession session){
        sessionManager.getLoggedUser(session);
        String sessionId = session.getId();
        return postService.addVideoToPost(file, sessionId);
    }

    @DeleteMapping("/posts")
    public DeleteResponsePostDTO deletePost(@RequestBody DeleteRequestPostDTO postDTO, HttpSession session){
        User user = sessionManager.getLoggedUser(session);
        return postService.deletePost(postDTO, user);
    }

    @PostMapping("/posts/dislike")
    public DisLikeResponsePostDTO dislike(@RequestBody DisLikeRequestPostDTO postDTO, HttpSession session){
        User user = sessionManager.getLoggedUser(session);
        if(user.getId() != postDTO.getUserId()){
            throw new NotAuthorizedException("cannot dislike this post");
        }
        return postService.dislikeComment(postDTO, user);
    }

    @PostMapping("/posts/like")
    public DisLikeResponsePostDTO like(@RequestBody DisLikeRequestPostDTO postDTO, HttpSession session){
        User user = sessionManager.getLoggedUser(session);
        if(user.getId() != postDTO.getUserId()){
            throw new NotAuthorizedException("cannot like this post");
        }
        return postService.likeComment(postDTO, user);
    }

    @GetMapping("/posts/{id}")
    public GetByIdResponsePostDTO getById(@PathVariable int id){
        return postService.getById(id);
    }

    @GetMapping("/posts/{id}/users")
    public GetAllByResponsePostDTO getByUser(@PathVariable int id, @RequestParam int page, @RequestParam int perpage){
        return postService.getByUser(id, page, perpage);
    }

    @GetMapping("/posts/{id}/categories/")
    public GetAllByResponsePostDTO getByCategory(@PathVariable(name = "id") int id, @RequestParam int page, @RequestParam int perpage){
        return postService.getByCategory(id, page, perpage);
    }

    @GetMapping("/posts/")
    public GetAllByResponsePostDTO getLast(@RequestParam int page, @RequestParam int perpage){
        return postService.getByLast(page, perpage);
    }

    @PostMapping("/posts/search")
    public SearchResponsePostDTO findPosts(@RequestBody FindByRequestPostDTO postDTO){
        return postService.findBy(postDTO);
    }

}
