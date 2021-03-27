package takeABreak.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import takeABreak.exceptions.BadRequestException;
import takeABreak.model.dto.*;
import takeABreak.model.pojo.Comment;
import takeABreak.model.repository.CommentRepository;
import takeABreak.model.repository.PostRepository;
import takeABreak.model.repository.UserRepository;

import java.time.LocalDate;

@Service
public class CommentService {

    @Autowired
    private UserRepository uRepository;
    @Autowired
    private PostRepository pRepository;
    @Autowired
    private CommentRepository cRepository;

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
}
