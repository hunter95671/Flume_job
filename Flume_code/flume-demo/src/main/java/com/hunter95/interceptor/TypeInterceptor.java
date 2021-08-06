package com.hunter95.interceptor;

import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.interceptor.Interceptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TypeInterceptor implements Interceptor {

    //声明存放事件的集合
    private List<Event> addHeaderEvents;

    @Override
    public void initialize() {

        //初始化存放事件的集合
        addHeaderEvents=new ArrayList<>();

    }

    //单个事件拦截
    @Override
    public Event intercept(Event event) {

        //1.获取事件中的头信息
        Map<String, String> headers = event.getHeaders();

        //2.获取事件中的body
        String body = new String(event.getBody());

        //3.根据body中是否有“hello”来决定添加怎样的头信息
        if(body.contains("hello")){
            //4.添加头信息
            headers.put("type","hunter95");
        }else {
            //4.添加头信息
            headers.put("type","others");
        }

        return event;
    }


    //多个事件拦截
    @Override
    public List<Event> intercept(List<Event> list) {

        //1.清空集合
        addHeaderEvents.clear();

        //2.遍历list
        for (Event event : list) {

            //3.给每一个事件添加头信息
            addHeaderEvents.add(intercept(event));
            
        }

        //4.返回结果
        return addHeaderEvents;
    }

    @Override
    public void close() {

    }

    public static class Builder implements Interceptor.Builder{

        @Override
        public Interceptor build() {
            return new TypeInterceptor();
        }

        @Override
        public void configure(Context context) {

        }
    }
}
