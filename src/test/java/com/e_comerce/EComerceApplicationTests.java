package com.e_comerce;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@ActiveProfiles("test")
class EComerceApplicationTests {

	@MockitoBean
    private RabbitTemplate rabbitTemplate;

	@MockitoBean
    private ConnectionFactory connectionFactory;

	@Test
	void contextLoads() {
	}

}
