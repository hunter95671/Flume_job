package com.hunter95.sink;

import org.apache.flume.*;
import org.apache.flume.conf.Configurable;
import org.apache.flume.sink.AbstractSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MySink extends AbstractSink implements Configurable {

    //获取logger对象
    private Logger logger = LoggerFactory.getLogger(MySink.class);

    //定义两个属性，前后缀
    private String prefix;
    private String subfix;

    @Override
    public void configure(Context context) {

        //读取配置文件给前后缀赋值
        prefix=context.getString("prefix");
        subfix=context.getString("subfix","hunter95");

    }

    /**
     * 1.获取channel
     * 2.从channel获取事务及数据
     * 3.发送数据
     * @return
     * @throws EventDeliveryException
     */

    @Override
    public Status process() throws EventDeliveryException {

        //1.定义返回值
        Status status=null;
        
        //2.获取channel
        Channel channel = getChannel();
        
        //3.从channel获取事务
        Transaction transaction = channel.getTransaction();

        //4.开启事务
        transaction.begin();

        try {
            //5.从channel获取数据
            Event event = channel.take();

            if(event!=null){
                //6.处理事件
                String body = new String(event.getBody());
                logger.info(prefix+body+subfix);
            }
            //7.提交事务
            transaction.commit();

            //8.成功提交，修改状态信息
            status=Status.READY;
        } catch (ChannelException e) {
            e.printStackTrace();

            //9.提交失败回滚
            transaction.rollback();

            //10.修改状态
            status=Status.BACKOFF;
        } finally {
            //11.最终关闭事务
            transaction.close();
        }
        //12.返回状态信息
        return status;
    }
}
