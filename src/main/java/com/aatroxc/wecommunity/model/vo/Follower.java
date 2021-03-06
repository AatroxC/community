package com.aatroxc.wecommunity.model.vo;

import com.aatroxc.wecommunity.model.entity.User;
import lombok.Data;


/**
 * 某个用户的粉丝
 *
 * @author mafei007
 * @date 2020/5/4 14:47
 */

@Data
public class Follower {

	private User user;
	private long followTime;
	/**
	 * 对于查看的用户来说，他有没有关注
	 */
	private boolean hasFollowed;

}
