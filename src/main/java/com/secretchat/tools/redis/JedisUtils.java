package com.secretchat.tools.redis;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisException;

import com.secretchat.tools.common.ObjectTranscoder;
import com.secretchat.tools.common.ReadPropsUtils;

/**
 * redis工具类 
 * -------- String --------
 * 1.redis.set( key, value); key value将字符串值value关联到key，get获取值方法
 * 2.redis.setex( key, 5, value); 设置有效期 5s  PSETEX单位为毫秒
 * 3.redis.mset( key1, value1, key2, value2); key value [key value ...]同时设置一个或多个key-value对，mget获取值方法  
 * 4.redis.dbSize();  	key的总个数
 * 5.redis.flushAll();	清空所有的key
 * 6.redis.append(key, appendValue); key 已经存在,并且值为字符串则追加，否则创建
 * --------- Hash ---------
 * 1.redis.hset(key,  field, value);  key field value将哈希表key中的域field的值设为value。    hget，hmget获取值方法  hmset设置多个
 * 2.redis.hgetAll(key); key返回哈希表key中，所有的域和值。    hkeys返回哈希表key中的所有域   hvals返回哈希表key中的所有值。  
 * 3.redis.hdel(key, field); key field [field ...]删除哈希表key中的一个或多个指定域。   
 * 4.redis.hlen(key);	key 返回哈希表key中域的数量。
 * 5.redis.hexists(key, field); key field查看哈希表key中，给定域field是否存在。
 * 6.hincrby key field increment为哈希表key中的域field的值加上增量increment   
 * 	 hkeys key返回哈希表key中的所有域   
 *   hvals key返回哈希表key中的所有值      
 * --------- List ---------
 * 1.redis.lpush( key, value); key value [value ...]将值value插入到列表的表头。 rpush插入尾部
 * 2.redis.lrange( key, 0, -1); key start stop返回列表key中指定区间内的元素，区间以偏移量start和stop指定。 -1倒数第一个元素，0第一元素 
 * 3.redis.llen( key); key返回列表key的长度。
 * 3.redis.rpop( key);	移除并返回存于 key 的 list 的最后一个元素 lpop第一个元素
 * 4.lrem key count value根据参数count的值，移除列表中与参数value相等的元素。
 * --------- set ----------
 * 1.redis.sadd( key, value); key value [value ...]将member元素加入到集合key当中。
 * 2.redis.srem( key, value); key value移除集合中的member元素。   
 * 3.redis.smembers( key); key返回集合key中的所有成员
 * 4.redis.sismember( key, value);	 key value判断value元素是否是集合key的成员。是（true），否则（false）   
 * 5.scard key返回集合key的基数(集合中元素的数量),
 * 	 smove source destination member将member元素从source集合移动到destination集合, 
 *   sinter key [key ...]返回一个集合的全部成员，该集合是所有给定集合的交集,等同sinterstore 
 *   sunion key [key ...]返回一个集合的全部成员，该集合是所有给定集合的并集,等同sunionstore
 *   sdiff key [key ...]返回一个集合的全部成员，该集合是所有给定集合的差集 ,等同sdiffstore
 * @author smachen
 * @version 2.8.0
 */
public class JedisUtils {
	private static Logger logger = LoggerFactory.getLogger(JedisUtils.class);
	
	private static JedisPool jedisPool = null;
	
	/** 
     * redis过期时间,以秒为单位 
     */  
    public final static int EXRP_ONE_MINUTE = 60;           //1分钟  
    public final static int EXRP_TWO_MINUTE = 60*2;        	//2分钟
    public final static int EXRP_FIVE_MINUTE = 60*5;        //5分钟
    public final static int EXRP_TEN_MINUTE = 60*10;        //10分钟
    public final static int EXRP_FIFTEEN_MINUTE = 60*15;    //15分钟
    public final static int EXRP_HALF_HOUR = 60*30;   		//半小时
    public final static int EXRP_HOUR = 60*60;          	//一小时  
    public final static int EXRP_DAY = 60*60*24;       		//一天  
    public final static int EXRP_MONTH = 60*60*24*30;   	//一个月
    public final static int EXRP_INFINITE = 0;   			//永久

