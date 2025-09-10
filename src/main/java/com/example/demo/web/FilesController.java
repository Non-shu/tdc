package com.example.demo.web;

import java.nio.file.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.example.demo.repository.mybatis.ApprovalAttachmentMapper;
import com.example.demo.domain.ApprovalAttachmentVO;

@RestController
@RequestMapping("/files")
public class FilesController {

  private final ApprovalAttachmentMapper attachmentMapper;

  public FilesController(ApprovalAttachmentMapper attachmentMapper) {
    this.attachmentMapper = attachmentMapper;
  }

  @Value("${file.upload.dir:${file.upload-dir:/var/app/uploads/approval}}")
  private String uploadRoot;

  private Path resolveFile(ApprovalAttachmentVO meta) throws Exception {
    Path p = Paths.get(meta.getPath());
    if (!p.isAbsolute()) { // 혹시 상대경로로 저장되어 있으면 루트 기준으로 보정
      p = Paths.get(uploadRoot).toAbsolutePath().normalize().resolve(p).normalize();
    }
    if (!Files.exists(p)) throw new NoSuchFileException(p.toString());
    return p;
  }

  @GetMapping("/download/{attId}")
  public ResponseEntity<Resource> download(@PathVariable long attId) throws Exception {
    ApprovalAttachmentVO meta = attachmentMapper.findMeta(attId);
    if (meta == null) return ResponseEntity.notFound().build();

    Path file = resolveFile(meta);
    Resource res = new UrlResource(file.toUri());
    String ct = Files.probeContentType(file);
    if (ct == null) ct = "application/octet-stream";

    ContentDisposition cd = ContentDisposition.attachment()
        .filename(meta.getFilename(), java.nio.charset.StandardCharsets.UTF_8)
        .build();

    return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType(ct))
        .header(HttpHeaders.CONTENT_DISPOSITION, cd.toString())
        .body(res);
  }

  @GetMapping("/inline/{attId}")
  public ResponseEntity<Resource> inline(@PathVariable long attId) throws Exception {
    ApprovalAttachmentVO meta = attachmentMapper.findMeta(attId);
    if (meta == null) return ResponseEntity.notFound().build();

    Path file = resolveFile(meta);
    Resource res = new UrlResource(file.toUri());
    String ct = Files.probeContentType(file);
    if (ct == null) ct = "application/octet-stream";

    ContentDisposition cd = ContentDisposition.inline()
        .filename(meta.getFilename(), java.nio.charset.StandardCharsets.UTF_8)
        .build();

    return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType(ct))
        .header(HttpHeaders.CONTENT_DISPOSITION, cd.toString())
        .body(res);
  }
}
