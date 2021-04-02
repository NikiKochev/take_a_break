package takeABreak.service;

import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import takeABreak.exceptions.BadRequestException;

import takeABreak.exceptions.InternalServerErrorException;
import takeABreak.exceptions.NotAuthorizedException;

import takeABreak.model.dao.PostDAO;
import takeABreak.model.dto.post.*;
import takeABreak.model.dto.user.UploadAvatarDTO;
import takeABreak.model.pojo.*;
import takeABreak.model.repository.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import javax.transaction.Transactional;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static takeABreak.service.UserService.AVATAR_TARGET_SIZE;

@Service
public class PostService {
    public static final String STILL_IMAGE_TYPE = ".jpg";
    public static final int SMALL_SIZE_WIGHT = 460;
    public static final int MEDIUM_SIZE_WIGHT = 650;
    public static final int LARGE_SIZE_WIGHT = 2560;
    public static final int SMALL_IMAGE_CODE = 2;
    public static final int MEDIUM_IMAGE_CODE = 3;
    public static final int LARGE_IMAGE_CODE = 4;

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

    public AddImageToPostResponseDTO addImageToPost(MultipartFile multipartFile, int userId){
        //get file extension
        String extension = multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().lastIndexOf(".") + 1);
        extension = extension.toLowerCase();
        System.out.println(extension);

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

        //check file size
        if(multipartFile.getSize() > 10485760){//10 MB
            throw new BadRequestException("Uploaded image should not exceed 10MB");
        }


        //save original file into the temp dir
        String dir = TempDir.getLocation();
        String imgName = userId + "_" + System.currentTimeMillis();
        String locationOriginalImg = dir + File.separator + imgName + "." + extension;
        File originalFile = new File(locationOriginalImg);
        try(OutputStream originalFileOutputStream = new FileOutputStream(originalFile);){
            originalFileOutputStream.write(multipartFile.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalServerErrorException("The server experienced some difficulties, try again later.");
        }

        //rename JPEG to JPG
        if(extension.equals("jpeg")){
            File copiedFile = new File(dir + File.separator + imgName + ".jpg");
            originalFile.renameTo(copiedFile);
            originalFile = new File(dir + File.separator + imgName + ".jpg");
            extension = "jpg";
        }

        try {
            //check for the image ratio. Should be max 1:4
            BufferedImage biOriginalImg = ImageIO.read(originalFile);
            double widthDouble = biOriginalImg.getWidth();
            double heightDouble = biOriginalImg.getHeight();
            if(widthDouble / heightDouble < 0.25 || widthDouble / heightDouble > 4){
                throw new BadRequestException("The image should be with max 1:4 or 4:1 ratio.");
            }
            biOriginalImg.flush();

            //in case the image is NOT GIF or JPG, convert it to JPG
            HashSet<String> typesToBeConverted = new HashSet<>(supportedImageFormats);
            typesToBeConverted.remove("jpg");
            typesToBeConverted.remove("jpeg");
            typesToBeConverted.remove("gif");

            if(typesToBeConverted.contains(extension)){
                //Rename original file
                File copiedFile = new File(dir + File.separator + imgName + "_copy." + extension);
                originalFile.renameTo(copiedFile);
                File originalFileName = new File(dir + File.separator + imgName + "." + STILL_IMAGE_TYPE);
                //save original file in JPG format
                BufferedImage duplicateBufferedImage = ImageIO.read(copiedFile);
                ImageIO.write(duplicateBufferedImage, "jpeg", originalFileName);
                duplicateBufferedImage.flush();
                copiedFile.delete();
            }


            //resize and save in small size
            File file = new File(dir + File.separator + imgName + "." + extension);
            BufferedImage biOriginalSize = ImageIO.read(file);
            File smallSize = new File(dir + File.separator + imgName + "_" + SMALL_IMAGE_CODE + STILL_IMAGE_TYPE);
            if(biOriginalImg.getWidth() > SMALL_SIZE_WIGHT) {
                BufferedImage biSmallSize = Scalr.resize(biOriginalSize, SMALL_SIZE_WIGHT);
                ImageIO.write(biSmallSize, extension, smallSize);
                biSmallSize.flush();
            }else{
                ImageIO.write(biOriginalSize, extension, smallSize);
            }
            //resize and save in medium size
            File mediumSize = new File(dir + File.separator + imgName + "_" + MEDIUM_IMAGE_CODE + STILL_IMAGE_TYPE);
            if(biOriginalImg.getWidth() > MEDIUM_SIZE_WIGHT) {
                BufferedImage biMediumSize = Scalr.resize(biOriginalSize, MEDIUM_SIZE_WIGHT);
                ImageIO.write(biMediumSize, extension, mediumSize);
                biMediumSize.flush();
            }else{
                ImageIO.write(biOriginalSize, extension, mediumSize);
            }
            //resize and save in large size
            File largeSize = new File(dir + File.separator + imgName + "_" + LARGE_IMAGE_CODE + STILL_IMAGE_TYPE);
            if(biOriginalImg.getWidth() > LARGE_SIZE_WIGHT) {
                BufferedImage biLargeSize = Scalr.resize(biOriginalSize, LARGE_SIZE_WIGHT);
                ImageIO.write(biLargeSize, extension, largeSize);
                biLargeSize.flush();
            }else{
                ImageIO.write(biOriginalSize, extension, largeSize);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalServerErrorException(
                    "The server experienced some difficulties or provided image is not in proper file format." +
                            "Try with different image. If the message appears again, try again later.");
        }

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
