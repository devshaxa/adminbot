package uz.softex.adminbot.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.softex.adminbot.model.Employee;
import uz.softex.adminbot.util.FileUploadUtil;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class RController {

    @PostMapping("message/pic")
    public String savePicMessage(@RequestParam(required = false) MultipartFile file) throws IOException {
        FileUploadUtil.saveFile("pic/", file.getOriginalFilename(), file);
        return file.getOriginalFilename();
    }
}
