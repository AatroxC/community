package com.nowcoder.community;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.model.entity.DiscussPost;
import com.nowcoder.community.model.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author mafei007
 * @date 2020/3/30 18:46
 */


@RunWith(SpringRunner.class)
@SpringBootTest(classes = CommunityApplication.class)
public class MapperTest {


    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private UserMapper userMapper;


    @Test
    public void testSelectPosts(){
        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPosts(0, 0, 10);
        for (DiscussPost discussPost : discussPosts) {
            System.out.println(discussPost);
        }

        Integer integer = discussPostMapper.selectDiscussPostRows(0);
        System.out.println(integer);
    }


    @Test
    public void test22(){
        // 验证
        User user = new User();
        user.setUsername("test");

        User user1 = userMapper.selectOne(user);
        System.out.println(user);
        System.out.println(user1);
    }

}
