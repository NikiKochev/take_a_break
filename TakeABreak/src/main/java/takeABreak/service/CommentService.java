package takeABreak.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import takeABreak.exceptions.BadRequestException;
import takeABreak.exceptions.NotFoundException;
import takeABreak.model.dao.CommentsDAO;
import takeABreak.model.dto.*;
import takeABreak.model.pojo.Comment;
import takeABreak.model.pojo.User;
import takeABreak.model.repository.CommentRepository;
import takeABreak.model.repository.PostRepository;
import takeABreak.model.repository.UserRepository;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class CommentService {

    @Autowired
    private UserRepository uRepository;
    @Autowired
    private PostRepository pRepository;
    @Autowired
    private CommentRepository cRepository;
    @Autowired
    private CommentsDAO commentsDao;

    public AddingResponseCommentsDTO addComment(AddingRequestCommentsDTO commentsDTO) {
        if(!uRepository.findById(commentsDTO.getUserId()).isPresent()){
            throw new BadRequestException("No such person");
        }
        if(!pRepository.findById(commentsDTO.getPostId()).isPresent()){
            throw new BadRequestException("No such post");
        }
        if(commentsDTO.getContent() == null || commentsDTO.getContent() == " "){
            throw new BadRequestException("You cannot make a empty comment");
        }
        Comment comment = new Comment();
        comment.setUser(uRepository.findById(commentsDTO.getUserId()).get());
        comment.setPost(pRepository.findById(commentsDTO.getPostId()).get());
        comment.setContent(commentsDTO.getContent());
        comment.setCreatedAt(LocalDate.now());
        if(cRepository.findById(commentsDTO.getParentId()).isPresent()){
            comment.setParent(cRepository.findById(commentsDTO.getParentId()).get());
        }
        cRepository.save(comment);
        return new AddingResponseCommentsDTO(comment);
    }

    public EditResponseCommentDTO editComment(EditRequestCommentDTO commentDTO) {
        if(!uRepository.findById(commentDTO.getUserId()).isPresent()){
            throw new BadRequestException("No such person");
        }
        if(!cRepository.findById(commentDTO.getCommentId()).isPresent()){
            throw new BadRequestException("No such post");
        }
        if(cRepository.findById(commentDTO.getCommentId()).get().getUser().getId() != commentDTO.getUserId()){
            throw new BadRequestException("You cannot edit other users comment");
        }
        if(commentDTO.getContent() == null || commentDTO.getContent() == " "){
            throw new BadRequestException("You cannot make a empty comment");
        }
        Comment comment = cRepository.findById(commentDTO.getCommentId()).get();
        comment.setContent(commentDTO.getContent());
        cRepository.save(comment);
        return new EditResponseCommentDTO(comment);
    }

    public DeleteResponseCommentDTO deleteComment(DeleteRequestCommentDTO commentDTO) {
        if(!uRepository.findById(commentDTO.getUserId()).isPresent()){
            throw new BadRequestException("No such person");
        }
        if(!cRepository.findById(commentDTO.getCommentId()).isPresent()){
            throw new BadRequestException("No such post");
        }
        if(cRepository.findById(commentDTO.getCommentId()).get().getUser().getId() != commentDTO.getUserId()){
            throw new BadRequestException("You cannot edit other users comment");
        }
        cRepository.delete(cRepository.findById(commentDTO.getCommentId()).get());
        return new DeleteResponseCommentDTO("Successful deleted");
    }

    public FindResponseCommentDTO findComments(int id, int page, int perpage) {
        if(!uRepository.findById(id).isPresent()){
            throw new BadRequestException("No such person");
        }
        return new FindResponseCommentDTO(commentsDao.getCommentsByUser(id, page, perpage));
    }

    public FindResponseCommentDTO findCommentsForPost(int id, int page, int perpage) {
        return new FindResponseCommentDTO(commentsDao.getCommentsByPost(id, page, perpage));
    }

    public GetByIdResponseCommentDTO getById(int id) {
        Optional<Comment> comment = cRepository.findById(id);
        if(!comment.isPresent()){
            throw new NotFoundException("Comment not found");
        }
        return new GetByIdResponseCommentDTO(comment.get(),true);
    }

    public GetByIdResponseCommentDTO likeComment(Comment comment, User loggedUser) {
        comment.getLikers().add(loggedUser);
        cRepository.save(comment);
        return new GetByIdResponseCommentDTO(comment,true);
    }

    public GetByIdResponseCommentDTO dislikeComment(Comment comment, User loggedUser) {
        comment.getDislikers().add(loggedUser);
        cRepository.save(comment);
        return new GetByIdResponseCommentDTO(comment,false);
    }
}
