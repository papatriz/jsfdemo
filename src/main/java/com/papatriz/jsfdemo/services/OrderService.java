package com.papatriz.jsfdemo.services;

import com.papatriz.jsfdemo.comparators.NodeComparatorDistanceBased;
import com.papatriz.jsfdemo.exceptions.NoLoadCargoPointException;
import com.papatriz.jsfdemo.models.main.*;
import com.papatriz.jsfdemo.repositories.main.IOrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class OrderService implements IOrderService{
    private final IOrderRepository orderRepository;
    private final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    public OrderService(IOrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }
    @Override
    @Transactional
    public void saveOrder(Order order) {
        orderRepository.save(order);
    }

    @Override
    public List<Order> getPendingOrders() {

        List<Order> orders = orderRepository.getPendingOrders2();
        fetchNodesAndCargos(orders);
        return orders;
    }

    @Override
    public List<Order> getActiveOrders() {

        return orderRepository.getActiveOrdersHQL();
    }

    @Override
    public List<Order> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        fetchNodesAndCargos(orders);
        return orders;
    }

    public void makeWayBill(Order order) throws NoLoadCargoPointException {

        List<Node> origin = order.getNodes();
        Optional<Node> startNode = origin.stream().filter(node -> node.getType() == EActionType.LOAD).findFirst();
        if (startNode.isEmpty()) {
            throw new NoLoadCargoPointException("There is no load points at all");
        }
        ECity startCity = startNode.get().getCity();
        origin.sort(new NodeComparatorDistanceBased(startCity));

        boolean restartCycle = false;

        for (int i=0; i < origin.size()-1; i++) {
            if (restartCycle) {
                i=0;
                restartCycle = false;
            }
            if(origin.get(i).getType() == EActionType.UNLOAD) {
                List<Node> previousNodes = (i > 0)? origin.subList(0, i) : new ArrayList<>();
                Cargo checkedCargo = origin.get(i).getCargo();
                boolean wasLoaded = previousNodes.stream().anyMatch(ni -> ni.getCargo().equals(checkedCargo));

                if (!wasLoaded) {
                    List<Node> nextNodes = origin.subList(i+1, origin.size());
                    Optional<Node> loadNode = nextNodes.stream().filter(n -> n.getCargo().equals(checkedCargo)).findFirst();
                    if (loadNode.isEmpty()) {
                        throw new NoLoadCargoPointException("Can't find load point for cargo "+checkedCargo);
                    }
                    else {
                        int loadIndex = origin.indexOf(loadNode.get());
                        Node tmpNode = origin.get(i);
                        origin.remove(tmpNode);
                        origin.add(loadIndex, tmpNode);

                        ECity lastCity = loadNode.get().getCity();

                        origin.subList(loadIndex, origin.size()).sort(new NodeComparatorDistanceBased(lastCity));
                        restartCycle = true;
                    }
                }
            }
        }
        for (Node n:origin) {
            logger.info(n.toString());
        }
    }

    public int getOrderMaxWeight(Order order) {

        if(order == null) return 0;
        List<Node> nodes = order.getNodes();
        if(nodes == null || nodes.isEmpty()) return 0;

        int weight = 0;
        int max = 0;

        for (Node node : nodes) {
            int currentWeight = node.getCargo().getWeight();
            weight += (node.getType() == EActionType.LOAD) ? currentWeight : -currentWeight;

            if (weight > max) max = weight;
        }
        return  max;
    }

    private void fetchNodesAndCargos(List<Order> orders) {
        List<Node> nodes = new ArrayList<>();
        orders.stream().forEach(o -> nodes.addAll(o.getNodes()));
        nodes.stream().forEach(n -> n.getCargo().getWeight());
    }
}
