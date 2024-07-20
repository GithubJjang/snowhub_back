package com.example.AuthwithFirebase.FirebaseSpringboot.dummy.repository;

import com.example.AuthwithFirebase.FirebaseSpringboot.dummy.model.TmpBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TmpBoardRepo extends JpaRepository<TmpBoard,Integer> {
}
