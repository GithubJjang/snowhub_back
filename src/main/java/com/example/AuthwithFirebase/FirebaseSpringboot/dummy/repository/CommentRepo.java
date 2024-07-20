package com.example.AuthwithFirebase.FirebaseSpringboot.dummy.repository;

import com.example.AuthwithFirebase.FirebaseSpringboot.dummy.model.Comment;
import com.example.AuthwithFirebase.FirebaseSpringboot.dummy.model.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepo extends JpaRepository<Comment,Integer> {

    List<Comment> findByReply(Reply reply);
}
