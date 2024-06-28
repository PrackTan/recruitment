package com.example.demo.controller;

import com.example.demo.entity.CompanyEntity;
import com.example.demo.entity.UserEntity;
import com.example.demo.payload.BaseLoad;
import com.example.demo.payload.MetaLoad;
import com.example.demo.payload.request.CompanyRequest;
import com.example.demo.service.serviceImp.CompanyServiceImp;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/company")
public class CompanyController {
    private final CompanyServiceImp companyServiceImp;

    public CompanyController(CompanyServiceImp companyServiceImp) {
        this.companyServiceImp = companyServiceImp;
    }

    @GetMapping("")
    public ResponseEntity<?> getallCompany(
            Pageable pageable,
            @Filter Specification<CompanyEntity> specification

//            @RequestParam("current") Optional<String> currentOption,
//            @RequestParam("pageSize") Optional<String> pageSizeOption
    ){
//        String sCurrent = currentOption.orElse("");
//        String sPageSize = pageSizeOption.orElse("");
//        System.out.println("current"+sCurrent+"page"+sPageSize);
//        int current = Integer.parseInt(sCurrent) - 1;
//        int page = Integer.parseInt(sPageSize);
//        Pageable pageable = PageRequest.of(current,page);
        BaseLoad companyEntityList = companyServiceImp.getCompanyPage(specification,pageable);
        BaseLoad baseLoad = new BaseLoad();
        baseLoad.setMessage("Lấy company thành công");
        baseLoad.setData(companyEntityList);
        return ResponseEntity.status(HttpStatus.OK).body(companyEntityList);
    }
    @PostMapping("")
    public ResponseEntity<?> addCompany(@Valid @RequestBody CompanyRequest companyRequest){
        CompanyEntity companyEntity = companyServiceImp.addCompany(companyRequest);
        BaseLoad baseLoad = new BaseLoad();
        baseLoad.setMessage("Added is succcesful");
        baseLoad.setData(companyEntity);
        return ResponseEntity.status(HttpStatus.CREATED).body(baseLoad);
    }
    @PutMapping("")
    public ResponseEntity<?> updateCompany(@Valid @RequestBody CompanyRequest companyRequest){
        CompanyEntity companyEntity = companyServiceImp.updateCompany(companyRequest);
        BaseLoad baseLoad = new BaseLoad();
        baseLoad.setMessage("Update thành công");
        baseLoad.setData(companyEntity);
        return ResponseEntity.status(HttpStatus.OK).body(baseLoad);
    }
    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteCompany(@PathVariable long id){
        boolean isSuccess = companyServiceImp.deleteCompany(id);
        MetaLoad metaLoad = new MetaLoad();
        BaseLoad baseLoad = new BaseLoad("xóa thành công",metaLoad, isSuccess);
        return ResponseEntity.status(HttpStatus.OK).body(baseLoad);
    }
}
