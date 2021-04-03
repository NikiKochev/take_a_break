package takeABreak.service;

import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.bytedeco.javacv.FrameGrabber;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import takeABreak.exceptions.BadRequestException;

import takeABreak.exceptions.InternalServerErrorException;
import takeABreak.exceptions.NotAuthorizedException;

import takeABreak.model.dao.GCloudProperties;
import takeABreak.model.dao.PostDAO;
import takeABreak.model.dto.post.*;
import takeABreak.model.pojo.*;
import takeABreak.model.pojo.resizeAnimatedGif.GifUtil;
import takeABreak.model.repository.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import javax.transaction.Transactional;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static takeABreak.conf.VideoImage.randomGrabberFFmpegImage;


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
    private AddMediaToPostResponseDTO addMediaToPostResponseDTO;
    @Autowired
    private ContentService contentService;
    @Autowired
    private UserService userService;
    @Autowired
    private GCloudProperties gCloudProperties;
    @Autowired
    private FileTypeRepository fileTypeRepository;

    @Transactional
    public AddingResponsePostDTO addPost(AddingRequestPostDTO postDTO, User user, String sessionId) {

        System.out.println(postDTO.getFileType());
        System.out.println(postDTO.getImageCode());

        if (!postDTO.getFileType().equals("gif") && !postDTO.getFileType().equals("jpg")) {
            throw new BadRequestException("The provided file type is invalid.");
        }

        String[] imageSizes = {"", "_2", "_3", "_4"};

        for (int i = 0; i < imageSizes.length; i++) {

            String imageURL =
                    gCloudProperties.getCloudBucketUrl()
                            + sessionId + "_"
                            + addMediaToPostResponseDTO.getImageCode()
                            + imageSizes[i] + "."
                            + addMediaToPostResponseDTO.getFileType();
            System.out.println(imageURL);
            try {
                URL url = new URL(imageURL);
                HttpURLConnection huc = (HttpURLConnection) url.openConnection();
                int responseCode = huc.getResponseCode();

                System.out.println(responseCode);

                if (responseCode != 200) {
                    throw new BadRequestException("The provided image code or type is not found.");
                }

            } catch (Exception e) {
                e.printStackTrace();
                throw new BadRequestException("The provided image code or type is not found.");
            }

            int fileType = 0;
            if (addMediaToPostResponseDTO.getFileType().equals("jpg")) {
                fileType = 1;
            } else {
                fileType = 2;
            }
            Content content = contentService.findById(fileType);

            Category category = categoryService.findById(postDTO.getCategoryId());
            Post post = new Post();
            post.setCategory(category);
            post.setUser(user);
            post.setTitle(postDTO.getTitle());
            post.setContent(content);
            post.setCreatedAt(LocalDate.now());
            if (postDTO.getDescription() != null) {
                post.setDescription(postDTO.getDescription());
            }
        }
        postRepository.save(null);
        return new AddingResponsePostDTO(null);
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

    public AddMediaToPostResponseDTO addImageToPost(MultipartFile multipartFile, String sessionId){

        //get file extension
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

        //check file size
        if(multipartFile.getSize() > 10485760){//10 MB
            throw new BadRequestException("Uploaded image should not exceed 10MB");
        }

        //save original file into the temp dir
        String dir = TempDir.getLocation();
        Long imageCodeLong = System.nanoTime();
        String imageCode = imageCodeLong.toString();
//        String originalName = sessionId + "_" + imageCode;
        String originalName = imageCode;

        String locationOriginalImg = dir + File.separator + originalName + "." + extension;
        File originalFile = new File(locationOriginalImg);
        try(OutputStream originalFileOutputStream = new FileOutputStream(originalFile);){
            originalFileOutputStream.write(multipartFile.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalServerErrorException("The server experienced some difficulties, try again later.");
        }

        //check for the image ratio. Should be max 1:4
        BufferedImage biOriginalImg = null;
        try {
            biOriginalImg = ImageIO.read(originalFile);double widthDouble = biOriginalImg.getWidth();
            double heightDouble = biOriginalImg.getHeight();
            if(widthDouble / heightDouble < 0.25 || widthDouble / heightDouble > 4){
                throw new BadRequestException("The image should be with max 1:4 or 4:1 ratio.");
            }
            biOriginalImg.flush();
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalServerErrorException("The server experienced some difficulties, try again later.");
        }


        String smallName = originalName + "_" + SMALL_IMAGE_CODE;
        String mediumName = originalName + "_" + MEDIUM_IMAGE_CODE;
        String largeName = originalName + "_" + LARGE_IMAGE_CODE;

        //Resize depending of type Still Image or GIF
        if(extension.equals("gif")){
            resizeGif(dir, originalName, smallName, mediumName, largeName);
        }else{
            resizeStillImage(dir, originalName, smallName, mediumName, largeName, extension);
        }

        //Save in Google Cloud
        ArrayList<String> fileNames = new ArrayList<>();
        fileNames.add(originalName);
        fileNames.add(smallName);
        fileNames.add(mediumName);
        fileNames.add(largeName);

        try {
            for (int i = 0; i < fileNames.size(); i++) {

                if(!extension.equals("gif")){
                    extension = "jpg";
                }

                File file = new File(dir + File.separator + fileNames.get(i) + "." + extension);

                Credentials credentials = GoogleCredentials
                        .fromStream(new FileInputStream(gCloudProperties.getCredentials()));
                Storage storage = StorageOptions.newBuilder().setCredentials(credentials)
                        .setProjectId(gCloudProperties.getProjectId()).build().getService();
                Bucket bucket = storage.get(gCloudProperties.getBucket());

                InputStream inStreamFinalImage = new FileInputStream(file);
                Blob blob = bucket.create(fileNames.get(i) + "." + extension, inStreamFinalImage);

                //delete files from temp folder
                inStreamFinalImage.close();
                file.delete();

            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalServerErrorException(
                    "The server experienced some difficulties, try again later.");
        }

        //Make content object
        int fileTypeCode = 1;
        if(extension.equals("gif")){
            fileTypeCode = 2;
        }

        Content content = new Content();
        Optional<FileType> fileTypeOps = fileTypeRepository.findById(fileTypeCode);
        if (fileTypeOps.isPresent()){
            FileType fileType = fileTypeOps.get();
            content.setFileType(fileType);
        }
//        content.setId(originalName);


        if(extension.equals("gif")){
            addMediaToPostResponseDTO.setFileType("gif");
        }else{
            addMediaToPostResponseDTO.setFileType("jpg");
        }
        addMediaToPostResponseDTO.setImageCode(imageCode);

        return addMediaToPostResponseDTO;
    }

    private void resizeStillImage(String dir, String originalName, String smallName, String mediumName, String largeName, String extension){

        String locationOriginalImg = dir + File.separator + originalName + "." + extension;
        File originalFile = new File(locationOriginalImg);
        //rename JPEG to JPG
        if(extension.equals("jpeg")){
            File copiedFile = new File(dir + File.separator + originalName + ".jpg");
            originalFile.renameTo(copiedFile);
            originalFile = new File(dir + File.separator + originalName + ".jpg");
            extension = "jpg";
        }

        try {
            BufferedImage biOriginalImg = ImageIO.read(originalFile);

            //in case the image is NOT GIF or JPG, convert it to JPG
            HashSet<String> typesToBeConverted = new HashSet<>();
            typesToBeConverted.add("mbp");
            typesToBeConverted.add("png");
            typesToBeConverted.add("wbmp");

            if(typesToBeConverted.contains(extension)){
                File copiedFile = new File(dir + File.separator + originalName + "_copy." + extension);
                originalFile.renameTo(copiedFile); //Rename original file
                File originalFileName = new File(dir + File.separator + originalName + STILL_IMAGE_TYPE);
                BufferedImage duplicateBufferedImage = ImageIO.read(copiedFile);
                ImageIO.write(duplicateBufferedImage, "jpg", originalFileName);//save original file in JPG format
                duplicateBufferedImage.flush();
                copiedFile.delete();
                extension = "jpg";
            }

            //resize and save in small size
            File file = new File(dir + File.separator + originalName + "." + extension);
            BufferedImage biOriginalSize = ImageIO.read(file);
            File smallSize = new File(dir + File.separator + originalName + "_" + SMALL_IMAGE_CODE + STILL_IMAGE_TYPE);
            if(biOriginalImg.getWidth() > SMALL_SIZE_WIGHT) {
                BufferedImage biSmallSize = Scalr.resize(biOriginalSize, SMALL_SIZE_WIGHT);
                ImageIO.write(biSmallSize, extension, smallSize);
                biSmallSize.flush();
            }else{
                ImageIO.write(biOriginalSize, extension, smallSize);
            }
            //resize and save in medium size
            File mediumSize = new File(dir + File.separator + originalName + "_" + MEDIUM_IMAGE_CODE + STILL_IMAGE_TYPE);
            if(biOriginalImg.getWidth() > MEDIUM_SIZE_WIGHT) {
                BufferedImage biMediumSize = Scalr.resize(biOriginalSize, MEDIUM_SIZE_WIGHT);
                ImageIO.write(biMediumSize, extension, mediumSize);
                biMediumSize.flush();
            }else{
                ImageIO.write(biOriginalSize, extension, mediumSize);
            }
            //resize and save in large size
            File largeSize = new File(dir + File.separator + originalName + "_" + LARGE_IMAGE_CODE + STILL_IMAGE_TYPE);
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
    }

    private void resizeGif(String dir, String originalName, String smallName, String mediumName, String largeName){

        int[] imageSizes = {SMALL_SIZE_WIGHT, MEDIUM_SIZE_WIGHT, LARGE_SIZE_WIGHT};
        String[] imageNames = {smallName, mediumName, largeName};

        for (int i = 0; i < imageSizes.length; i++) {
            String originalLocation = dir + File.separator + originalName + ".gif";
            String resizedLocation = dir + File.separator + imageNames[i] + ".gif";
            File originalFile = new File(originalLocation);
            File destFile = new File(resizedLocation);

            try {
                GifUtil.gifInputToOutput(originalFile, destFile, imageSizes[i]);
            } catch (Exception e) {
                e.printStackTrace();
                throw new InternalServerErrorException(
                        "The server experienced some difficulties or provided image is not in proper file format." +
                                "Try with different image. If the message appears again, try again later.");
            }
        }

    }

    public AddMediaToPostResponseDTO addVideoToPost(MultipartFile multipartFile, String sessionId){

        //get file extension
        String extension = multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().lastIndexOf(".") + 1);
        extension = extension.toLowerCase();

        //check if the file format is supported
        HashSet<String> supportedImageFormats = new HashSet();
        supportedImageFormats.add("avi");
        supportedImageFormats.add("asf");
        supportedImageFormats.add("flv");
        supportedImageFormats.add("mp4");
        supportedImageFormats.add("mpeg");
        supportedImageFormats.add("mpg");
        supportedImageFormats.add("webm");
        supportedImageFormats.add("3g2");
        supportedImageFormats.add("mkv");
        supportedImageFormats.add("m4v");
        supportedImageFormats.add("mov");
        if(!supportedImageFormats.contains(extension)){
            throw new BadRequestException("Unsupported file type.");
        }

        //Max accepted is limited by spring in application.properties

        //save original file into the temp dir
        String dir = TempDir.getLocation();
        Long mediaCodeLong = System.currentTimeMillis();
        String mediaCode = mediaCodeLong.toString();
        String originalName = sessionId + "_" + mediaCode;
        String locOriginalMedia = dir + File.separator + originalName + "." + extension;
        File originalFile = new File(locOriginalMedia);
        try(OutputStream originalFileOutputStream = new FileOutputStream(originalFile);){
            originalFileOutputStream.write(multipartFile.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalServerErrorException("The server experienced some difficulties, try again later.");
        }

        //thumbnails
        try {
            System.out.println(randomGrabberFFmpegImage(locOriginalMedia, 2));
        } catch (FrameGrabber.Exception e) {
            e.printStackTrace();
        }


        return addMediaToPostResponseDTO;
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
        System.out.println("тука продължава");
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
