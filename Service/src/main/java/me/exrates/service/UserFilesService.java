package me.exrates.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface UserFilesService {

    List<MultipartFile> reduceInvalidFiles(MultipartFile[] files);

    boolean checkFileValidity(MultipartFile file);

    void createUserFiles(int userId, List<MultipartFile> files) throws IOException;

    String saveReceiptScan(int userId, int invoiceId, MultipartFile file) throws IOException;

    String createUserAvatar(int userId, MultipartFile file) throws IOException;

    void deleteUserFile(String filename, int userId) throws IOException;

	Path getUserFilePath(String filename, int userId) throws IOException;
}
