package takeABreak.service;

import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import net.bytebuddy.utility.RandomString;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import takeABreak.controller.UserController;
import takeABreak.exceptions.AuthenticationException;
import takeABreak.exceptions.BadRequestException;
import takeABreak.exceptions.InternalServerErrorException;
import takeABreak.model.dao.GCloudProperties;
import takeABreak.model.dao.UserDao;
import takeABreak.model.dto.user.*;
import takeABreak.model.pojo.TempDir;
import takeABreak.model.pojo.User;
import takeABreak.model.repository.UserRepository;
import java.io.*;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;


@Service
public class UserService {

    public static final int AVATAR_TARGET_SIZE = 200;
    public static final String AVATAR_IMAGE_TYPE = ".png";
    @Autowired
    private UserRepository repository;
    @Autowired
    private UserDao userDAO;
    @Autowired
    private EmailService emailService;
    @Autowired
    private CountryService countryService;
    @Autowired
    private GCloudProperties gCloudProperties;

    public RegisterResponseUserDTO addUser(RegisterRequestUserDTO userDTO) {
        checkForPropertyCredential(userDTO);
        userDTO.setPassword(hashPassword(userDTO.getPassword()));
        User user = new User(userDTO,verificationCode());
        repository.save(user);
        sendEmail(user);
        return new RegisterResponseUserDTO(user);
    }

    private void sendEmail(User user) {
        Thread t= new Thread(() -> emailService.sendSimpleMessage(user));
        t.start();
    }

    private void checkForPropertyCredential(RegisterRequestUserDTO userDTO) {
        if (!userDTO.getPassword().equals(userDTO.getConfirmPassword())) {
            throw new BadRequestException("Passwords are not equals");
        }
        if (repository.findByEmail(userDTO.getEmail()) != null) {
            throw new BadRequestException("You already have account");
        }
    }

    private String hashPassword(String password) {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(password);
    }

    private String verificationCode() {
        return RandomString.make(64);
    }

    public UploadAvatarDTO addAvatar(MultipartFile multipartFile, User user) {

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

        String dir = TempDir.getLocation();
        String imgName = "userId_" + user.getId() +"_" + System.currentTimeMillis();
        String locationOriginalImg = dir + File.separator + imgName + "." + extension;
        File file = new File(locationOriginalImg);
        String resizedPngLocation = dir + File.separator + imgName + "_resized" + AVATAR_IMAGE_TYPE;
        File resizedPng = new File(resizedPngLocation);
        try{
            //write original file in temp dir
            OutputStream originalFileOutputStream = new FileOutputStream(file);
            originalFileOutputStream.write(multipartFile.getBytes());
            //resize, crop and convert original file to PNG and save it in the temp dir
            File originalImgFile = new File(locationOriginalImg);
            BufferedImage biOriginalImg = ImageIO.read(originalImgFile);

            int widthPix = biOriginalImg.getWidth();
            int heightPix = biOriginalImg.getHeight();
            BufferedImage biCroppedImage = null;
            if(heightPix < widthPix) {
                biCroppedImage = Scalr.crop(biOriginalImg, (widthPix - heightPix) / 2, 0, heightPix, heightPix);
            }else{
                biCroppedImage = Scalr.crop(biOriginalImg, 0, (heightPix - widthPix) / 2, widthPix, widthPix);
            }
            BufferedImage biFinalImage = Scalr.resize(biCroppedImage, AVATAR_TARGET_SIZE);
            //save final image in local server machine and delete the rest
            ImageIO.write(biFinalImage, "png", resizedPng);
            originalFileOutputStream.close();
            originalImgFile.delete();
            biFinalImage.flush();
            //save in Google Cloud
            Credentials credentials = GoogleCredentials
                    .fromStream(new FileInputStream(gCloudProperties.getCredentials()));
            Storage storage = StorageOptions.newBuilder().setCredentials(credentials)
                    .setProjectId(gCloudProperties.getProjectId()).build().getService();
            Bucket bucket = storage.get(gCloudProperties.getBucket());

            InputStream inStreamFinalImage = new FileInputStream(resizedPngLocation);
            Blob blob = bucket.create(imgName + AVATAR_IMAGE_TYPE, inStreamFinalImage);

        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalServerErrorException(
                    "The server experienced some difficulties or provided image is not in proper file format." +
                            "Try with different image. If the message appears again, try again later.");
        }

        user.setAvatar(gCloudProperties.getCloudBucketUrl() + imgName + AVATAR_IMAGE_TYPE);
        repository.save(user);
        UploadAvatarDTO avatar = new UploadAvatarDTO(repository.findById(user.getId()).get().getAvatar(), user.getId());
        return avatar;
    }

