package takeABreak.model.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Setter
@NoArgsConstructor
@Component
public class GCloudProperties {

    @Value("${gcloud.path}")
    private String cloudBucketUrl;
    @Value("${gcloud.project-id}")
    private String projectId;
    @Value("${gcloud.bucket}")
    private String bucket;
    @Value("${gcloud.credentials}")
    private String credentials;
}
