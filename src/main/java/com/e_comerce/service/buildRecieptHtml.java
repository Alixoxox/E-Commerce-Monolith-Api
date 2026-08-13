package com.e_comerce.service;

import com.e_comerce.model.PastOrders;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class buildRecieptHtml {

    private final SpringTemplateEngine templateEngine;

    public buildRecieptHtml(SpringTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public String build(PastOrders order) {

        Context context = new Context();

        context.setVariable("companyName", "Meezan Store");
        context.setVariable("order", order);

        return templateEngine.process("order", context);
    }
}