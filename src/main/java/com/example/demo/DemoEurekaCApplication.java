package com.example.demo;

import java.util.Map;
import java.util.concurrent.locks.Lock;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

@SpringBootApplication
@EnableEurekaClient
@RestController
@EnableCaching
public class DemoEurekaCApplication {
  private HazelcastInstance instance = Hazelcast.newHazelcastInstance();
  private Map<String,String> cache = instance.getMap("cache");

	public static void main(String[] args) {
		SpringApplication.run(DemoEurekaCApplication.class, args);
	}
	
  @Value("${server.port}")
  String port;
  @RequestMapping("/hi")
  public String home(HttpServletRequest request,@RequestParam String name) {
    Lock lock = instance.getLock("locktest");
    try {
      lock.lock();
      if (cache.get("name") != null) {
        System.out.println("from cache");
        return "hi " + cache.get("name") + ",i am from port:" + port;
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      lock.unlock();
    }
    
    cache.put("name",name);
    return "hi "+name+",i am from port:" +port;
  }
}
