package com.hcmute.prse_be.service;

import com.hcmute.prse_be.config.Config;
import com.hcmute.prse_be.constants.Constant;
import com.hcmute.prse_be.constants.ErrorMsg;
import com.hcmute.prse_be.entity.AdminEntity;
import com.hcmute.prse_be.entity.CartEntity;
import com.hcmute.prse_be.entity.StudentEntity;
import com.hcmute.prse_be.repository.AdminRepository;
import com.hcmute.prse_be.repository.CartRepository;
import com.hcmute.prse_be.repository.StudentRepository;
import com.hcmute.prse_be.response.Response;
import com.hcmute.prse_be.util.GenerateUtils;
import net.minidev.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class StudentServiceImpl implements StudentService{

    private final EmailService emailService;
    private final StudentRepository studentRepository;
    private final AdminRepository adminRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final CartRepository cartRepository;


    public StudentServiceImpl(EmailService emailService, StudentRepository studentRepository, AdminRepository adminRepository, BCryptPasswordEncoder passwordEncoder, CartRepository cartRepository) {
        this.emailService = emailService;
        this.studentRepository = studentRepository;
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
        this.cartRepository = cartRepository;
    }

    @Override
    public StudentEntity findById(Long id) {
        return studentRepository.findById(id).orElse(null);
    }

    @Override
    public StudentEntity findByUsername(String username) {
        return studentRepository.findByUsername(username);
    }

    @Override
    public boolean existsByUsername(String username) {
        return studentRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return studentRepository.existsByEmail(email) || adminRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        return studentRepository.existsByPhoneNumber(phoneNumber);
    }

    @Override
    public StudentEntity register(StudentEntity studentEntity) {

        if(!validateNewStudent(studentEntity)) return null;


        // set passworx
        studentEntity.setPasswordHash(passwordEncoder.encode(studentEntity.getPasswordHash()));

        // set some active
        studentEntity.setIsActive(false);
        studentEntity.setIsBlocked(false);

        // set active code
        studentEntity.setActiveCode(GenerateUtils.generateUUID());

        // set point and money
        studentEntity.setMoney(0.0);
        studentEntity.setPoint(10.0);

        // set avatar url
        studentEntity.setAvatarUrl(Constant.DEFAULT_AVATAR_URL);


        sendActiveEmail(studentEntity.getEmail(), studentEntity.getActiveCode());

        studentRepository.save(studentEntity);

        return studentEntity;
    }

    @Override
    public JSONObject activeAccount(String email, String activeCode) {

        JSONObject response = new JSONObject();

        try {
            StudentEntity student = studentRepository.findByEmail(email);

            if(student == null)
            {
                return Response.error(ErrorMsg.STUDENT_EMAIL_NOT_EXIST);
            }

            if(student.getIsBlocked())
            {
                return Response.error(ErrorMsg.ACCOUNT_BLOCKED);
            }

            if(student.getIsActive())
            {
                return Response.error(ErrorMsg.ACCOUNT_ACTIVATED);
            }

            if(!Objects.equals(student.getActiveCode(), activeCode))
            {
                return Response.error(ErrorMsg.WRONG_ACTIVATE_CODE);
            }


            student.setIsActive(true);
            student = studentRepository.save(student);

            // tao cart
            CartEntity cartEntity = new CartEntity();
            cartEntity.setStudentId(student.getId());

            // save
            student = studentRepository.save(student);
            cartRepository.save(cartEntity);


            response.put("student", student);

            return Response.success(response);

        }catch(Exception e)
        {
            return Response.error(ErrorMsg.SOMETHING_WENT_WRONG);
        }
    }

    @Override
    public void sendActiveEmail(String email, String activeCode)
    {
        String subject = "Kích hoạt tài khoản của bạn tại EasyEdu";
        String text = "Vui lòng sử dụng mã sau để kich hoạt cho tài khoản <"+email+">:<html><body><br/><h1>"+activeCode+"</h1></body></html>";
        text+="<br/> Click vào đường link để kích hoạt tài khoản: ";
        String front_end_host = Config.getParam("spring_boot", "front_end_host");
        String url = front_end_host+"/activate/"+email+"/"+activeCode;
        text+=("<br/> <a href="+url+">"+url+"</a> ");
        emailService.sendMessage("easyeduwebsite@gmail.com", email, subject, text);
    }

    @Override
    public void saveAvatarStudent(String urlAvatar, String username) {
        StudentEntity student = studentRepository.findByUsername(username);
        if(student != null){
            student.setAvatarUrl(urlAvatar);
            studentRepository.save(student);
        }
    }

    @Override
    public long getCountStudent() {
        return studentRepository.count();
    }


    @Override
    public long countByYearAndMonth(int currentYear, int currentMonth) {
        return studentRepository.countByYearAndMonth(currentYear, currentMonth);
    }

    @Override
    public Page<StudentEntity> findAllWithFilters(String search, String status, String role, int page, int size) {
        // Convert string filters to Boolean
        Boolean statusFilter = status.equals("ALL") ? null : status.equals("ACTIVE");
        Boolean roleFilter = role.equals("ALL") ? null : role.equals("INSTRUCTOR");

        // Create pageable object
        Pageable pageable = PageRequest.of(page, size);

        try {
            return studentRepository.findAllWithFilters(
                    search.trim().isEmpty() ? null : search.trim(),
                    statusFilter,
                    roleFilter,
                    pageable
            );
        } catch (Exception e) {
            LogService.getgI().info("Error when finding students with filters");
            return null;
        }
    }

    @Override
    public StudentEntity save(StudentEntity studentEntity) {
        return studentRepository.save(studentEntity);
    }

    @Override
    public boolean updatePassword(String newPassword, String username) {
        try{
            StudentEntity student = studentRepository.findByUsername(username);
            if(student == null)
                return false;
            student.setPasswordHash(passwordEncoder.encode(newPassword));
            studentRepository.save(student);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isMatch(String oldPassword, String currentPassword) {
        return passwordEncoder.matches(oldPassword,currentPassword);
    }


    private boolean validateNewStudent(StudentEntity studentEntity) {

        return
                !studentRepository.existsByUsername(studentEntity.getUsername()) &&
                !studentRepository.existsByEmail(studentEntity.getEmail());
    }
}
