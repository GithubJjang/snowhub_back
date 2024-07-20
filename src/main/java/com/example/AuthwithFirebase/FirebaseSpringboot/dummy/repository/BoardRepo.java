package com.example.AuthwithFirebase.FirebaseSpringboot.dummy.repository;

import com.example.AuthwithFirebase.FirebaseSpringboot.dummy.model.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepo extends JpaRepository<Board,Integer> {
    Page<Board> findByCategory(String category, Pageable pageable);

}
