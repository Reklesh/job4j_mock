package ru.checkdev.auth.service;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.checkdev.auth.domain.Notify;
import ru.checkdev.auth.domain.Order;
import ru.checkdev.auth.repository.OrderRepository;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Mikhail Epatko.
 * 25 August 2017
 * mail: mikhail.epatko@gmail.com
 */

@Service
public class OrderService {

    private final OrderRepository orders;
    private final String recipient;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    public OrderService(OrderRepository orders, final @Value("${recipient.notification}") String recipient,
                        KafkaTemplate<String, Object> kafkaTemplate) {
        this.orders = orders;
        this.recipient = recipient;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void save(Order order) {
        order.setCreated(Calendar.getInstance());
        this.orders.save(order);
        Map<String, Object> keys = new HashMap<>();
        keys.put("order", order);
        Notify notify = new Notify(recipient, keys, Notify.Type.ORDER.name());
        kafkaTemplate.send("notifications-topic", notify);
    }

    public void toArchive(Order order) {
        this.orders.updateArchive(order.getId(), true);
        order.setArchive(true);
    }

    public List<Order> findAll() {
        return Lists.newArrayList(this.orders.findAll());
    }

    public List<Order> findByType(String type) {
        if ("fresh".equals(type)) {
            return this.orders.findByArchiveOrderByCreatedDesc(false);
        } else {
            return this.orders.findAllByOrderByCreatedDesc();
        }
    }
}
