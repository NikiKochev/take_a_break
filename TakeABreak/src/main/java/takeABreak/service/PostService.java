package takeABreak.service;

import org.bytedeco.javacv.FrameGrabber;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import takeABreak.exceptions.BadRequestException;

import takeABreak.exceptions.InternalServerErrorException;
import takeABreak.exceptions.NotAuthorizedException;

import takeABreak.model.dao.FormatTypeDAO;
import takeABreak.model.dao.GCloud;
import takeABreak.model.dao.PostDAO;
import takeABreak.model.dto.post.*;
import takeABreak.model.pojo.*;
import takeABreak.model.pojo.resizeAnimatedGif.GifUtil;
import takeABreak.model.repository.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import javax.transaction.Transactional;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static takeABreak.model.pojo.VideoImage.randomGrabberFFmpegImage;

import com.cloudmersive.client.invoker.ApiClient;
import com.cloudmersive.client.invoker.Configuration;
import com.cloudmersive.client.invoker.auth.*;
import com.cloudmersive.client.VideoApi;


@Service
public class PostService {

    public static final String STILL_IMAGE_TYPE = ".jpg";
    public static final int SMALL_SIZE_WIGHT = 460;
    public static final int MEDIUM_SIZE_WIGHT = 650;
    public static final int LARGE_SIZE_WIGHT = 2560;
    public static final int VIDEO_RESIZED_SIZE = MEDIUM_SIZE_WIGHT;
    public static final int SMALL_SIZE_CODE = 2;
    public static final int MEDIUM_SIZE_CODE = 3;
    public static final int LARGE_SIZE_CODE = 4;

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private PostDAO postDAO;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private AddMediaToPostResponseDTO addMediaToPostResponseDTO;
    @Autowired
    private UserService userService;
    @Autowired
    private GCloud gCloud;
    @Autowired
    private FileTypeRepository fileTypeRepository;
    @Autowired
    private ContentRepository contentRepository;
    @Autowired
    FormatTypeDAO formatTypeDAO;
    @Autowired
    CategoryRepository categoryRepository;
    @Value("${cloudmersive.credentials}")
    private String cloudmersiveKey;