	/**
	 * 构建redis连接池
	 * 
	 * @param ip
	 * @param port
	 * @return JedisPool
	 */
	private static JedisPool getPool() {
		ReadPropsUtils read = new ReadPropsUtils();
		try {  
			if (jedisPool == null) {
				JedisPoolConfig config = new JedisPoolConfig();
				// 控制一个pool可分配多少个jedis实例，通过pool.getResource()来获取；
				// 如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
				config.setMaxTotal(Integer.parseInt(read.getValue("redis.maxActive")));
				// 控制一个pool最多有多少个状态为idle(空闲的)的jedis实例。
				config.setMaxIdle(Integer.parseInt(read.getValue("redis.maxIdle")));
				// 表示当borrow(引入)一个jedis实例时，最大的等待时间，如果超过等待时间，则直接抛出JedisConnectionException；
				config.setMaxWaitMillis(Integer.parseInt(read.getValue("redis.maxWait")));
				// 在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
				config.setTestOnBorrow(Boolean.parseBoolean(read.getValue("redis.testOnBorrow")));
				jedisPool = new JedisPool(config, read.getValue("redis.host"),
						Integer.parseInt(read.getValue("redis.port")), 3000, read.getValue("redis.pass"));
			}
		} catch (Exception e) {  
            logger.error("Create JedisPool error : "+e);  
        }
		return jedisPool;
	}
	
	/** 
     * 在多线程环境同步初始化 
     */  
    private static synchronized void initialPool() {  
        if (jedisPool == null) {    
        	getPool();  
        }  
    }  

	/**
	 * 获取资源
	 * 
	 * @return
	 * @throws JedisException
	 */
	public synchronized static Jedis getResource() throws JedisException {
		Jedis jedis = null;
		try {
			if (jedisPool == null) {    
				initialPool();  
	        }  
			jedis = jedisPool.getResource();
		} catch (JedisException e) {
			logger.warn("getResource.", e);
			returnBrokenResource(jedis);
			throw e;
		}
		return jedis;
	}
	
	/**
	 * 归还资源/释放资源
	 * 
	 * @param jedis
	 * @param isBroken
	 */
	public static void returnBrokenResource(Jedis jedis) {
		if (jedis != null) {
			jedis.close();
		}
	}
	
	/**
	 * 
	 * 删除指定的key
	 * 
	 * @param key 缓存键
	 * @return 返回删除结果
	 */
	public static long delObject(Object key) {
		long result = 0;
		Jedis jedis = null;
		try {
			jedis = getResource();
			if (jedis.exists(getBytesKey(key))) {
				result = jedis.del(getBytesKey(key));
				logger.debug("del {}", key);
			} else {
				logger.debug("del {} not exists", key);
			}
		} catch (Exception e) {
			logger.warn("del {}", key, e);
		} finally {
			returnBrokenResource(jedis);
		}
		return result;
	}
	
	/**
	 * 判断缓存中键是否存在
	 * 
	 * 2017-06-09 
	 * @author smachen
	 * 
	 * @param key	缓存键
	 * @return true 此键缓存中,否则不存在
	 */
	public static boolean existsObject(Object key) {
		boolean result = false;
		Jedis jedis = null;
		try {
			jedis = getResource();
			result = jedis.exists(getBytesKey(key));
			logger.debug("existsObject {}", key);
		} catch (Exception e) {
			logger.warn("existsObject {}", key, e);
		} finally {
			returnBrokenResource(jedis);
		}
		return result;
	}
	
