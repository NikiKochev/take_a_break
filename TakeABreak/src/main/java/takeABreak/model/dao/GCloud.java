package takeABreak.model.dao;

import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import takeABreak.exceptions.InternalServerErrorException;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

@Getter
@Setter
@NoArgsConstructor
@Component
public class GCloud {

    @Value("${gcloud.path}")
    private String cloudBucketUrl;
    @Value("${gcloud.project-id}")
    private String projectId;
    @Value("${gcloud.bucket}")
    private String bucket;
    @Value("${gcloud.credentials}")
    private String credentials;

    public void addToGCloudAndDeleteFromLocal(String filePath, String fileName){

        File file = new File(filePath);

        try{
        Credentials credentials = GoogleCredentials
                .fromStream(new FileInputStream(this.credentials));
        Storage storage = StorageOptions.newBuilder().setCredentials(credentials)
                .setProjectId(this.projectId).build().getService();
        Bucket bucket = storage.get(this.bucket);

        InputStream inStreamFinalImage = new FileInputStream(file);
        Blob blob = bucket.create(fileName, inStreamFinalImage);

        //delete files from temp folder
        inStreamFinalImage.close();
        file.delete();
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalServerErrorException("The server experienced some difficulties, try again later.");
        }

    }
}
