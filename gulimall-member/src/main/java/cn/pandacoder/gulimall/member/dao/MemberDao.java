package cn.pandacoder.gulimall.member.dao;

import cn.pandacoder.gulimall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author pandacoder
 * @email 894302745@qq.com
 * @date 2020-12-03 11:32:06
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
