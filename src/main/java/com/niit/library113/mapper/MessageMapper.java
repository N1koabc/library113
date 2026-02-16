package com.niit.library113.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.niit.library113.entity.Message;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MessageMapper extends BaseMapper<Message> {}