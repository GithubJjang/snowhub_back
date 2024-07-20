package com.example.AuthwithFirebase.FirebaseSpringboot.dummy.controller;

import com.example.AuthwithFirebase.FirebaseSpringboot.dummy.dto.comment.CommentDTO;
import com.example.AuthwithFirebase.FirebaseSpringboot.dummy.model.Reply;
import com.example.AuthwithFirebase.FirebaseSpringboot.dummy.repository.ReplyRepo;
import com.example.AuthwithFirebase.FirebaseSpringboot.dummy.service.CommentService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@AllArgsConstructor
@RestController
public class CommentController {

    private final ReplyRepo replyRepo;// 런타임 전에 오류를 잡기 위해서, 생성자 의존성 주입을 한다.
    private final CommentService commentService;

    @PostMapping("/board/comment")
    public ResponseEntity<?> getComment(@RequestParam(name = "id") int replyId,
                                        @RequestBody CommentDTO commentDTO){
        return commentService.saveComment(commentDTO,replyId);

    }

    @GetMapping("/board/reply/comment")
    public ResponseEntity<?> getComment(@RequestParam(name = "id")int replyId){
        Reply reply = replyRepo.findById(replyId).orElseThrow(
                ()-> new NullPointerException("/board/reply/commment")
        );

        List<CommentDTO> commentDTO = commentService.getComment(reply);


        // reply로 comment에 관련 답글찾기

        return ResponseEntity.ok(commentDTO);

    }

}
