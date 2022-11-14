package com.agri.common.upload.service;

import com.agri.common.upload.mapper.ImageInfoMapper;
import com.agri.common.upload.model.ImageInfo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ImageInfoServiceImpl extends ServiceImpl<ImageInfoMapper, ImageInfo> implements IImageInfoService {
}
