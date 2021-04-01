package takeABreak.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import takeABreak.exceptions.BadRequestException;
import takeABreak.model.pojo.Size;
import takeABreak.model.repository.SizeRepository;

import java.util.Optional;

@Service
public class SizeService {
    @Autowired
    private SizeRepository sizeRepository;

    public Size findById(int id) {
        Optional<Size> size = sizeRepository.findById(id);
        if(size.isPresent() ){
            return size.get();
        }
        throw new BadRequestException("No such post");
    }
}
