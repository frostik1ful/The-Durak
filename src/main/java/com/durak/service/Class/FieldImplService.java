package com.durak.service.Class;

import com.durak.entity.Field;
import com.durak.repository.FieldRepository;
import com.durak.service.Interface.FieldDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FieldImplService implements FieldDAO {
    @Autowired
    FieldRepository fieldRepository;
    @Override
    public void save(Field field) {
        fieldRepository.save(field);
    }
}
