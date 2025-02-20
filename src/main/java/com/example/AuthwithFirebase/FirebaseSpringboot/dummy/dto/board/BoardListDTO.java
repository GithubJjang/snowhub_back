package com.example.AuthwithFirebase.FirebaseSpringboot.dummy.dto.board;

import lombok.*;

import java.sql.Timestamp;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class BoardListDTO { //category, title, writer, date, count -> 리액트에 뿌리기 위한 DTE ( 전체 게시글 보기 )

    private int id;
    private String category;
    private String title;
    private String content;
    private String writer;
    private int count;
    private Timestamp createDate;
}
