package com.taotao.rest.dao;

public interface JedisClient {
	
	String get(String key);
	String set(String key,String value);
    //取哈希
	long hset(String hkey,String key,String value);
	String hget(String hkey,String key);
	//自增长
	long incr(String key);
	//设置过期时间
	long expire(String key,int second);
	//查看过期时间
	long ttl(String key);
	long del(String key);
	long hdel(String hkey,String key);
}