    @Transactional
    public AddingResponsePostDTO addPost(AddingRequestPostDTO postDTO, User user, String sessionId) {

        List<Content> contentWithSession = contentRepository.findAllBySession(sessionId);

        if (contentWithSession.size() == 0) {
            throw new BadRequestException("Invalid session for that content. Try to logout and login again.");
        }

        Content content = contentWithSession.get(contentWithSession.size() - 1);

        if (!content.getSession().equals(sessionId) || content.getId() != postDTO.getContentId()) {
            throw new BadRequestException("Invalid session for that content. Try to logout and login again.");
        }

        Post post = new Post();

        post.setTitle(postDTO.getTitle());

        post.setDescription(postDTO.getDescription());

        Optional<Category> categoryOps = categoryRepository.findById(postDTO.getCategoryId());
        if (categoryOps.isPresent()){
            Category category = categoryOps.get();
            post.setCategory(category);
        }

        post.setAdultContent(postDTO.isAdultContent());

        post.setContent(content);

        post.setCreatedAt(LocalDate.now());

        post.setUser(user);

        postRepository.save(post);

        return new AddingResponsePostDTO(post);
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
        Long imageCodeLong = System.currentTimeMillis();
        String imageCode = imageCodeLong.toString();
        imageCode = imageCode.substring(imageCode.length()-8);
        String originalName = sessionId + "_" + imageCode;

        String locationOriginalImg = dir + File.separator + originalName + "." + extension;
        File originalFile = new File(locationOriginalImg);
        try(OutputStream originalFileOutputStream = new FileOutputStream(originalFile);){
            originalFileOutputStream.write(multipartFile.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalServerErrorException("The server experienced some difficulties, try again later.");
        }

        //check for the image ratio. Should be max 1:4
        try {
            BufferedImage biOriginalImg = ImageIO.read(originalFile);
            double widthDouble = biOriginalImg.getWidth();
            double heightDouble = biOriginalImg.getHeight();
            if(widthDouble / heightDouble < 0.25 || widthDouble / heightDouble > 4){
                originalFile.delete();
                throw new BadRequestException("Try with different file or file that is between 1:4 or 4:1 ratio.");
            }
            biOriginalImg.flush();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }

        //file names
        ArrayList<String> fileNames = new ArrayList<>();
        fileNames.add(originalName);
        fileNames.add(originalName + "_" + SMALL_SIZE_CODE);
        fileNames.add(originalName + "_" + MEDIUM_SIZE_CODE);
        fileNames.add(originalName + "_" + LARGE_SIZE_CODE);

        //Resize depending of type Still Image or GIF
        if(extension.equals("gif")){
            resizeGif(dir, fileNames);
        }else{
            resizeStillImage(dir, originalName, extension, false);
        }

        //Save in Google Cloud
        try {
            for (int i = 0; i < fileNames.size(); i++) {

                if(!extension.equals("gif")){
                    extension = "jpg";
                }

                String filePath = dir + File.separator + fileNames.get(i) + "." + extension;
                gCloud.addToGCloudAndDeleteFromLocal(filePath, fileNames.get(i) + "." + extension);

            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalServerErrorException("The server experienced some difficulties, try again later.");
        }

        //Make content object and save in DB
        int fileTypeCode = 0;
        if(extension.equals("gif")){
            fileTypeCode = 2;
        }
        if(extension.equals("jpg")){
            fileTypeCode = 1;
        }else {
            fileTypeCode = 3;
        }

        Content content = new Content();
        Optional<FileType> fileTypeOps = fileTypeRepository.findById(fileTypeCode);
        if (fileTypeOps.isPresent()){
            FileType fileType = fileTypeOps.get();
            content.setFileType(fileType);
        }

        int imageCodeInt = Integer.parseInt(imageCode);
        content.setId(imageCodeInt);
        content.setSession(sessionId);
        content.setCreatedAt(LocalDateTime.now());
        contentRepository.save(content);

        //make 4 formatType objects and save them in DB
        for (int i = 1; i < fileNames.size()+1; i++) {

            String url = gCloud.getCloudBucketUrl() + fileNames.get(i-1) + "." + extension;
            formatTypeDAO.saveFormatType(i, url, content.getId());
        }

        addMediaToPostResponseDTO.setContentId(content.getId());

        return addMediaToPostResponseDTO;
    }

    private void resizeStillImage(String dir, String originalName, String extension, boolean isThumbnail){

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
            File smallSize = new File(dir + File.separator + originalName + "_" + SMALL_SIZE_CODE + STILL_IMAGE_TYPE);
            if(biOriginalImg.getWidth() > SMALL_SIZE_WIGHT) {
                BufferedImage biSmallSize = Scalr.resize(biOriginalSize, SMALL_SIZE_WIGHT);
                ImageIO.write(biSmallSize, extension, smallSize);
                biSmallSize.flush();
            }else{
                ImageIO.write(biOriginalSize, extension, smallSize);
            }
            //resize and save in medium size
            File mediumSize = new File(dir + File.separator + originalName + "_" + MEDIUM_SIZE_CODE + STILL_IMAGE_TYPE);
            if(biOriginalImg.getWidth() > MEDIUM_SIZE_WIGHT) {
                BufferedImage biMediumSize = Scalr.resize(biOriginalSize, MEDIUM_SIZE_WIGHT);
                ImageIO.write(biMediumSize, extension, mediumSize);
                biMediumSize.flush();
            }else{
                ImageIO.write(biOriginalSize, extension, mediumSize);
            }

            if (isThumbnail){
                return;
            }
            //resize and save in large size
            File largeSize = new File(dir + File.separator + originalName + "_" + LARGE_SIZE_CODE + STILL_IMAGE_TYPE);
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

    private void resizeGif(String dir, ArrayList<String> fileNames){

        int[] imageSizes = {SMALL_SIZE_WIGHT, MEDIUM_SIZE_WIGHT, LARGE_SIZE_WIGHT};

        for (int i = 0; i < imageSizes.length; i++) {
            String originalLocation = dir + File.separator + fileNames.get(0) + ".gif";
            String resizedLocation = dir + File.separator + fileNames.get(i+1) + ".gif";
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
        mediaCode = mediaCode.substring(mediaCode.length()-8);
        String originalName = sessionId + "_" + mediaCode;
        String locOriginalMedia = dir + File.separator + originalName + "." + extension;

        File originalFile = new File(locOriginalMedia);
        try(OutputStream originalFileOutputStream = new FileOutputStream(originalFile);){
            originalFileOutputStream.write(multipartFile.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalServerErrorException("The server experienced some difficulties, try again later.");
        }

        //file names
        ArrayList<String> fileNames = new ArrayList<>();
        fileNames.add(originalName);
        fileNames.add(originalName + "_" + SMALL_SIZE_CODE);
        fileNames.add(originalName + "_" + MEDIUM_SIZE_CODE);
        fileNames.add(originalName + "_" + LARGE_SIZE_CODE);


        //send for conversion to Cloudmersive
        ApiClient defaultClient = Configuration.getDefaultApiClient();

        // Configure API key authorization: Apikey
        ApiKeyAuth Apikey = (ApiKeyAuth) defaultClient.getAuthentication("Apikey");
        Apikey.setApiKey(cloudmersiveKey);
        // Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
        //Apikey.setApiKeyPrefix("Token");

        VideoApi apiInstance = new VideoApi();
        File inputFile = new File(locOriginalMedia); // File | Input file to perform the operation on.
        String fileUrl = null; // String | Optional; URL of a video file being used for conversion. Use this option for files larger than 2GB.
        Integer maxWidth = VIDEO_RESIZED_SIZE; // Integer | Optional; Maximum width of the output video, up to the original video width. Defaults to original video width.
        Integer maxHeight = VIDEO_RESIZED_SIZE; // Integer | Optional; Maximum height of the output video, up to the original video width. Defaults to original video height.
        Boolean preserveAspectRatio = true; // Boolean | Optional; If false, the original video's aspect ratio will not be preserved, allowing customization of the aspect ratio using maxWidth and maxHeight, potentially skewing the video. Default is true.
        Integer frameRate = 30; // Integer | Optional; Specify the frame rate of the output video. Defaults to original video frame rate.
        Integer quality = 70; // Integer | Optional; Specify the quality of the output video, where 100 is lossless and 1 is the lowest possible quality with highest compression. Default is 50.
        try {

            byte[] result = apiInstance.videoConvertToMp4(inputFile, fileUrl, maxWidth, maxHeight, preserveAspectRatio, frameRate, quality, true);
            File resizedFile = new File(dir + File.separator + fileNames.get(3) + ".mp4");
            OutputStream resizedFileOutputStream = new FileOutputStream(resizedFile);
            resizedFileOutputStream.write(result);
            resizedFileOutputStream.close();

        } catch (Exception e) {
            System.err.println("Exception when calling VideoApi#videoConvertToMp4");
            e.printStackTrace();
            throw new BadRequestException("You can upload max 3.5MB video files");
        }

        //thumbnails
        //make in original size
        try {
            randomGrabberFFmpegImage(locOriginalMedia, 2);
        } catch (FrameGrabber.Exception e) {
            e.printStackTrace();
        }
        //resize
        resizeStillImage(dir, originalName, "jpg", true);
        File file = new File(dir + File.separator + originalName + ".jpg");
        file.delete();

        //Save in GCloud
        ArrayList<String> fileNamesWithExtension = new ArrayList<>();
        fileNamesWithExtension.add(fileNames.get(0) + "." + extension);
        fileNamesWithExtension.add(fileNames.get(1) + ".jpg");
        fileNamesWithExtension.add(fileNames.get(2) + ".jpg");
        fileNamesWithExtension.add(fileNames.get(3) + ".mp4");

        ArrayList<String> filePaths = new ArrayList();
        filePaths.add(dir + File.separator + fileNamesWithExtension.get(0));
        filePaths.add(dir + File.separator + fileNamesWithExtension.get(1));
        filePaths.add(dir + File.separator + fileNamesWithExtension.get(2));
        filePaths.add(dir + File.separator + fileNamesWithExtension.get(3));

        for (int i = 0; i < filePaths.size(); i++) {
            gCloud.addToGCloudAndDeleteFromLocal(filePaths.get(i), fileNamesWithExtension.get(i));
        }

        //Save in DB
        Content content = new Content();
        Optional<FileType> fileTypeOps = fileTypeRepository.findById(3);
        if (fileTypeOps.isPresent()){
            FileType fileType = fileTypeOps.get();
            content.setFileType(fileType);
        }

        int imageCodeInt = Integer.parseInt(mediaCode);
        content.setId(imageCodeInt);
        content.setSession(sessionId);
        content.setCreatedAt(LocalDateTime.now());
        contentRepository.save(content);

        //make 4 formatType objects and save them in DB
        for (int i = 1; i < fileNames.size()+1; i++) {

            String url = gCloud.getCloudBucketUrl() + fileNamesWithExtension.get(i-1);
            formatTypeDAO.saveFormatType(i+4, url, content.getId());

        }

        addMediaToPostResponseDTO.setContentId(content.getId());

        return addMediaToPostResponseDTO;
    }

    @Transactional
    public AddingResponsePostDTO editPost(EditingRequestPostDTO postDTO, User user, String sessionId){

        Post postOld = new Post();

        Optional<Post> postOldOps = postRepository.findById(postDTO.getPostId());
        if (postOldOps.isPresent()) {
            postOld = postOldOps.get();
        }else{
            throw new BadRequestException("Non existing post");
        }

        //check credentials (Is the user owner of the post)
        if (user.getId() != postOld.getUser().getId()){
            throw new NotAuthorizedException("You are not the owner of the post.");
        }

        //editing post
        Post post = new Post();

        if(postDTO.getContentId() != 0) {
            List<Content> contentWithSession = contentRepository.findAllBySession(sessionId);
            if (contentWithSession.size() == 0) {
                throw new NotAuthorizedException("Invalid session for that content. Try to logout and login again.");
            }

            Content content = contentWithSession.get(contentWithSession.size() - 1);

            if (!content.getSession().equals(sessionId) || content.getId() != postDTO.getContentId()) {
                throw new NotAuthorizedException("Invalid session for that content. Try to logout and login again.");
            }
            post.setContent(content);
        }else{
           Optional<Content> contentOps = contentRepository.findById(postOld.getContent().getId());
            if (contentOps.isPresent()){
                Content content = contentOps.get();
                post.setContent(content);
            }
        }

        if(postDTO.getDescription() != null){
            post.setDescription(postDTO.getDescription());
        }else{
            post.setDescription(postOld.getDescription());
        }

        if (postDTO.getTitle() != null){
            post.setTitle(postDTO.getTitle());
        }else{
            post.setTitle(postOld.getTitle());
        }

        if (postDTO.getCategoryId() != 0){
            Optional<Category> categoryOps = categoryRepository.findById(postDTO.getCategoryId());
            if (categoryOps.isPresent()){
                Category category = categoryOps.get();
                post.setCategory(category);
            }
        }else{
            Optional<Category> categOps = categoryRepository.findById(postDTO.getPostId());
            if (categOps.isPresent()){
                Category categOld = categOps.get();
                post.setCategory(categOld);
            }
        }

        post.setCreatedAt(postOld.getCreatedAt());

        post.setId(postDTO.getPostId());

        post.setUser(user);

        postRepository.save(post);

        return new AddingResponsePostDTO(post);
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
        if(!user.getPosts().contains(post)){
            throw new NotAuthorizedException("Trying to delete a post that you are not an owner of.");
        }
        postRepository.delete(post);
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
