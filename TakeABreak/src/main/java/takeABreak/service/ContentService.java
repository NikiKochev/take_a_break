package takeABreak.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import takeABreak.exceptions.BadRequestException;
import takeABreak.model.pojo.Content;
import takeABreak.model.repository.ContentRepository;

@Service
public class ContentService {
    @Autowired
    private ContentRepository contentRepository;

    public Content findById(int contentId) {
        if(!contentRepository.findById(contentId).isPresent()){
            throw new BadRequestException("mo picture or video to upload");
        }
        return contentRepository.findById(contentId).get();
    }

    public void save(Content content) {
        contentRepository.save(content);
    }
}
