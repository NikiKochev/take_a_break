package takeABreak.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import takeABreak.exceptions.BadRequestException;
import takeABreak.exceptions.NotAuthorizedException;
import takeABreak.exceptions.NotFoundException;
import takeABreak.model.dto.comments.*;
import takeABreak.model.pojo.Comment;
import takeABreak.model.pojo.User;
import takeABreak.model.repository.CommentRepository;
import takeABreak.service.CommentService;
import javax.servlet.http.HttpSession;
import java.util.Optional;

@RestController
public class CommentController extends AbstractController{
    @Autowired
    private SessionManager sessionManager;
    @Autowired
    private CommentService commentService;
    @Autowired
    private CommentRepository repository;

    @PostMapping("/comments")
    public AddingResponseCommentsDTO add(@RequestBody AddingRequestCommentsDTO commentsDTO, HttpSession session ){
        if(sessionManager.getLoggedUser(session).getId() != commentsDTO.getUserId()){
            throw new NotAuthorizedException("You cannot write comment from different name");
        }
        return commentService.addComment(commentsDTO);
    }

    @PutMapping("/comments")
    public EditResponseCommentDTO editComment(@RequestBody EditRequestCommentDTO commentDTO, HttpSession session){
        if(sessionManager.getLoggedUser(session).getId() != commentDTO.getUserId()){
            throw new BadRequestException("You cannot write comment from different name");
        }
        return commentService.editComment(commentDTO);
    }

    @DeleteMapping("/comments")
    public DeleteResponseCommentDTO deleteComment (@RequestBody DeleteRequestCommentDTO commentDTO, HttpSession session){
        User user = sessionManager.getLoggedUser(session);
        if(user.getId() != commentDTO.getUserId()){
            throw new NotAuthorizedException("You cannot delete this comment");
        }
        return commentService.deleteComment(commentDTO);
    }
    @GetMapping("/comments/user/{id}")
    public FindResponseCommentDTO getByUserId(@PathVariable int id ){
        return commentService.findComments(id);
    }

    @GetMapping("/comments/post/{id}")
    public FindResponseCommentDTO getByPostId(@PathVariable int id ){
        return commentService.findCommentsForPost(id);
    }

    @PostMapping("/comments/like/{id}")
    public GetByIdResponseCommentDTO like(@PathVariable int id, HttpSession session){
        Optional<Comment> comment = repository.findById(id);
        if(! comment.isPresent()){
            throw new NotFoundException("Not such comment");
        }
        return commentService.likeComment(comment.get(), sessionManager.getLoggedUser(session));
    }

    @GetMapping("/comments/{id}")
    public GetByIdResponseCommentDTO getById (@PathVariable int id){
        return commentService.getById(id);
    }

    @PostMapping("/comments/dislike/{id}")
    public GetByIdResponseCommentDTO dislike(@PathVariable int id, HttpSession session){
        Optional<Comment> comment = repository.findById(id);
        if(! comment.isPresent()){
            throw new NotFoundException("Not such comment");
        }
        return commentService.dislikeComment(comment.get(), sessionManager.getLoggedUser(session));
    }

}
