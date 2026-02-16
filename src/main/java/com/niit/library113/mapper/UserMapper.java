package com.niit.library113.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.niit.library113.entity.User; // 2. 引用 entity 包下的 User
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {}