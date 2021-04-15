package takeABreak.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import takeABreak.model.dao.ContentDAO;
import takeABreak.model.dao.GCloud;
import takeABreak.model.pojo.Content;
import takeABreak.model.pojo.FormatType;
import takeABreak.model.repository.ContentRepository;
import takeABreak.model.repository.FormatTypeRepository;

import java.util.List;

@Configuration
@EnableScheduling
public class ScheduledTasks {

    @Autowired
    ContentDAO contentDAO;

    @Autowired
    ContentRepository contentRepository;

    @Autowired
    GCloud gCloud;

    @Autowired
    FormatTypeRepository formatTypeRepository;

    @Scheduled(cron = "0 0 3 * * *")//daily at 03.00
    public void deleteUnusedContent(){

        //find content without post older than 1 hour
        List<Content> contentWithoutPostToDelete = contentDAO.findAllContentWithoutPostOlderThan1Hour();

        for (Content content : contentWithoutPostToDelete) {

            List<FormatType> formatTypesForCurrentContent = formatTypeRepository.findAllByContentId(content.getId());

            boolean isDeletedFromCloud = true;
            for (FormatType formatType : formatTypesForCurrentContent){
                Boolean deleted = gCloud.deleteFromGCloud(formatType.getUrl().substring(formatType.getUrl().lastIndexOf("/") + 1));
                if (!deleted){
                    isDeletedFromCloud = false;
                }
            }

            if (isDeletedFromCloud){
                contentRepository.delete(content);
            }

        }
    }
}