	/**
	 * 
	 * 存储Object缓存内容
	 * 
	 * 2017-06-09 
	 * @author smachen
	 * 
	 * @param key    	缓存键
	 * @param value		Object值
	 * @param cacheSeconds 缓存有效期，0为不超时， 单位为秒
	 * @return 返回设置结果，OK是成功否则失败
	 */
	public static String setObject(String key, Object value, int cacheSeconds) {
		String result = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			result = jedis.set(getBytesKey(key), ObjectTranscoder.serialize(value));
			if (cacheSeconds != 0) {
				jedis.expire(key, cacheSeconds);
			}
			logger.debug("setObject {} = {}", key, value);
		} catch (Exception e) {
			logger.error("setObject {} = {}", new Object[] { key, value, e });
		} finally {
			returnBrokenResource(jedis);
		}
		return result;
	}
	
	/**
	 * 
	 * 获取Object缓存值
	 * 
	 * 2017-06-09 
	 * @author smachen
	 * 
	 * @param key	缓存键
	 * @return	返回获取的Object缓存值
	 */
	public static Object getObject(String key) {
		Object value = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			if (jedis.exists(getBytesKey(key))) {
				value = ObjectTranscoder.deserialize(jedis.get(getBytesKey(key)));
			}
			logger.debug("getObject {} = {}", key, value);
		} catch (Exception e) {
			logger.warn("getObject {} = {}", new Object[] { key, value, e });
		} finally {
			returnBrokenResource(jedis);
		}
		return value;
	}
	
	
	/**
	 * 判断Hash缓存中field属性是否存在
	 * 
	 * 2017-06-09 
	 * @author smachen
	 * 
	 * @param key	缓存键
	 * @param field Hash里键
	 * @return true map里存在field,否则不存在
	 */
	public static boolean existsHash(Object key,Object field) {
		boolean result = false;
		Jedis jedis = null;
		try {
			jedis = getResource();
			result = jedis.hexists(getBytesKey(key),getBytesKey(field));
			logger.debug("existsHash key {} field {}", key, field);
		} catch (Exception e) {
			logger.warn("existsHash key {} field {}", key, field, e);
		} finally {
			returnBrokenResource(jedis);
		}
		return result;
	}
	
	/**
	 * 
	 * 存储Hash缓存内容
	 * 
	 * 2017-06-09 
	 * @author smachen
	 * 
	 * @param key    	缓存键
	 * @param value		hash 值
	 * @param cacheSeconds 缓存有效期，0为不超时， 单位为秒
	 * @return 返回设置结果，OK是成功否则失败
	 */
	public static <T> String setHash(String key, Map<T, T> value, int cacheSeconds) {
		String result = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			Map<byte[], byte[]> map = new HashMap<byte[], byte[]>();
			for(Entry<T,T> entry: value.entrySet()) {   
				byte[] keys = ObjectTranscoder.serialize(entry.getKey());
				byte[] val = ObjectTranscoder.serialize(entry.getValue());
				map.put(keys, val);
			}
			result = jedis.hmset(getBytesKey(key), map);
			if (cacheSeconds != 0) {
				jedis.expire(key, cacheSeconds);
			}
			logger.debug("setHash {} = {}", key, value);
		} catch (Exception e) {
			logger.error("setHash {} = {}", new Object[] { key, value, e });
		} finally {
			returnBrokenResource(jedis);
		}
		return result;
	}
	
	/**
	 * 
	 * 获取hash缓存值
	 * 
	 * 2017-06-09 
	 * @author smachen
	 * 
	 * @param key	缓存键
	 * @return	返回获取的hash缓存值
	 */
	@SuppressWarnings("unchecked")
	public static <T> Map<T, T> getHash(String key) {
		Map<T, T> value = new HashMap<T,T>();
		Jedis jedis = null;
		try {
			jedis = getResource();
			if (jedis.exists(getBytesKey(key))) {
				Map<byte[],byte[]> map = jedis.hgetAll(getBytesKey(key));   
				for(Entry<byte[], byte[]> entry: map.entrySet()) {   
					T keys = (T) ObjectTranscoder.deserialize(entry.getKey());
					T vals = (T) ObjectTranscoder.deserialize(entry.getValue());
					value.put(keys, vals);
		        }  
			}
			logger.debug("getHash {} = {}", key, value);
		} catch (Exception e) {
			logger.warn("getHash {} = {}", new Object[] { key, value, e });
		} finally {
			returnBrokenResource(jedis);
		}
		return value;
	}
	
	/**
	 * 
	 * 获取Hash缓存中某个hash键的值
	 * 
	 * 2017-06-09 
	 * @author smachen
	 * 
	 * @param key	缓存键
	 * @param field hash键
	 * @return 返回获取的hash缓存中的某个键的值
	 */
	@SuppressWarnings("unchecked")
	public static <T> Object getHashVal(String key,Object field) {
		Object value = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			if (jedis.exists(getBytesKey(key))) {
				Map<byte[],byte[]> map = jedis.hgetAll(getBytesKey(key));   
				for(Entry<byte[], byte[]> entry: map.entrySet()) {   
					Object keys = ObjectTranscoder.deserialize(entry.getKey());
					if (keys.equals(field)) {
						value = (T) ObjectTranscoder.deserialize(entry.getValue());
						break;
					}
		        }  
			}
			logger.debug("getHash {} = {}", key, value);
		} catch (Exception e) {
			logger.warn("getHash {} = {}", new Object[] { key, value, e });
		} finally {
			returnBrokenResource(jedis);
		}
		return value;
	}
	
    /**
     * 
	 * 存储List缓存内容
	 * 
     * 2017-06-09 
	 * @author smachen
	 * 
	 * @param key 缓存键
	 * @param value List缓存值 
	 * @param cacheSeconds 缓存有效期，0为不超时， 单位为秒
	 * @return 返回设置结果，OK是成功否则失败
	 */
	public static <T> void setList(String key, List<T> value, int cacheSeconds) {
		Jedis jedis = null;
		try {
			jedis = getResource();
			if (jedis.exists(key)) {
				jedis.del(key);
			}
			for (int i = 0; i < value.size(); i++) {
				T v = value.get(i);
				jedis.lpush(key.getBytes(),ObjectTranscoder.serialize(v));  
			}
			if (cacheSeconds != 0) {
				jedis.expire(key, cacheSeconds);
			}
			logger.debug("setList {} = {}", key, value);
		} catch (Exception e) {
			logger.error("setList {} = {}", new Object[] { key, value, e });
		} finally {
			returnBrokenResource(jedis);
		}
	}
	
	/**
	 * 
	 * 获取List缓存值
	 * 
	 * 2017-06-09 
	 * @author smachen
	 * 
	 * @param key	缓存键
	 * @return	返回获取的List缓存值
	 */   
    @SuppressWarnings("unchecked")
	public static <T> List<T> getList(String key){    
        List<T> value = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			if (jedis.exists(key)) {
				List<byte[]> list = jedis.lrange(key.getBytes(), 0, -1);   
				value = new ArrayList<T>();
				for(int i=0;i < list.size();i++){   
		            byte[] val = list.get(i);
		            value.add((T) ObjectTranscoder.deserialize(val));
		        } 
				logger.debug("getList {} = {}", key, value);
			} else {
				logger.debug("getList {}  not exists", key);
			}
		} catch (Exception e) {
			logger.error("getList {} = {}", new Object[] { key, value, e });
		} finally {
			returnBrokenResource(jedis);
		}
		return value;
    }
	
	/**
	 * 将Object key 转成byte[]
	 * @param object key值
	 * @return 
	 */
	public static byte[] getBytesKey(Object object) {
		if (object == null) {
			return null;
		}else if (object instanceof String) {
			try {
				return ((String) object).getBytes("UTF-8");
			} catch (UnsupportedEncodingException e) {
				return null;
			}
		} else {
			return ObjectTranscoder.serialize(object);
		}
	}
	
	public static void main(String[] args) {
		
	}

}
