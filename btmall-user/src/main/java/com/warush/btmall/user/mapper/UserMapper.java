package com.warush.btmall.user.mapper;

import com.warush.btmall.beans.UmsMember;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

/**
 * @version 1.0
 * @created by bill
 * @on 2019-11-03 19:11
 **/
@Repository
public interface UserMapper extends Mapper<UmsMember> {
}
