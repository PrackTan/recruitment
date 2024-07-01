package com.example.demo.service.serviceImp;

import com.example.demo.entity.CompanyEntity;
import com.example.demo.payload.BaseLoad;
import com.example.demo.payload.request.CompanyRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

public interface CompanyServiceImp {
    CompanyEntity addCompany(CompanyRequest companyRequest);
    CompanyEntity updateCompany(CompanyRequest companyRequest);
    List<CompanyEntity> getAllCompany();
    BaseLoad getCompanyPage( Specification<CompanyEntity> specification, Pageable pageable);
    boolean deleteCompany(Long id);
    Optional<CompanyEntity> getCompanyById (long id);
}
