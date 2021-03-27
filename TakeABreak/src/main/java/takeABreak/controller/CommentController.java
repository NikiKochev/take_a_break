package takeABreak.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import takeABreak.exceptions.BadRequestException;
import takeABreak.exceptions.NotAuthorizedException;
import takeABreak.model.dto.*;
import takeABreak.model.pojo.User;
import takeABreak.service.CommentService;
import javax.servlet.http.HttpSession;

@RestController
public class CommentController {
    @Autowired
    private SessionManager sessionManager;
    @Autowired
    private CommentService commentService;

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
}
