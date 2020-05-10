package com.nowcoder.community.event;

import com.nowcoder.community.elasticsearch.model.EsDiscussPost;
import com.nowcoder.community.model.entity.DiscussPost;
import com.nowcoder.community.model.entity.Message;
import com.nowcoder.community.model.enums.MessageStatus;
import com.nowcoder.community.model.event.Event;
import com.nowcoder.community.model.support.CommunityConstant;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.ElasticsearchService;
import com.nowcoder.community.service.MessageService;
import com.nowcoder.community.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * @author mafei007
 * @date 2020/5/5 18:58
 */

@Component
@Slf4j
public class EventConsumer {

	private final MessageService messageService;
	private final DiscussPostService discussPostService;
	private final ElasticsearchService elasticsearchService;

	private final String COMMENT = "comment";
	private final String LIKE = "like";
	private final String FOLLOW = "follow";
	private final String PUBLISH = "publish";


	public EventConsumer(MessageService messageService, DiscussPostService discussPostService, ElasticsearchService elasticsearchService) {
		this.messageService = messageService;
		this.discussPostService = discussPostService;
		this.elasticsearchService = elasticsearchService;
	}

	/**
	 * 将点赞、评论、关注这些系统通知 相关信息存入 message 表中
	 * COMMENT, LIKE, FOLLOW 这些 topic 要事先在 kafka 中创建！
	 *
	 * @param record
	 */
	@KafkaListener(topics = {COMMENT, LIKE, FOLLOW})
	public void handleCommentMessage(ConsumerRecord record) {
		if (record == null || record.value() == null) {
			log.error("消息的内容为空");
			return;
		}

		Event event = JsonUtils.jsonToPojo(record.value().toString(), Event.class);
		if (event == null) {
			log.error("消息格式错误！");
			return;
		}

		// 发送站内通知
		Message message = new Message();
		// fromId 为 1 代表系统通知
		message.setFromId(CommunityConstant.SYSTEM_USER_ID);
		message.setToId(event.getEntityUserId());
		message.setConversationId(event.getTopic().getValue());
		message.setStatus(MessageStatus.UNREAD);
		message.setCreateTime(new Date());

		// 构建 content
		Map<String, Object> content = new HashMap<>();
		content.put("userId", event.getUserId());
		content.put("entityType", event.getEntityType().getValue());
		content.put("entityId", event.getEntityId());
		content.putAll(event.getData());
		message.setContent(JsonUtils.objectToJson(content));
		// 保存到数据库
		messageService.addMessage(message);
	}

	/**
	 * 消费发帖事件，将贴子添加到 es 索引库
	 * @param record
	 */
	@KafkaListener(topics = {PUBLISH})
	public void hadlePublishMessage(ConsumerRecord record) {
		if (record == null || record.value() == null) {
			log.error("消息的内容为空");
			return;
		}

		Event event = JsonUtils.jsonToPojo(record.value().toString(), Event.class);
		if (event == null) {
			log.error("消息格式错误！");
			return;
		}

		DiscussPost post = discussPostService.findDiscussPostById(event.getEntityId());
		EsDiscussPost esDiscussPost = EsDiscussPost.convertTo(post);
		elasticsearchService.saveDiscussPost(esDiscussPost);
	}

}