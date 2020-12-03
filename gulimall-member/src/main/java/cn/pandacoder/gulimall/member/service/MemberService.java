package cn.pandacoder.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.pandacoder.common.utils.PageUtils;
import cn.pandacoder.gulimall.member.entity.MemberEntity;

import java.util.Map;

/**
 * 会员
 *
 * @author pandacoder
 * @email 894302745@qq.com
 * @date 2020-12-03 11:32:06
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

