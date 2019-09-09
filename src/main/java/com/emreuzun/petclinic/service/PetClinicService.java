package com.emreuzun.petclinic.service;


import com.emreuzun.petclinic.dao.ClinicDao;
import com.emreuzun.petclinic.dao.OwnerDao;
import com.emreuzun.petclinic.model.Clinic;
import com.emreuzun.petclinic.model.Owner;

public class PetClinicService {

    private OwnerDao ownerDao;
    private ClinicDao clinicDao;

    public void setOwnerDao(OwnerDao ownerDao) {
        this.ownerDao = ownerDao;
    }

    public void setClinicDao(ClinicDao clinicDao) {
        this.clinicDao = clinicDao;
    }

    public void addNewOwners(Long clinicId, Owner... owners) {
        Clinic clinic = clinicDao.findById(clinicId);
        for (Owner owner : owners) {
            ownerDao.create(owner);
            clinic.getPersons().add(owner);
        }
    }
}