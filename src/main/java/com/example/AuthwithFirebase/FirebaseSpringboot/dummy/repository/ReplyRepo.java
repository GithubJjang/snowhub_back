package com.example.AuthwithFirebase.FirebaseSpringboot.dummy.repository;

import com.example.AuthwithFirebase.FirebaseSpringboot.dummy.model.Board;
import com.example.AuthwithFirebase.FirebaseSpringboot.dummy.model.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReplyRepo extends JpaRepository<Reply,Integer> {

    List<Reply> findAllByBoard(Board board);
}
