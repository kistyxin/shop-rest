package com.taotao.rest.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.taotao.common.utils.JsonUtils;
import com.taotao.mapper.TbContentMapper;
import com.taotao.pojo.TbContent;
import com.taotao.pojo.TbContentExample;
import com.taotao.pojo.TbContentExample.Criteria;
import com.taotao.rest.dao.JedisClient;
import com.taotao.rest.service.ContentService;
@Service
public class ContentServiceImpl implements ContentService {
	//从缓存中取内容

	@Autowired
	private TbContentMapper contentMapper;
	@Autowired
	private JedisClient jedisClient;
	@Value("${INDEX_CONTENT_REDIS_KEY}")
	private String INDEX_CONTENT_REDIS_KEY;
	
	@Override
	//返回相同categoryId的实体的列表（同一块位置的所有不同广告内容）
	public List<TbContent> getContentlist(long contentCid) {
		
		//从缓存中取内容
		try{
			String result = jedisClient.hget(INDEX_CONTENT_REDIS_KEY, contentCid+"");
		    if(!StringUtils.isBlank(result)){
		    	//把字符串转换成list
		    	List<TbContent> list = JsonUtils.jsonToList(result, TbContent.class);
		        return list;
		    };
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		TbContentExample example = new TbContentExample();
		Criteria criteria = example.createCriteria();
		criteria.andCategoryIdEqualTo(contentCid);
		//执行查询
		List<TbContent> list = contentMapper.selectByExample(example);
		
		
		
		//向缓存中添加内容
		try{
			//把list转换成字符串
			String cacheString = JsonUtils.objectToJson(list);
			//第二个参数是长整型,而hest要求第二个参数是string型，所以加+""换个类型
			jedisClient.hset(INDEX_CONTENT_REDIS_KEY, contentCid+"", cacheString);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return list;
	}

}
