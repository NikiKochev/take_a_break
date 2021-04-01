package takeABreak.service;

import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import takeABreak.exceptions.BadRequestException;
import takeABreak.exceptions.InternalServerErrorException;
import takeABreak.exceptions.NotFoundException;
import takeABreak.model.dto.post.*;
import takeABreak.model.pojo.*;
import takeABreak.model.repository.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
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
    private CategoryRepository categoryRepository;
    @Autowired
    private SizeRepository sizeRepository;
    @Autowired
    private ContentRepository contentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AddImageToPostResponseDTO addImageToPostResponseDTO;


    public AddingResponsePostDTO addPost(AddingRequestPostDTO postDTO, User user) {
        Optional<Category> cat = categoryRepository.findById(postDTO.getCategoryId());
        if(!cat.isPresent()){
            throw new NotFoundException("Not such a category");
        }
        Category category = cat.get();
        if(user.getId() != postDTO.getUserId()){
            throw new BadRequestException("Not the same person");
        }
        Optional<Content> content = contentRepository.findById(postDTO.getContentId());
        if(!content.isPresent()){
            throw new BadRequestException("mo picture or video to upload");
        }
        Post post = new Post();
        post.setCategory(category);
        post.setUser(user);
        post.setTitle(postDTO.getTitle());
        post.setContent(content.get());
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
        FormatType formatType = new FormatType(content, f.getAbsolutePath(), sizeRepository.findById(1).get());
        formatList.add(formatType);
        // тук ще трябва да са въведени всички видове снимки или видеа
        content.setFormatTypes(formatList);
        contentRepository.save(content);
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
        userRepository.save(user);
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
        userRepository.save(user);
        return new DisLikeResponsePostDTO(post, user,true);
    }

}
