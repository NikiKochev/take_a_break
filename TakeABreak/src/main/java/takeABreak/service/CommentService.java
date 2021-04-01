package takeABreak.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import takeABreak.exceptions.BadRequestException;
import takeABreak.exceptions.NotFoundException;
import takeABreak.model.dao.CommentsDAO;
import takeABreak.model.dto.comments.*;
import takeABreak.model.pojo.Comment;
import takeABreak.model.pojo.User;
import takeABreak.model.repository.CommentRepository;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class CommentService {

    @Autowired
    private UserService userService;
    @Autowired
    private PostService postService;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private CommentsDAO commentsDAO;

    public AddingResponseCommentsDTO addComment(AddingRequestCommentsDTO commentsDTO) {
        commentsDTO.setContent(commentsDTO.getContent().trim());
        if(commentsDTO.getContent() == null || commentsDTO.getContent().equals("")){
            throw new BadRequestException("You cannot make a empty comment");
        }
        Comment comment = new Comment();
        comment.setUser(userService.findById(commentsDTO.getUserId()));
        comment.setPost(postService.findById(commentsDTO.getPostId()));
        comment.setContent(commentsDTO.getContent());
        comment.setCreatedAt(LocalDate.now());
        if(commentRepository.findById(commentsDTO.getParentId()).isPresent()){
            comment.setParent(commentRepository.findById(commentsDTO.getParentId()).get());
        }
        commentRepository.save(comment);
        return new AddingResponseCommentsDTO(comment);
    }

    public EditResponseCommentDTO editComment(EditRequestCommentDTO commentDTO) {
        commentDTO.setContent(commentDTO.getContent().trim());
        User user = userService.findById(commentDTO.getUserId());
        Comment comment = findById(commentDTO.getCommentId());
        if(comment.getUser().getId() != user.getId()){
            throw new BadRequestException("You cannot edit other users comment");
        }
        if(commentDTO.getContent() == null || commentDTO.getContent().equals("")){
            throw new BadRequestException("You cannot make a empty comment");
        }
        comment.setContent(commentDTO.getContent());
        commentRepository.save(comment);
        return new EditResponseCommentDTO(comment);
    }

    private Comment findById(int commentId) {
        Optional<Comment> com =  commentRepository.findById(commentId);
        if(com.isPresent()){
           return com.get();
        }
        throw new BadRequestException("No such post");
    }

    public DeleteResponseCommentDTO deleteComment(DeleteRequestCommentDTO commentDTO) {
        userService.findById(commentDTO.getUserId());
        Comment comment = findById(commentDTO.getCommentId());
        if(comment.getUser().getId() != commentDTO.getUserId()){
            throw new BadRequestException("You cannot edit other users comment");
        }
        commentRepository.delete(comment);
        return new DeleteResponseCommentDTO("Successful deleted");
    }

    public FindResponseCommentDTO findComments(int id, int page, int perpage) {
        userService.findById(id);
        return new FindResponseCommentDTO(commentsDAO.findByUser(id, page, perpage));
    }

    public FindResponseCommentDTO findCommentsForPost(int id, int page, int perpage) {
        return new FindResponseCommentDTO(commentsDAO.findByPost(id, page, perpage));
    }

    public GetByIdResponseCommentDTO getById(int id) {
        return new GetByIdResponseCommentDTO(findById(id),true);
    }

    public GetByIdResponseCommentDTO likeComment(int  commentId, User loggedUser) {
        Comment comment = getCommentById(commentId);
        if(!loggedUser.getLikedComments().contains(comment)) {
            loggedUser.getLikedComments().add(comment);
            loggedUser.getDislikedComments().remove(comment);
        }
        else {
            loggedUser.getLikedComments().remove(comment);
        }
        userService.save(loggedUser);
        return new GetByIdResponseCommentDTO(comment,true);
    }

    public GetByIdResponseCommentDTO dislikeComment(int commentId, User loggedUser) {
        Comment comment = getCommentById(commentId);
        if(!loggedUser.getDislikedComments().contains(comment)) {
            loggedUser.getDislikedComments().add(comment);
            loggedUser.getLikedComments().remove(comment);
        }
        else {
            loggedUser.getDislikedComments().remove(comment);
        }
        userService.save(loggedUser);
        return new GetByIdResponseCommentDTO(comment,false);
    }

    public Comment getCommentById(int id){
        Optional<Comment> comment  = commentRepository.findById(id);
        if(comment.isPresent()){
            return comment.get();
        }
        throw new NotFoundException("Not such comment");
    }
}
