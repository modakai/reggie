package com.sakura.reggieApi.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sakura.reggieApi.common.pojo.Log;
import org.apache.ibatis.annotations.Mapper;

/**
 * 日志表 的 mapper
 * @author sakura
 * @className LogMapper
 * @createTime 2023/2/8
 */
@Mapper
public interface LogMapper extends BaseMapper<Log> {
}
