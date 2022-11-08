package com.papatriz.jsfdemo.repositories.main;

import com.papatriz.jsfdemo.models.main.Node;
import org.springframework.data.jpa.repository.JpaRepository;

public interface INodeRepository extends JpaRepository<Node, Integer> {
}
