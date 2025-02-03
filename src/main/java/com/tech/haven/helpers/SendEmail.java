//package com.tech.haven.helpers;
//
//import java.io.File;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.core.io.FileSystemResource;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.MimeMessageHelper;
//import org.springframework.stereotype.Service;
//
//import com.tech.haven.exceptions.ResourceNotFoundException;
//import com.tech.haven.repositories.OrderRepository;
//
//import jakarta.mail.MessagingException;
//import jakarta.mail.internet.MimeMessage;
//
//@Service
//public class SendEmail {
//
//    @Autowired
//    private JavaMailSender mailSender;
//
//    @Value("${spring.mail.username}")
//    private String from;
//
//    @Autowired
//    private OrderRepository orderRepo;
//
//    public void sendSimpleEmail(String to, String subject, String content) {
//        SimpleMailMessage msg = new SimpleMailMessage();
//        msg.setFrom(from);
//        msg.setTo(to);
//        msg.setSubject(subject);
//        msg.setText(content);
//        mailSender.send(msg);
//    }
//
//    public void sendEmail(String to, String subject, String content, String userName, String orderId)
//            throws MessagingException, InterruptedException {
//
//        MimeMessage mimeMsg = mailSender.createMimeMessage();
//        MimeMessageHelper helper = new MimeMessageHelper(mimeMsg, true);
//        helper.setFrom(from);
//        helper.setTo(to);
//        helper.setSubject(subject);
//        helper.setText(content);
//
//        String userEmail = orderRepo.findById(orderId).orElseThrow().getUser().getEmail();
//
//        FileSystemResource fileSystemResource = new FileSystemResource(
//                new File("E:\\PDF\\" + userEmail + "\\invoice-" + orderId + ".pdf"));
//        if (fileSystemResource.exists()) {
//            helper.addAttachment("Invoice" + userName + ".pdf", fileSystemResource);
//        } else {
//            throw new ResourceNotFoundException("Invoice pdf not found");
//        }
//        mailSender.send(mimeMsg);
//    }
//}
