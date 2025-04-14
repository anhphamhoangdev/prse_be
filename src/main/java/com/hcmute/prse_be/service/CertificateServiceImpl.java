package com.hcmute.prse_be.service;

import com.hcmute.prse_be.config.Config;
import com.hcmute.prse_be.constants.ImageFolderName;
import com.hcmute.prse_be.entity.CertificateEntity;
import com.hcmute.prse_be.entity.CourseEntity;
import com.hcmute.prse_be.entity.InstructorEntity;
import com.hcmute.prse_be.entity.StudentEntity;
import com.hcmute.prse_be.repository.CertificateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.UUID;


@Service
public class CertificateServiceImpl implements CertificateService {

    private final CertificateRepository certificateRepository;
    private final CloudinaryService cloudinaryService;
    private final String CERTIFICATE_TEMPLATE = Config.getParam("certificate", "base_file");

    @Autowired
    public CertificateServiceImpl(CertificateRepository certificateRepository, CloudinaryService cloudinaryService) {
        this.certificateRepository = certificateRepository;
        this.cloudinaryService = cloudinaryService;
    }

    @Override
    public CertificateEntity createCertificateAndUploadCloudinary(StudentEntity student, CourseEntity course, InstructorEntity instructor) {
        try {
            // Create certificate file name
            String fileName = "certificate_STDID" + student.getId() + "_CID" + course.getId() + ".png";

            // Create certificate image in memory
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            BufferedImage certificateImage = generateCertificateImage(student.getFullName(), course.getTitle(), instructor.getFullName());

            // Write the image to the output stream
            ImageIO.write(certificateImage, "png", baos);

            // Convert to MultipartFile equivalent for Cloudinary upload
            ByteArrayInputStream inputStream = new ByteArrayInputStream(baos.toByteArray());

            // Upload to Cloudinary
            String cloudinaryUrl = cloudinaryService.uploadFile(baos.toByteArray(), ImageFolderName.CERTIFICATE_FOLDER, fileName);

            // Create and save certificate entity
            CertificateEntity certificate = new CertificateEntity();
            certificate.setStudentId(student.getId());
            certificate.setNameInCertificate(student.getFullName());
            certificate.setCourseId(course.getId());
            certificate.setCourseName(course.getTitle());
            certificate.setCertificateUrl(cloudinaryUrl);
            certificate.setCertificatePublicCode(UUID.randomUUID().toString());
            return certificateRepository.save(certificate);

        } catch (Exception e) {
            throw new RuntimeException("Error creating and uploading certificate: " + e.getMessage(), e);
        }
    }

    @Override
    public CertificateEntity getCertificateByStudentAndCourse(Long studentId, Long courseId) {
        return certificateRepository.findByStudentIdAndCourseId(studentId, courseId);
    }

    @Override
    public CertificateEntity getCertificateByPublicCode(String publicCode) {
        return certificateRepository.findByCertificatePublicCode(publicCode);
    }

