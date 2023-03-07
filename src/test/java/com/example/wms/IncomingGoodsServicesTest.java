package com.example.wms;

import com.example.wms.dtos.ProductDetailsDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = WmsApplication.class)
@AutoConfigureMockMvc
public class IncomingGoodsServicesTest {


    @Autowired
    private MockMvc webTestClient;
    @RepeatedTest(100)
    public void createIncomingGoodsTest() throws Exception {
        MvcResult mvcResult1 = webTestClient
                .perform(get("http://localhost:8080/product/getProductDetails")
                        .param("name", "laptop")).andReturn();
        String response = (mvcResult1.getResponse().getContentAsString());
        ObjectMapper mapper = new ObjectMapper();
        ProductDetailsDto prevDetailsDto = mapper.readValue(response, ProductDetailsDto.class);
        ExecutorService exec = Executors.newSingleThreadExecutor();
        exec.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    MvcResult mvcResult = webTestClient
                            .perform(post("http://localhost:8080/inbound/createIncomingGoods")
                                    .param("name", "laptop")
                                    .param("quantity", "1")).andReturn();
                } catch (Exception e) {
                    e.printStackTrace();
                }            }
        });
        exec.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    MvcResult mvcResult = webTestClient
                            .perform(post("http://localhost:8080/inbound/createIncomingGoods")
                                    .param("name", "laptop")
                                    .param("quantity", "1")).andReturn();
                } catch (Exception e) {
                    e.printStackTrace();
                }            }
        });
        exec.shutdown();
        exec.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        MvcResult mvcResult2 = webTestClient
                .perform(MockMvcRequestBuilders.get("http://localhost:8080/product/getProductDetails")
                        .param("name", "laptop")).andReturn();
        String response1 = (mvcResult2.getResponse().getContentAsString());
        ProductDetailsDto newDetailsDto = mapper.readValue(response1, ProductDetailsDto.class);
        Assertions.assertEquals(prevDetailsDto.getProductDetails().getQuantity() + 2, newDetailsDto.getProductDetails().getQuantity());
    }


}

