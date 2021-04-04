package takeABreak.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import takeABreak.model.dto.post.*;
import takeABreak.model.pojo.User;
import takeABreak.service.PostService;
import javax.servlet.http.HttpSession;


@RestController
public class PostController extends AbstractController{

    @Autowired
    private SessionManager sessionManager;
    @Autowired
    private PostService postService;

    @PutMapping("/posts/add")
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

    @PutMapping("/posts/edit")
    public AddingResponsePostDTO editPost(@RequestBody EditingRequestPostDTO postDTO, HttpSession session){
        User user = sessionManager.getLoggedUser(session);
        String sessionId = session.getId();
        return postService.editPost(postDTO, user, sessionId);
    }

    @DeleteMapping("/posts/delete")
    public DeleteResponsePostDTO deletePost(@RequestBody DeleteRequestPostDTO postDTO, HttpSession session){
        User user = sessionManager.getLoggedUser(session);
        return postService.deletePost(postDTO, user);
    }

    @PostMapping("/posts/dislike")
    public DisLikeResponsePostDTO dislike(@RequestBody DisLikeRequestPostDTO postDTO, HttpSession session){
        User user = sessionManager.getLoggedUser(session);
        return postService.dislikeComment(postDTO, user);
    }

    @PostMapping("/posts/like")
    public DisLikeResponsePostDTO like(@RequestBody DisLikeRequestPostDTO postDTO, HttpSession session){
        User user = sessionManager.getLoggedUser(session);
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

    @GetMapping("/posts/{id}/categories")
    public GetAllByResponsePostDTO getByCategory(@PathVariable(name = "id") int id, @RequestParam int page, @RequestParam int perpage){
        return postService.getByCategory(id, page, perpage);
    }

    @GetMapping("/posts")
    public GetAllByResponsePostDTO getLast(@RequestParam int page, @RequestParam int perpage){
        return postService.getByLast(page, perpage);
    }

    @PostMapping("/posts/search")
    public SearchResponsePostDTO findPosts(@RequestBody FindByRequestPostDTO postDTO){
        return postService.findBy(postDTO);
    }

}
