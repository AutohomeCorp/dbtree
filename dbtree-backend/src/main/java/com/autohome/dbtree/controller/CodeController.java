package com.autohome.dbtree.controller;

import com.autohome.dbtree.config.MybatisCodeGeneratorConfig;
import com.autohome.dbtree.contract.Protocol;
import com.autohome.dbtree.service.impl.MyBatisCodeGenerateService;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
@RequestMapping("/code")
public class CodeController {

    @Resource
    private MybatisCodeGeneratorConfig mybatisCodeGeneratorConfig;

    @Resource
    private MyBatisCodeGenerateService myBatisCodeGenerateService;

    @RequestMapping(value = "/mybatisDownload", method = RequestMethod.GET)
    public ResponseEntity<org.springframework.core.io.Resource> mybatisDownload(@RequestParam(value = "zipFile", required = true) String zipFile) throws IOException {
        File file = new File(mybatisCodeGeneratorConfig.getMybatisBaseFolder() + File.separator + zipFile);
        Path path = Paths.get(file.getAbsolutePath());
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=" + zipFile);
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);
    }

    @ResponseBody
    @RequestMapping(value = "/generate", method = RequestMethod.GET)
    public Protocol<String> generate(String dbName, String domainPackage, String mapperPackage, Boolean useActualColumnNames, String tables) {
        List<String> tableList = Splitter.on(",").splitToList(tables);
        String fileName = myBatisCodeGenerateService.execute(domainPackage, mapperPackage, dbName, tableList, useActualColumnNames);
        if(Strings.isNullOrEmpty(fileName)) {
            return new Protocol<>(501, "代码生成失败");
        }
        return new Protocol<>(fileName);
    }
}
