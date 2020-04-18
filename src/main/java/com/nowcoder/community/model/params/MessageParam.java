package com.nowcoder.community.model.params;

import com.nowcoder.community.model.entity.Message;
import com.nowcoder.community.model.entity.User;
import com.nowcoder.community.model.enums.MessageStatus;
import com.nowcoder.community.model.support.UserHolder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * @author mafei007
 * @date 2020/4/18 23:31
 */

@Data
public class MessageParam {

	/**
	 * 要发送消息的用户名
	 */
	@NotBlank(message = "要发送消息的用户名不能为空！")
	private String toName;

	/**
	 * 发送的内容
	 */
	@NotBlank(message = "要发送消息的内容不能为空！")
	private String content;

	public Message convertTo(User target){
		Message message = new Message();
		message.setFromId(UserHolder.get().getId());
		message.setToId(target.getId());
		if (message.getFromId() < message.getToId()){
			message.setConversationId(message.getFromId() + "_" + message.getToId());
		} else{
			message.setConversationId(message.getToId() + "_" + message.getFromId());
		}
		message.setContent(this.content);
		message.setStatus(MessageStatus.UNREAD);
		message.setCreateTime(new Date());

		return message;
	}

}
