package takeABreak.service;

import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import takeABreak.exceptions.BadRequestException;

import takeABreak.exceptions.NotAuthorizedException;

import takeABreak.exceptions.NotFoundException;
import takeABreak.model.dao.PostDAO;
import takeABreak.model.dto.post.*;
import takeABreak.model.pojo.*;
import takeABreak.model.repository.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import javax.transaction.Transactional;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static takeABreak.service.UserService.AVATAR_TARGET_SIZE;

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
    private AddImageToPostResponseDTO addImageToPostResponseDTO;
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

    public AddImageToPostResponseDTO addImageToPost(MultipartFile multipartFile){

        String extension = multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().lastIndexOf(".") + 1);
        extension = extension.toLowerCase();
        //check if the file format is supported
        HashSet<String> supportedImageFormats = new HashSet();
        supportedImageFormats.add("jpg");
        supportedImageFormats.add("jpeg");
        supportedImageFormats.add("gif");
        supportedImageFormats.add("mbp");
        supportedImageFormats.add("wbmp");
        supportedImageFormats.add("png");
        if(!supportedImageFormats.contains(extension)){
            throw new BadRequestException("Unsupported file type. Please upload an image file in JPEG, PNG, BMP, WBMP or GIF format");
        }

        String dir = TempDir.getLocation();
        String imgName = "" + System.nanoTime();
        String locationOriginalImg = dir + File.separator + imgName + "." + extension;
        File file = new File(locationOriginalImg);

//        try{
//            //write original file in temp dir
//            OutputStream originalFileOutputStream = new FileOutputStream(file);
//            originalFileOutputStream.write(multipartFile.getBytes());
//            addImageToPostResponseDTO.setPathSize1(locationOriginalImg);
//            //resize, crop and convert original file to PNG and save it in the temp dir
//            File originalImgFile = new File(locationOriginalImg);
//            BufferedImage biOriginalImg = ImageIO.read(originalImgFile);
//            int width = biOriginalImg.getWidth();
//            int height = biOriginalImg.getHeight();
//
//            if(width/height < 0.25 || width/height > 4){
//                throw new BadRequestException("Inappropriate image ratio. It should not exceed 1:4 or 4:1");
//            }
//
//            if(width < 460){
//
//            }
//
//            String resized = dir + File.separator + imgName + "_resized.png";
//            File resizedPng = new File(resizedPngLocation);
//
//            int widthPix = biOriginalImg.getWidth();
//            int heightPix = biOriginalImg.getHeight();
//            BufferedImage biCroppedImage = null;
//            if(heightPix < widthPix) {
//                biCroppedImage = Scalr.crop(biOriginalImg, (widthPix - heightPix) / 2, 0, heightPix, heightPix);
//            }else{
//                biCroppedImage = Scalr.crop(biOriginalImg, 0, (heightPix - widthPix) / 2, widthPix, widthPix);
//            }
//            BufferedImage biFinalImage = Scalr.resize(biCroppedImage, AVATAR_TARGET_SIZE);
//            //save final image in local server machine and delete the rest
//            ImageIO.write(biFinalImage, "png", resizedPng);
//            biFinalImage.flush();
//            originalFileOutputStream.close();
//            originalImgFile.delete();
//            //save in AWS
//            String bucketName = "takeabreak";
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new InternalServerErrorException(
//                    "The server experienced some difficulties or provided image is not in proper file format." +
//                            "Try with different image. If the message appears again, try again later.");
//        }

        return null;
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
