package com.employeselfservice.services;

import com.employeselfservice.models.Designation;
import com.employeselfservice.repositories.DesignationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DesignationService {
    @Autowired
    private DesignationRepository designationRepository;

    public Designation findById(Long id){
        return designationRepository.findById(id).get();
    }

    public List<Designation> getAllDesignations(){
        return designationRepository.findAll();
    }
}
