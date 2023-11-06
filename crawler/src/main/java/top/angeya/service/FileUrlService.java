package top.angeya.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.angeya.dao.FileUrlMapper;
import top.angeya.entity.FileUrl;

/**
 * 文件下载地址服务
 *
 * @author: angeya
 * @date: 2023/11/6 23:20
 * @description:
 */
@Service
public class FileUrlService extends ServiceImpl<FileUrlMapper, FileUrl> {
}
