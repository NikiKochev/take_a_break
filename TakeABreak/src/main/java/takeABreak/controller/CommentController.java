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

    @GetMapping("/comments/user/{userId}")
    public FindResponseCommentDTO getByUser(@PathVariable int userId, @RequestParam int page, @RequestParam int perpage ){//todo
        return commentService.findComments(userId, page, perpage);
    }

    @GetMapping("/comments/post/{postId}")
    public FindResponseCommentDTO getByPostId(@PathVariable int postId, @RequestParam int page, @RequestParam int perpage ){
        return commentService.findCommentsForPost(postId, page, perpage);
    }

    @PostMapping("/comments/like/{commentId}")
    public GetByIdResponseCommentDTO like(@PathVariable int commentId, HttpSession session){
        return commentService.likeComment(commentId, sessionManager.getLoggedUser(session));
    }

    @GetMapping("/comments/{commentId}")
    public GetByIdResponseCommentDTO getById (@PathVariable int commentId){
        return commentService.getById(commentId);
    }

    @PostMapping("/comments/dislike/{commentId}")
    public GetByIdResponseCommentDTO dislike(@PathVariable int commentId, HttpSession session){
        return commentService.dislikeComment(commentId, sessionManager.getLoggedUser(session));
    }

}