    /**
     * Generates a certificate image with the student name and course information
     */
    private BufferedImage generateCertificateImage(String studentName, String courseName, String instructorName) {
        try {
            // Read the certificate template
            File templateFile = new File(CERTIFICATE_TEMPLATE);
            if (!templateFile.exists()) {
                throw new IOException("Certificate template file does not exist: " + CERTIFICATE_TEMPLATE);
            }

            BufferedImage certificateTemplate = ImageIO.read(templateFile);
            Graphics2D g2d = certificateTemplate.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            // Add student name (giữ nguyên)
            Font nameFont = new Font("Sansita", Font.BOLD | Font.ITALIC, 100);
            g2d.setFont(nameFont);
            g2d.setColor(new Color(28, 78, 216));
            int textWidth = g2d.getFontMetrics().stringWidth(studentName);
            int x = (certificateTemplate.getWidth() - textWidth) / 2;
            int nameY = (int)(certificateTemplate.getHeight() * 0.51);
            g2d.drawString(studentName, x, nameY);

            // Setup fonts for the rest of the text
            Font normalFont = new Font("Calibri", Font.ITALIC, 34);
            Font boldItalicFont = new Font("Calibri", Font.BOLD | Font.ITALIC, 34);
            g2d.setColor(new Color(28, 78, 216));

            // Khoảng cách lề cố định
            int fixedMargin = 100; // Có thể điều chỉnh
            int availableWidth = certificateTemplate.getWidth() - 2 * fixedMargin;
            int y = (int)(certificateTemplate.getHeight() * 0.6);
            int lineHeight = g2d.getFontMetrics(normalFont).getHeight();

            // Text components
            String part1 = "Has successfully completed the course ";
            String courseText = "'" + courseName + "'";
            String part2 = " instructed by " + instructorName;

            // Tính chiều rộng của từng phần
            g2d.setFont(normalFont);
            FontMetrics fmNormal = g2d.getFontMetrics();
            g2d.setFont(boldItalicFont);
            FontMetrics fmBoldItalic = g2d.getFontMetrics();
            int part1Width = fmNormal.stringWidth(part1);
            int courseTextWidth = fmBoldItalic.stringWidth(courseText);
            int part2Width = fmNormal.stringWidth(part2);
            int fullWidth = part1Width + courseTextWidth + part2Width;

            if (fullWidth <= availableWidth) {
                // Trường hợp fit trong 1 dòng, căn giữa
                int centeredX = fixedMargin + (availableWidth - fullWidth) / 2;
                g2d.setFont(normalFont);
                g2d.drawString(part1, centeredX, y);
                centeredX += part1Width;

                g2d.setFont(boldItalicFont);
                g2d.drawString(courseText, centeredX, y);
                centeredX += courseTextWidth;

                g2d.setFont(normalFont);
                g2d.drawString(part2, centeredX, y);
            } else {
                // Trường hợp cần ngắt dòng, mỗi phần căn giữa
                // Part 1: căn giữa
                g2d.setFont(normalFont);
                int part1CenteredX = fixedMargin + (availableWidth - part1Width) / 2;
                g2d.drawString(part1, part1CenteredX, y);
                y += lineHeight;

                // Course name
                g2d.setFont(boldItalicFont);
                if (courseTextWidth <= availableWidth) {
                    // Course name fit trong 1 dòng, căn giữa
                    int courseCenteredX = fixedMargin + (availableWidth - courseTextWidth) / 2;
                    g2d.drawString(courseText, courseCenteredX, y);
                    y += lineHeight;
                } else {
                    // Course name quá dài, ngắt dòng và căn giữa từng dòng
                    String[] words = courseName.split("\\s+");
                    StringBuilder currentLine = new StringBuilder();

                    for (String word : words) {
                        String testWord = "'" + word + "'";
                        int wordWidth = fmBoldItalic.stringWidth(testWord);
                        String testLine = !currentLine.isEmpty() ?
                                currentLine + " " + testWord : testWord;

                        if (fmBoldItalic.stringWidth(testLine) <= availableWidth) {
                            if (!currentLine.isEmpty()) currentLine.append(" ");
                            currentLine.append(word);
                        } else {
                            if (!currentLine.isEmpty()) {
                                // Vẽ dòng hiện tại, căn giữa
                                String lineText = "'" + currentLine.toString() + "'";
                                int lineWidth = fmBoldItalic.stringWidth(lineText);
                                int centeredX = fixedMargin + (availableWidth - lineWidth) / 2;
                                g2d.drawString(lineText, centeredX, y);
                                y += lineHeight;
                                currentLine = new StringBuilder(word);
                            } else {
                                // Từ quá dài, vẽ và căn giữa
                                String wordText = "'" + word + "'";
                                int wordCenteredX = fixedMargin + (availableWidth - wordWidth) / 2;
                                g2d.drawString(wordText, wordCenteredX, y);
                                y += lineHeight;
                                currentLine = new StringBuilder();
                            }
                        }
                    }

                    // Vẽ dòng cuối của course name nếu có
                    if (!currentLine.isEmpty()) {
                        String lineText = "'" + currentLine.toString() + "'";
                        int lineWidth = fmBoldItalic.stringWidth(lineText);
                        int centeredX = fixedMargin + (availableWidth - lineWidth) / 2;
                        g2d.drawString(lineText, centeredX, y);
                        y += lineHeight;
                    }
                }

                // Part 2: căn giữa
                g2d.setFont(normalFont);
                int part2CenteredX = fixedMargin + (availableWidth - part2Width) / 2;
                g2d.drawString(part2, part2CenteredX, y);
            }

            g2d.dispose();
            return certificateTemplate;

        } catch (IOException e) {
            throw new RuntimeException("Error adding text to certificate: " + e.getMessage(), e);
        }
    }

}