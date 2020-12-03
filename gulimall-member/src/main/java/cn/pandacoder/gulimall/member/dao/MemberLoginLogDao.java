package cn.pandacoder.gulimall.member.dao;

import cn.pandacoder.gulimall.member.entity.MemberLoginLogEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员登录记录
 * 
 * @author pandacoder
 * @email 894302745@qq.com
 * @date 2020-12-03 11:32:06
 */
@Mapper
public interface MemberLoginLogDao extends BaseMapper<MemberLoginLogEntity> {
	
}
