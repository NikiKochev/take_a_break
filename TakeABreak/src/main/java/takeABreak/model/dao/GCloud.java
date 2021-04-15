package takeABreak.model.dao;

import com.google.api.client.util.Key;
import com.google.api.gax.paging.Page;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.KeyFactory;
import org.springframework.stereotype.Component;
import takeABreak.exceptions.InternalServerErrorException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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

    private Storage getCloudStorage(){

        Credentials credentials = null;
        try {
            credentials = GoogleCredentials
                    .fromStream(new FileInputStream(this.credentials));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Storage storage = StorageOptions.newBuilder().setCredentials(credentials)
                .setProjectId(this.projectId).build().getService();

        return storage;

    }

    public void addToGCloudAndDeleteFromLocal(String filePath, String fileName){

        File file = new File(filePath);
        Storage storage = getCloudStorage();
        Bucket bucket = storage.get(this.bucket);

        try{

        InputStream inStreamFinalImage = new FileInputStream(file);
        bucket.create(fileName, inStreamFinalImage);

        //delete files from temp folder
        inStreamFinalImage.close();
        file.delete();
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalServerErrorException("The server experienced some difficulties, try again later.");
        }

    }

    public boolean deleteFromGCloud(String fileName){

        Storage storage = getCloudStorage();

        BlobId b = BlobId.of(this.bucket, fileName);
        boolean deleted = storage.delete(b);

        return deleted;
    }
}
