package takeABreak.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import takeABreak.exceptions.BadRequestException;
import takeABreak.exceptions.NotAuthorizedException;
import takeABreak.exceptions.NotFoundException;
import takeABreak.model.dao.PostDAO;
import takeABreak.model.dto.post.*;
import takeABreak.model.pojo.*;
import takeABreak.model.repository.*;

import javax.transaction.Transactional;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private PostDAO postDAO;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SizeService sizeService;
    @Autowired
    private ContentService contentService;
    @Autowired
    private UserService userService;


    public AddingResponsePostDTO addPost(AddingRequestPostDTO postDTO, User user) {
        if(user.getId() != postDTO.getUserId()){
            throw new BadRequestException("Not the same person");
        }
        Category category = categoryService.findById(postDTO.getCategoryId());
        Content content = contentService.findById(postDTO.getContentId());
        Post post = new Post();
        post.setCategory(category);
        post.setUser(user);
        post.setTitle(postDTO.getTitle());
        post.setContent(content);
        post.setCreatedAt(LocalDate.now());
        if(postDTO.getDescription() !=null) {
            post.setDescription(postDTO.getDescription());
        }
        postRepository.save(post);
        return new AddingResponsePostDTO(post);
    }

    public AddingContentToPostResponsePostDTO addContent(File f,  FileType type) {
        Content content = new Content();
        content.setFileType(type);
        //todo тука трябва да се създадт няколко различни файла с различна големина и да се подадат и да се създадат обекти FormatType
        //аз съм го направил за един формат
        // всички тези контенти да се пратят на addCo...
        List<FormatType> formatList = new ArrayList<>();
        FormatType formatType = new FormatType(content, f.getAbsolutePath(),sizeService.findById(1));
        formatList.add(formatType);
        // тук ще трябва да са въведени всички видове снимки или видеа
        content.setFormatTypes(formatList);
        contentService.save(content);
        return new AddingContentToPostResponsePostDTO(content);
    }

    public DisLikeResponsePostDTO dislikeComment(DisLikeRequestPostDTO postDTO, User user) {
        Post post = findById(postDTO.getPostId());
        if(!user.getDislikedPosts().contains(post)) {
            user.getDislikedPosts().add(post);
            user.getLikedPosts().remove(post);
        }
        else {
            user.getDislikedPosts().remove(post);
        }
        userService.save(user);
        return new DisLikeResponsePostDTO(post, user,false);
    }

    public DisLikeResponsePostDTO likeComment(DisLikeRequestPostDTO postDTO, User user) {
        Post post = findById(postDTO.getPostId());
        if(!user.getLikedPosts().contains(post)) {
            user.getLikedPosts().add(post);
            user.getDislikedPosts().remove(post);
        }
        else {
            user.getLikedPosts().remove(post);
        }
        userService.save(user);
        return new DisLikeResponsePostDTO(post, user,true);
    }

    public GetByIdResponsePostDTO getById(int id) {
        return new GetByIdResponsePostDTO(findById(id));
    }

    public SearchResponsePostDTO findBy(FindByRequestPostDTO postDTO) {
        return new SearchResponsePostDTO( postDAO.findBy(postDTO.getText(), postDTO.getPage(), postDTO.getPerpage()));
    }

    public GetAllByResponsePostDTO getByCategory(int categoryId, int page, int perpage) {
        categoryService.findById(categoryId);
        return new GetAllByResponsePostDTO(postDAO.findByCategory(categoryId, page, perpage));
    }

    public GetAllByResponsePostDTO getByLast(int page, int perpage){
        return new GetAllByResponsePostDTO(postDAO.findLast(page, perpage));
    }

    public GetAllByResponsePostDTO getByUser(int userId, int page, int perpage) {
        userService.findById(userId);
        return new GetAllByResponsePostDTO(postDAO.findByUser(userId, page, perpage));
    }

    @Transactional
    public DeleteResponsePostDTO deletePost(DeleteRequestPostDTO postDTO, User user) {
        Post post = findById(postDTO.getPostId());
        if(user.getPosts().contains(post)){
            throw new NotAuthorizedException("You cannot delete post of others");
        }
        user.getPosts().remove(post);
        postRepository.delete(post);
        userService.save(user);
        return new DeleteResponsePostDTO("Post is deleted");
    }

    public Post findById(int postId) {
        Optional<Post> post  = postRepository.findById(postId);
        if(post.isPresent() ){
            return post.get();
        }
        throw new BadRequestException("No such post");
    }
}
