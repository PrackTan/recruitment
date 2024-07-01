package com.example.demo.service;

import com.example.demo.entity.CompanyEntity;
import com.example.demo.payload.BaseLoad;
import com.example.demo.payload.MetaLoad;
import com.example.demo.payload.request.CompanyRequest;
import com.example.demo.repository.CompanyRepository;
import com.example.demo.service.serviceImp.CompanyServiceImp;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CompanyService implements CompanyServiceImp {
    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Override
    public CompanyEntity addCompany(CompanyRequest companyRequest) {
        CompanyEntity companyEntity = companyRepository.save(CompanyEntity.builder()
                .name(companyRequest.getName())
                .description(companyRequest.getDescription())
                .address(companyRequest.getAddress())
                .logo(companyRequest.getLogo())
                .build()
        );
        return companyEntity;
    }

    @Override
    public CompanyEntity updateCompany(CompanyRequest companyRequest) {
        Optional<CompanyEntity> companyEntityId = companyRepository.findById(companyRequest.getId());
        if(companyEntityId.isPresent()){
            CompanyEntity companyEntity = companyRepository.save(CompanyEntity.builder()
                    .id(companyRequest.getId())
                    .name(companyRequest.getName())
                    .description(companyRequest.getDescription())
                    .address(companyRequest.getAddress())
                    .build());
            return companyEntity;

        }
        return null;
    }

    @Override
    public List<CompanyEntity> getAllCompany() {
        List<CompanyEntity> companyEntityList = companyRepository.findAll();
        return companyEntityList;
    }

    @Override
    public BaseLoad getCompanyPage(Specification<CompanyEntity> specification,Pageable pageable) {
        /// chuyển findall thành list nên sử dụng Page để có getContent trả ra list
        Page<CompanyEntity> pageCompany = companyRepository.findAll(specification,pageable);
        MetaLoad metaLoad = new MetaLoad();
        metaLoad.setPage(pageCompany.getNumber() + 1);
        metaLoad.setPageSize(pageCompany.getSize());
        metaLoad.setPages(pageCompany.getTotalPages());
        metaLoad.setTotal(pageCompany.getTotalElements());
        BaseLoad baseLoad = new BaseLoad();
        baseLoad.setMessage("GET SUCCESS");
        baseLoad.setMetaLoad(metaLoad);

        List<CompanyRequest> listCompanies = pageCompany.getContent().stream().map(item -> new CompanyRequest(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAddress(),
                item.getLogo(),
                item.getCreateAt(),
                item.getUpdatedAt())).collect(Collectors.toList());
        baseLoad.setData(listCompanies);
        return baseLoad;
    }

    @Override
    public boolean deleteCompany(Long id) {
        Optional<CompanyEntity> companyEntityId = companyRepository.findById(id);
        if(companyEntityId.isPresent()){
             companyRepository.deleteById(id);
             return true;
        };
        return false;
    }

    @Override
    public Optional<CompanyEntity> getCompanyById(long id) {
        return companyRepository.findById(id);
    }

    ;

}
