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

    @PostMapping("/comments")
    public AddingResponseCommentsDTO add(@RequestBody AddingRequestCommentsDTO commentsDTO, HttpSession session ){
        User user = sessionManager.getLoggedUser(session);
        return commentService.addComment(commentsDTO, user);
    }

    @PutMapping("/comments")
    public EditResponseCommentDTO editComment(@RequestBody EditRequestCommentDTO commentDTO, HttpSession session){
        User user = sessionManager.getLoggedUser(session);
        return commentService.editComment(commentDTO, user);
    }

    @DeleteMapping("/comments")
    public DeleteResponseCommentDTO deleteComment (@RequestBody DeleteRequestCommentDTO commentDTO, HttpSession session){
        User user = sessionManager.getLoggedUser(session);
        return commentService.deleteComment(commentDTO, user);
    }

    @GetMapping("/comments/user/")
    public FindResponseCommentDTO getByUser(@RequestParam int userId, @RequestParam int page, @RequestParam int perpage ){
        return commentService.findComments(userId, page, perpage);
    }

    @GetMapping("/comments/post/{postId}")
    public FindResponseCommentDTO getByPostId(@PathVariable int postId, @RequestParam int page, @RequestParam int perpage ){
        return commentService.findCommentsForPost(postId, page, perpage);
    }

    @PostMapping("/comments/like")
    public GetByIdResponseCommentDTO like(@RequestParam int commentId, HttpSession session){
        return commentService.likeComment(commentId, sessionManager.getLoggedUser(session));
    }

    @GetMapping("/comments/{commentId}")
    public GetResponseCommentDTO getById (@PathVariable(name = "commentId") int commentId){
        return commentService.getById(commentId);
    }

    @PostMapping("/comments/dislike")
    public GetByIdResponseCommentDTO dislike(@RequestParam int commentId, HttpSession session){
        return commentService.dislikeComment(commentId, sessionManager.getLoggedUser(session));
    }

}
