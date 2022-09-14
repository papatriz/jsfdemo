package com.papatriz.jsfdemo.repositories;

import com.papatriz.jsfdemo.models.Node;
import org.springframework.data.jpa.repository.JpaRepository;

public interface INodeRepository extends JpaRepository<Node, Integer> {
}