    public LoginUserResponseDTO login(LoginUserRequestDTO dto) {
        User user = repository.findByEmail(dto.getEmail());
        if(!user.isVerify()){
            throw new BadRequestException("You must verify yor account");
        }
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        //that is missing
        if (user == null || !encoder.matches(dto.getPassword(), user.getPassword())) {
            throw new AuthenticationException("wrong credentials");
        }
        return new LoginUserResponseDTO(user);
    }

    public LoginUserResponseDTO getById(int id) {
        return new LoginUserResponseDTO(findById(id));
    }

    public byte[] getAvatar(User user) throws IOException {//todo remove
        File file = new File(user.getAvatar());
        return Files.readAllBytes(file.toPath());
    }

    public UserDeleteResponseDTO deleteDate(User user) {
        String date = LocalDate.now().toString();
        user.setDeletedAt(LocalDate.now());
        user.setAvatar(date);
        user.setCity(null);
        user.setCountry(null);
        user.setEmail(date);
        user.setFirstName(null);
        user.setLastName(null);
        repository.save(user);
        return new UserDeleteResponseDTO(user);
    }

    public LoginUserResponseDTO editUser(User loggedUser, EditResponseUserDTO userDTO) {
        if(userDTO.getAge() != 0){
            loggedUser.setAge(userDTO.getAge());
        }
        if(userDTO.getFirstName() != null){
            loggedUser.setFirstName(userDTO.getFirstName());
        }
        if(userDTO.getLastName() != null){
            loggedUser.setLastName(userDTO.getLastName());
        }
        if(userDTO.getEmail() != null){
            loggedUser.setEmail(userDTO.getEmail());
        }
        if(userDTO.getCity() != null){
            loggedUser.setCity(userDTO.getCity());
        }
        if(userDTO.getCountry() >= 0){
            loggedUser.setCountry(countryService.findById(userDTO.getCountry()));
        }
        if(userDTO.getPassword() != null && userDTO.getFerifyPassword().compareTo(userDTO.getPassword()) == 0){
            checkForPassword(loggedUser, userDTO);
        }

        repository.save(loggedUser);
        return new LoginUserResponseDTO(loggedUser);
    }

    private void checkForPassword(User loggedUser, EditResponseUserDTO userDTO) {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(userDTO.getOldPassword(), loggedUser.getPassword())) {
            throw new AuthenticationException("wrong credentials");
        }
        loggedUser.setPassword(hashPassword(userDTO.getPassword()));
    }

    public SearchForUsersResponseDTO findUsers(SearchForUsersRequestDTO searchDTO) {
        return new SearchForUsersResponseDTO(userDAO.findBy(searchDTO));
    }

    public void save(User user) {
        repository.save(user);
    }

    public User findById(int userId) {
        Optional<User> user = repository.findById(userId);
        if(user.isPresent()){
            System.out.println("и тука");
            return user.get();
        }
        throw  new BadRequestException("No such person");
    }

    public LoginUserResponseDTO findByVerificationCode(String code) {
        User user = repository.findByVerification(code);
        user.setVerify(true);
        repository.save(user);
        return new LoginUserResponseDTO(user);
    }
}
