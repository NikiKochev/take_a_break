package takeABreak.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import takeABreak.exceptions.BadRequestException;
import takeABreak.exceptions.NotFoundException;
import takeABreak.model.dto.post.*;
import takeABreak.model.pojo.*;
import takeABreak.model.repository.*;

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
    private CategoryService categoryService;
    @Autowired
    private SizeService sizeService;
    @Autowired
    private ContentService contentService;
    @Autowired
    private UserService userService;


    public AddingResponsePostDTO addPost(AddingRequestPostDTO postDTO, User user) {
        Optional<Category> cat = categoryService.findById(postDTO.getCategoryId());
        if(!cat.isPresent()){
            throw new NotFoundException("Not such a category");
        }
        Category category = cat.get();
        if(user.getId() != postDTO.getUserId()){
            throw new BadRequestException("Not the same person");
        }
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
        FormatType formatType = new FormatType(content, f.getAbsolutePath(),sizeService.findById(1).get());
        formatList.add(formatType);
        // тук ще трябва да са въведени всички видове снимки или видеа
        content.setFormatTypes(formatList);
        contentService.save(content);
        return new AddingContentToPostResponsePostDTO(content);
    }

    public DisLikeResponsePostDTO dislikeComment(DisLikeRequestPostDTO postDTO, User user) {
        Optional<Post> p = postRepository.findById(postDTO.getPostId());
        if(! p.isPresent()){
            throw new NotFoundException("Not such post");
        }
        Post post = p.get();
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
        Optional<Post> p = postRepository.findById(postDTO.getPostId());
        if(! p.isPresent()){
            throw new NotFoundException("Not such post");
        }
        Post post = p.get();
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
        Optional<Post> p = postRepository.findById(id);
        if(!p.isPresent()){
            throw new BadRequestException("no such a post");
        }
        return new GetByIdResponsePostDTO(p.get());
    }
}
