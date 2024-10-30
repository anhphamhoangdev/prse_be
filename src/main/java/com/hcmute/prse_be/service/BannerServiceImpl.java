package com.hcmute.prse_be.service;

import com.hcmute.prse_be.entity.BannerEntity;
import com.hcmute.prse_be.repository.BannerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BannerServiceImpl implements BannerService {

    private final BannerRepository bannerRepository;

    @Autowired
    public BannerServiceImpl(BannerRepository bannerRepository) {
        this.bannerRepository = bannerRepository;
    }

    public List<BannerEntity> getAllBannerActive(){
        return bannerRepository.findAllByIsActiveTrueOrderByOrderIndexAsc();
    }

}
