package takeABreak.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import takeABreak.model.pojo.Size;
import takeABreak.model.repository.SizeRepository;

import java.util.Optional;

@Service
public class SizeService {
    @Autowired
    private SizeRepository sizeRepository;

    public Optional<Size> findById(int id) {
        return sizeRepository.findById(id);
    }
}
