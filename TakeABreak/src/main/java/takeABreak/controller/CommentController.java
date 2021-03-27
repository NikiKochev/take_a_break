package takeABreak.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import takeABreak.exceptions.BadRequestException;
import takeABreak.exceptions.NotAuthorizedException;
import takeABreak.exceptions.NotFoundException;
import takeABreak.model.dto.*;
import takeABreak.model.pojo.Comment;
import takeABreak.model.pojo.User;
import takeABreak.model.repository.CommentRepository;
import takeABreak.service.CommentService;
import javax.servlet.http.HttpSession;
import java.util.Optional;

@RestController
public class CommentController {
    @Autowired
    private SessionManager sessionManager;
    @Autowired
    private CommentService commentService;
    @Autowired
    private CommentRepository repository;

    @PostMapping("/comments/{id}")
    public AddingResponseCommentsDTO addComment (@RequestBody AddingRequestCommentsDTO commentsDTO, HttpSession session, @PathVariable int id){
        User u = sessionManager.getLoggedUser(session);
        if(commentsDTO.getPostId() != id){
            throw new BadRequestException("You cannot write comments to different post");
        }
        return commentService.addComment(commentsDTO);
    }

    @PutMapping("/coments")
    public EditResponseCommentDTO editComment(@RequestBody EditRequestCommentDTO commentDTO, HttpSession session){

        if(sessionManager.getLoggedUser(session).getId() != commentDTO.getUserId()){
            throw new BadRequestException("You cannot write comment from different name");
        }
        //да видя дали сесията е активна ако не да се логва
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
    @GetMapping("/comments/user/{id}/search?page=1&perpage=20")
    public FindResponseCommentDTO getByUserId(@RequestParam int page , @RequestParam int perpage, @PathVariable int id ){
        return commentService.findComments(id, page, perpage);
    }

    @GetMapping("/comments/post/{id}/search?page=1&perpage=20")
    public FindResponseCommentDTO getByPostId(@RequestParam int page , @RequestParam int perpage, @PathVariable int id ){
        return commentService.findCommentsForPost(id, page, perpage);
    }

    @PostMapping("/comments/like/{id}")
    public GetByIdResponseCommentDTO like(@PathVariable int id, HttpSession session){
        Optional<Comment> comment = repository.findById(id);
        if(! comment.isPresent()){
            throw new NotFoundException("Not such comment");
        }
        if(comment.get().getLikers().contains(sessionManager.getLoggedUser(session))){
            throw  new BadRequestException("User already liked this comment");
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
        if(comment.get().getDislikers().contains(sessionManager.getLoggedUser(session))){
            throw  new BadRequestException("User already disliked this comment");
        }
        return commentService.dislikeComment(comment.get(), sessionManager.getLoggedUser(session));
    }

}
