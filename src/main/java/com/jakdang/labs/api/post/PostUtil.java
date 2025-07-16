package com.jakdang.labs.api.post;

import com.jakdang.labs.api.auth.dto.UserDTO;
import com.jakdang.labs.api.auth.service.UserService;
import com.jakdang.labs.api.common.ResponseDTO;
import com.jakdang.labs.api.file.FileServiceClient;
import com.jakdang.labs.api.file.dto.ResponseFileDTO;
import com.jakdang.labs.api.post.model.CommentDTO;
import com.jakdang.labs.api.post.model.PostDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PostUtil {

    private final FileServiceClient fileServiceClient;
    private final UserService userService;

    //글쓴이 정보 추가
    public PostDTO addAuthorToPost(PostDTO postDTO) {
        if (postDTO == null) {
            return null;
        }
        
        if (postDTO.getAuthor_id() == null) {
            return postDTO;
        }

        try {
            // 멤버 정보를 추가로 등록
            UserDTO userDTO = userService.getUserDTO(postDTO.getAuthor_id());
            postDTO.setAuthor(userDTO);
        } catch (Exception e) {
            System.err.println("작성자 정보 조회 실패: authorId=" + postDTO.getAuthor_id() + ", error=" + e.getMessage());
            postDTO.setAuthor(null);
        }
        
        return postDTO;
    }


    //글쓴이 정보 추가
    public CommentDTO addAuthorToComment(CommentDTO commentDTO) {
        if (commentDTO == null || commentDTO.getAuthorId() == null) {
            return commentDTO;
        }

        try {
            // 멤버 정보를 추가로 등록
            UserDTO userDTO = userService.getUserDTO(commentDTO.getAuthorId());
            commentDTO.setAuthor(userDTO);

            List<CommentDTO> childComments = commentDTO.getChildComments();
            if (childComments != null) {
                childComments.forEach(
                        it-> {
                            try {
                                UserDTO childMemberInfo = userService.getUserDTO(it.getAuthorId());
                                UserDTO parentAuthorInfo = userService.getUserDTO(it.getParentAuthorId());
                                it.setAuthor(childMemberInfo);
                                it.setParentAuthor(parentAuthorInfo);
                            } catch (Exception e) {
                                System.err.println("자식 댓글 작성자 정보 조회 실패: " + e.getMessage());
                                it.setAuthor(null);
                                it.setParentAuthor(null);
                            }
                        });
            }
        } catch (Exception e) {
            System.err.println("댓글 작성자 정보 조회 실패: " + e.getMessage());
            commentDTO.setAuthor(null);
        }

        return commentDTO;
    }


    //파일 정보 추가
    public PostDTO addFilePost(PostDTO postDTO) {
        if (postDTO == null || postDTO.getFile_ids() == null || postDTO.getFile_ids().size() == 0) {
            return postDTO;
        }

        try {
            // 파일 정보를 추가로 등록
            ResponseDTO<List<ResponseFileDTO>> responseDTO = fileServiceClient.findAllById(postDTO.getFile_ids());

            if(responseDTO != null && responseDTO.getData() != null && responseDTO.getData().size() > 0){
                postDTO.setFiles(responseDTO.getData());
            } else {
                postDTO.setFiles(null);
            }
        } catch (Exception e) {
            System.err.println("파일 정보 조회 실패: " + e.getMessage());
            postDTO.setFiles(null);
        }
        
        return postDTO;
    }

    public PostDTO addTargetClass(PostDTO postDTO) {
        if (postDTO == null || postDTO.getTarget_ids() == null || postDTO.getTarget_ids().size() == 0) {
            return postDTO;
        }

        // 파일 정보를 추가로 등록
//        List<ClassVO> classVOS = classService.findByIds(postDTO.getTarget_ids());
//        if(classVOS.size() > 0){
//            postDTO.setClassList(classVOS);
//        }
        return postDTO;
    }


}
