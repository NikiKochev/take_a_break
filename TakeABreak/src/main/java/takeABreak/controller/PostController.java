package takeABreak.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import takeABreak.exceptions.BadRequestException;
import takeABreak.exceptions.NotAuthorizedException;
import takeABreak.exceptions.NotFoundException;
import takeABreak.model.dto.categorory.AllCategoryResponseDTO;
import takeABreak.model.dto.post.*;
import takeABreak.model.pojo.Category;
import takeABreak.model.pojo.FileType;
import takeABreak.model.pojo.Post;
import takeABreak.model.pojo.User;
import takeABreak.model.repository.CategoryRepository;
import takeABreak.model.repository.FormatTypeRepository;
import takeABreak.model.repository.PostRepository;
import takeABreak.model.repository.UserRepository;
import takeABreak.service.PostService;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Optional;

@RestController
public class PostController extends AbstractController{

    @Autowired
    private SessionManager sessionManager;
    @Autowired
    private PostService postService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FormatTypeRepository typeRepository;
    @Value("${file.path}")
    private String filePath;

    @PutMapping("/posts")
    public AddingResponsePostDTO addPost(@RequestBody AddingRequestPostDTO postDTO, HttpSession session){
        User u = sessionManager.getLoggedUser(session);
        return postService.addPost(postDTO, u);
    }

    @PutMapping("/posts/add/image")
    public AddImageToPostResponseDTO addImageToPost(@RequestPart MultipartFile file, HttpSession session){
//        User user = sessionManager.getLoggedUser(session);//TODO fix it and delete the next row
//        int userId =  user.getId();

        int userId  = 5;
//       ============
        return postService.addImageToPost(file, userId);
    }

    @PutMapping("/posts/{id}/type")
    public AddingContentToPostResponsePostDTO addContentToPost(@PathVariable(name = "id") int typeId, @RequestPart MultipartFile file, HttpSession ses) throws IOException {
        sessionManager.getLoggedUser(ses);
        Optional<FileType> t = typeRepository.findById(typeId);
        if(!t.isPresent()){
            throw new NotFoundException("file type not found");
        }
        FileType fileType = t.get();
        File f = new File(filePath + File.separator + typeId+"_"+System.nanoTime() +".png");
        OutputStream os = new FileOutputStream(f);
        os.write(file.getBytes());
        os.close();
        AddingContentToPostResponsePostDTO add = postService.addContent(f, fileType);
        return add;
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

    @GetMapping("posts/{id}")
    public GetByIdResponsePostDTO getById(@PathVariable int id){
        return postService.getById(id);
    }

    @GetMapping("posts/{id}/users")
    public GetAllByResponsePostDTO getByUser(@PathVariable int id, @RequestParam int page, @RequestParam int perpage){
        return postService.getByUser(id, page, perpage);
    }

    @GetMapping("posts/categories/{id}/")
    public GetAllByResponsePostDTO getByCategory(@PathVariable int id, @RequestParam int page, @RequestParam int perpage){
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
