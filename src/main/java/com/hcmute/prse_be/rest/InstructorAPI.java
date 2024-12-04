package com.hcmute.prse_be.rest;

import com.hcmute.prse_be.constants.ApiPaths;
import com.hcmute.prse_be.constants.ErrorMsg;
import com.hcmute.prse_be.constants.ImageFolderName;
import com.hcmute.prse_be.dtos.*;
import com.hcmute.prse_be.entity.*;
import com.hcmute.prse_be.request.*;
import com.hcmute.prse_be.response.Response;
import com.hcmute.prse_be.service.*;
import com.hcmute.prse_be.util.ConvertUtils;
import com.hcmute.prse_be.util.JsonUtils;
import net.minidev.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/instructor")
public class InstructorAPI {

    private final StudentService studentService;

    private final InstructorService instructorService;

    private final CloudinaryService cloudinaryService;

    private final CourseService courseService;

    private final WebSocketService webSocketService;


    public InstructorAPI(StudentService studentService, InstructorService instructorService, CloudinaryService cloudinaryService, CourseService courseService, WebSocketService webSocketService) {
        this.studentService = studentService;
        this.instructorService = instructorService;
        this.cloudinaryService = cloudinaryService;
        this.courseService = courseService;
        this.webSocketService = webSocketService;
    }


    @GetMapping(ApiPaths.GET_PROFILE)
    public ResponseEntity<JSONObject> getProfile(Authentication authentication) {
        try {

            String username = authentication.getName();

            StudentEntity student = studentService.findByUsername(username);
            if (student == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Response.error("Không tìm thấy thông tin người dùng"));
            }

            InstructorEntity instructor = instructorService.getInstructorByStudentId(student.getId());

            if (instructor == null || !instructor.getIsActive()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Response.error("Không tìm thấy thông tin giáo viên"));
            }

            JSONObject response = new JSONObject();
            response.put("instructor", instructor);
            return ResponseEntity.ok(Response.success(response));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Response.error(ErrorMsg.SOMETHING_WENT_WRONG));
        }
    }

    @GetMapping("/courses")
    public ResponseEntity<JSONObject> getCourses(Authentication authentication) {
        try {

            String username = authentication.getName();

            StudentEntity student = studentService.findByUsername(username);
            if (student == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Response.error("Không tìm thấy thông tin người dùng"));
            }

            InstructorEntity instructor = instructorService.getInstructorByStudentId(student.getId());

            if (instructor == null || !instructor.getIsActive()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Response.error("Không tìm thấy thông tin giáo viên"));
            }

            List<CourseEntity> courses = courseService.getCoursesByInstructorId(instructor.getId());

            JSONObject response = new JSONObject();
            response.put("courses", courses);
            return ResponseEntity.ok(Response.success(response));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Response.error(ErrorMsg.SOMETHING_WENT_WRONG));
        }
    }

    @GetMapping("/revenue")
    public ResponseEntity<JSONObject> getRevenueStatistics(
            @RequestParam(defaultValue = "6") int monthsCount,
            Authentication authentication
    ) {
        try {
            // Verify student
            String username = authentication.getName();
            StudentEntity student = studentService.findByUsername(username);
            if (student == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Response.error("Không tìm thấy thông tin người dùng"));
            }

            // Verify instructor
            InstructorEntity instructor = instructorService.getInstructorByStudentId(student.getId());
            if (instructor == null || !instructor.getIsActive()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Response.error("Không tìm thấy thông tin giáo viên"));
            }

            // Get revenue statistics
            List<RevenueStatisticsDTO> statistics = instructorService.getRevenueStatistics(
                    instructor.getId(),
                    monthsCount
            );

            // Build response
            JSONObject response = new JSONObject();
            response.put("revenue_statistics", statistics);

            return ResponseEntity.ok(Response.success(response));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Response.error(ErrorMsg.SOMETHING_WENT_WRONG));
        }
    }

    @GetMapping("/recent-enrollments")
    public ResponseEntity<?> getRecentEnrollments(Authentication authentication) {
        try {
            String username = authentication.getName();
            StudentEntity student = studentService.findByUsername(username);
            if (student == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Response.error("Không tìm thấy thông tin người dùng"));
            }

            InstructorEntity instructor = instructorService.getInstructorByStudentId(student.getId());
            if (instructor == null || !instructor.getIsActive()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Response.error("Không tìm thấy thông tin giáo viên"));
            }

            List<RecentEnrollmentDTO> enrollments = instructorService.getRecentEnrollments(instructor.getId());

            JSONObject response = new JSONObject();
            response.put("recent_enrollments", enrollments);
            return ResponseEntity.ok(Response.success(response));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Response.error(ErrorMsg.SOMETHING_WENT_WRONG));
        }
    }

    @PostMapping("/upload-course")
    public ResponseEntity<JSONObject> uploadCourse(
            @RequestParam("image") MultipartFile image,
            @RequestParam("data") String data,
            Authentication authentication) {
        try {

            String username = authentication.getName();
            StudentEntity student = studentService.findByUsername(username);
            if (student == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Response.error("Không tìm thấy thông tin người dùng"));
            }

            InstructorEntity instructor = instructorService.getInstructorByStudentId(student.getId());
            if (instructor == null || !instructor.getIsActive()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Response.error("Không tìm thấy thông tin giáo viên"));
            }

            // Xử lý dữ liệu form
            CourseFormDataRequest courseFormData = JsonUtils.DeSerialize(data, CourseFormDataRequest.class);

            // tao khoa hoc
            CourseEntity course = courseService.createCourse(courseFormData, instructor);

            // upload anh
            String imagePath =
                    cloudinaryService.uploadImage(image, ImageFolderName.COURSE+"/"+course.getId());


            // luu anh
            course.setImageUrl(imagePath);
            courseService.saveCourse(course);

            // gui thong bao cho giao vien
            // ban socket cho giáo viên
            WebSocketMessage message = WebSocketMessage.uploadStarted(
                    "Khóa học: " + course.getTitle(),
                    "Preview video đang được upload..."

            );
            webSocketService.sendToInstructor(instructor.getId(), "/uploads", message);

            JSONObject response = new JSONObject();
            response.put("course", course);


            return ResponseEntity.ok(Response.success(response));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.error("Có lỗi xảy ra khi tải lên khóa học"));
        }
    }

    @PostMapping("/upload-preview-video")
    public ResponseEntity<JSONObject> uploadVideo(@RequestParam("video") MultipartFile file,
                                                  @RequestParam("courseId") String courseId,
                                                  Authentication authentication) {
        try {
            LogService.getgI().info("Uploading video... : uploadVideo");
            String username = authentication.getName();
            StudentEntity student = studentService.findByUsername(username);
            if (student == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Response.error("Không tìm thấy thông tin người dùng"));
            }

            InstructorEntity instructor = instructorService.getInstructorByStudentId(student.getId());
            if (instructor == null || !instructor.getIsActive()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Response.error("Không tìm thấy thông tin giáo viên"));
            }

            // Lấy thông tin khóa học
            CourseEntity course = courseService.getCourse(ConvertUtils.toLong(courseId));
            if (course == null || !course.getInstructorId().equals(instructor.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Response.error("Không tìm thấy thông tin khóa học"));
            }


            // Tạo một UploadingVideoDetail mới
            String threadId = UUID.randomUUID().toString();
            UploadingVideoDetail uploadStatus = new UploadingVideoDetail(threadId, "PENDING");
            uploadStatus.setInstructorId(instructor.getId());
            uploadStatus.setTitle("Preview video của khóa học: " + course.getTitle());
            uploadStatus.setImageUrl(course.getImageUrl());

            // Lưu vào cache
            UploadingVideoCache.getInstance().getUploadingVideo().put(threadId, uploadStatus);

            // Tên thư mục : course/{courseId}
            String folderName = ImageFolderName.COURSE + "/" + course.getId();
            CompletableFuture.supplyAsync(() -> {
                try {
                    // Thực hiện upload video
                    Map uploadResult = cloudinaryService.uploadVideo(file, folderName);
                    LogService.getgI().info("Video uploaded successfully. URL: " + JsonUtils.Serialize(uploadResult));
                    uploadStatus.setStatus("COMPLETED");

                    String videoUrl = (String) uploadResult.get("secure_url");
                    uploadStatus.setUploadResult(uploadResult);

                    // Cập nhật thông tin khóa học ở đây
                    course.setPreviewVideoUrl(videoUrl);
                    courseService.saveCourse(course);

                    // Gửi thông báo cho giáo viên
                    WebSocketMessage messageSuccess = WebSocketMessage.uploadComplete(
                            "Khóa học: " + course.getTitle(),
                            "Preview video đã được upload thành công"

                    );
                    webSocketService.sendToInstructor(instructor.getId(), "/uploads", messageSuccess);

                    return uploadResult;
                } catch (Exception e) {
                    uploadStatus.setStatus("FAILED");
                    uploadStatus.setErrorMessage(e.getMessage());
                    return null;
                } finally {
                    UploadingVideoCache.getInstance().getUploadingVideo().remove(threadId);
                }
            });



            // Trả về phản hồi ngay lập tức
            JSONObject response = new JSONObject();
            response.put("success", true);
            return ResponseEntity.ok(Response.success(response));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.error("Có lỗi xảy ra khi tải lên khóa học"));
        }
    }


    @GetMapping("/upload-status")
    public ResponseEntity<JSONObject> getUploadStatusesByInstructor(Authentication authentication) {
        try {
            // Kiểm tra thông tin người dùng
            String username = authentication.getName();
            StudentEntity student = studentService.findByUsername(username);
            if (student == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Response.error("Không tìm thấy thông tin người dùng"));
            }

            // Kiểm tra thông tin giảng viên
            InstructorEntity instructor = instructorService.getInstructorByStudentId(student.getId());
            if (instructor == null || !instructor.getIsActive()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Response.error("Không tìm thấy thông tin giáo viên"));
            }

            // Lấy danh sách trạng thái upload của instructor
            UploadingVideoCache cache = UploadingVideoCache.getInstance();
            List<UploadingVideoDetail> statuses = cache.getUploadingVideo().values().stream()
                    .filter(status -> instructor.getId().equals(status.getInstructorId()))
                    .collect(Collectors.toList());

            // Trả về response thành công
            JSONObject response = new JSONObject();
            response.put("uploadStatuses", statuses);
            return ResponseEntity.ok(Response.success(response));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.error("Có lỗi xảy ra khi lấy trạng thái upload"));
        }
    }

    @GetMapping("/courses/{courseId}")
    public ResponseEntity<JSONObject> getCourse(Authentication authentication, @PathVariable("courseId") Long courseId) {
        try {

            Long id = ConvertUtils.toLong(courseId);
            String username = authentication.getName();
            StudentEntity student = studentService.findByUsername(username);
            if (student == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Response.error("Không tìm thấy thông tin người dùng"));
            }

            InstructorEntity instructor = instructorService.getInstructorByStudentId(student.getId());
            if (instructor == null || !instructor.getIsActive()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Response.error("Không tìm thấy thông tin giáo viên"));
            }

            CourseEntity courseEntity = courseService.getCourse(id);
            if(courseEntity == null || !courseEntity.getInstructorId().equals(instructor.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Response.error("Không tìm thấy thông tin khóa học"));
            }

            // Trả về response thành công
            JSONObject response = new JSONObject();
            response.put("course", courseEntity);
            return ResponseEntity.ok(Response.success(response));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.error("Có lỗi xảy ra khi lấy trạng thái upload"));
        }
    }

    @PutMapping("/courses/{courseId}")
    public ResponseEntity<JSONObject> updateCourse(Authentication authentication,
                                                   @PathVariable("courseId") Long courseId,
                                                   @RequestBody UpdateCourseRequest updateCourseRequest) {
        try {

            Long id = ConvertUtils.toLong(courseId);
            String username = authentication.getName();
            StudentEntity student = studentService.findByUsername(username);
            if (student == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Response.error("Không tìm thấy thông tin người dùng"));
            }

            InstructorEntity instructor = instructorService.getInstructorByStudentId(student.getId());
            if (instructor == null || !instructor.getIsActive()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Response.error("Không tìm thấy thông tin giáo viên"));
            }

            CourseEntity courseEntity = courseService.getCourse(id);
            if(courseEntity == null || !courseEntity.getInstructorId().equals(instructor.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Response.error("Không tìm thấy thông tin khóa học"));
            }

            courseEntity.setTitle(updateCourseRequest.getCourse().getTitle());
            courseEntity.setDescription(updateCourseRequest.getCourse().getDescription());
            courseEntity.setShortDescription(updateCourseRequest.getCourse().getShortDescription());
            courseEntity.setImageUrl(updateCourseRequest.getCourse().getImageUrl());
            courseEntity.setLanguage(updateCourseRequest.getCourse().getLanguage());
            courseEntity.setIsPublish(updateCourseRequest.getCourse().getIsPublish());
            courseEntity.setPreviewVideoUrl(updateCourseRequest.getCourse().getPreviewVideoUrl());
            courseEntity.setPreviewVideoDuration(updateCourseRequest.getCourse().getPreviewVideoDuration());
            courseEntity.setOriginalPrice(updateCourseRequest.getCourse().getOriginalPrice());
            courseService.saveCourse(courseEntity);
            // Trả về response thành công
            JSONObject response = new JSONObject();
            response.put("course", courseEntity);
            return ResponseEntity.ok(Response.success(response));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.error("Có lỗi xảy ra khi lấy trạng thái upload"));
        }
    }

    @GetMapping("/courses/{courseId}/curriculum")
    public ResponseEntity<JSONObject> getCurriculum(Authentication authentication, @PathVariable("courseId") Long courseId) {
        try {

            Long id = ConvertUtils.toLong(courseId);
            String username = authentication.getName();
            StudentEntity student = studentService.findByUsername(username);
            if (student == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Response.error("Không tìm thấy thông tin người dùng"));
            }

            InstructorEntity instructor = instructorService.getInstructorByStudentId(student.getId());
            if (instructor == null || !instructor.getIsActive()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Response.error("Không tìm thấy thông tin giáo viên"));
            }

            CourseEntity courseEntity = courseService.getCourse(id);
            if(courseEntity == null || !courseEntity.getInstructorId().equals(instructor.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Response.error("Không tìm thấy thông tin khóa học"));
            }

            List<ChapterEntity> chapters = courseService.getChaptersByCourseId(id);


            // create chapter id
            List<ChapterInstructorEditDTO> chapterInstructorEditDTOS  = new ArrayList<>();

            for (ChapterEntity chapter : chapters) {
                ChapterInstructorEditDTO chapterInstructorEditDTO = new ChapterInstructorEditDTO();
                chapterInstructorEditDTO.setId(chapter.getId());
                chapterInstructorEditDTO.setTitle(chapter.getTitle());
                chapterInstructorEditDTO.setOrderIndex(chapter.getOrderIndex());
                chapterInstructorEditDTOS.add(chapterInstructorEditDTO);
            }

            // find lesson by chapter
            for (ChapterInstructorEditDTO chapterInstructorEditDTO : chapterInstructorEditDTOS) {
                List<LessonInstructorEditDTO> lessonInstructorEditDTOS = new ArrayList<>();
                for (ChapterEntity chapter : chapters) {
                    if (chapter.getId().equals(chapterInstructorEditDTO.getId())) {
                        List<LessonEntity> lessons = courseService.getAllLessonByChapterId(chapter.getId());
                        for (LessonEntity lesson : lessons) {
                            LessonInstructorEditDTO lessonInstructorEditDTO = new LessonInstructorEditDTO();
                            lessonInstructorEditDTO.setId(lesson.getId());
                            lessonInstructorEditDTO.setTitle(lesson.getTitle());
                            lessonInstructorEditDTO.setType(lesson.getType());
                            lessonInstructorEditDTO.setPublish(lesson.getIsPublish());
                            lessonInstructorEditDTO.setOrderIndex(lesson.getOrderIndex());
                            lessonInstructorEditDTOS.add(lessonInstructorEditDTO);
                        }
                    }
                }
                chapterInstructorEditDTO.setLessons(lessonInstructorEditDTOS);
            }
            // Trả về response thành công
            JSONObject response = new JSONObject();
            response.put("chapters", chapterInstructorEditDTOS);
            return ResponseEntity.ok(Response.success(response));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.error("Có lỗi xảy ra khi lấy trạng thái upload"));
        }
    }

    @GetMapping("/courses/{courseId}/chapter/{chapterId}")
    public ResponseEntity<JSONObject> getChapterInfo(Authentication authentication, @PathVariable("courseId") Long courseId, @PathVariable("chapterId") Long chapterId) {
        try {
            String username = authentication.getName();
            StudentEntity student = studentService.findByUsername(username);
            if (student == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Response.error("Không tìm thấy thông tin người dùng"));
            }

            InstructorEntity instructor = instructorService.getInstructorByStudentId(student.getId());
            if (instructor == null || !instructor.getIsActive()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Response.error("Không tìm thấy thông tin giáo viên"));
            }

            CourseEntity courseEntity = courseService.getCourse(courseId);
            if(courseEntity == null || !courseEntity.getInstructorId().equals(instructor.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Response.error("Không tìm thấy thông tin khóa học"));
            }

            ChapterEntity chapterEntity = courseService.getChapterById(chapterId);

            if(chapterEntity == null || !chapterEntity.getCourseId().equals(courseId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Response.error("Không tìm thấy thông tin chương"));
            }

            ChapterInstructorEditDTO chapterInstructorEditDTO = new ChapterInstructorEditDTO();
            chapterInstructorEditDTO.setId(chapterEntity.getId());
            chapterInstructorEditDTO.setTitle(chapterEntity.getTitle());
            chapterInstructorEditDTO.setOrderIndex(chapterEntity.getOrderIndex());

            List<LessonInstructorEditDTO> lessonInstructorEditDTOS = new ArrayList<>();
            List<LessonEntity> lessons = courseService.getAllLessonByChapterId(chapterId);
            for (LessonEntity lesson : lessons) {
                LessonInstructorEditDTO lessonInstructorEditDTO = new LessonInstructorEditDTO();
                lessonInstructorEditDTO.setId(lesson.getId());
                lessonInstructorEditDTO.setTitle(lesson.getTitle());
                lessonInstructorEditDTO.setType(lesson.getType());
                lessonInstructorEditDTO.setPublish(lesson.getIsPublish());
                lessonInstructorEditDTO.setOrderIndex(lesson.getOrderIndex());
                lessonInstructorEditDTOS.add(lessonInstructorEditDTO);
            }
            chapterInstructorEditDTO.setLessons(lessonInstructorEditDTOS);

            // Trả về response thành công
            JSONObject response = new JSONObject();
            response.put("chapter", chapterInstructorEditDTO);
            return ResponseEntity.ok(Response.success(response));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.error("Có lỗi xảy ra khi lấy trạng thái upload"));
        }
    }

    @GetMapping("/courses/{courseId}/chapter/{chapterId}/lesson/{lessonId}")
    public ResponseEntity<JSONObject> getLessonInfo(Authentication authentication, @PathVariable("courseId") Long courseId, @PathVariable("chapterId") Long chapterId, @PathVariable("lessonId") Long lessonId){
        try {
            String username = authentication.getName();
            StudentEntity student = studentService.findByUsername(username);
            if (student == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Response.error("Không tìm thấy thông tin người dùng"));
            }

            InstructorEntity instructor = instructorService.getInstructorByStudentId(student.getId());
            if (instructor == null || !instructor.getIsActive()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Response.error("Không tìm thấy thông tin giáo viên"));
            }

            CourseEntity courseEntity = courseService.getCourse(courseId);
            if(courseEntity == null || !courseEntity.getInstructorId().equals(instructor.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Response.error("Không tìm thấy thông tin khóa học"));
            }

            ChapterEntity chapterEntity = courseService.getChapterById(chapterId);

            if(chapterEntity == null || !chapterEntity.getCourseId().equals(courseId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Response.error("Không tìm thấy thông tin chương"));
            }


            List<LessonEntity> lessons = courseService.getAllLessonByChapterId(chapterId);
            for (LessonEntity lesson : lessons) {
                if (lesson.getId().equals(lessonId)) {
                    JSONObject response = new JSONObject();
                    response.put("lesson", lesson);
                    return ResponseEntity.ok(Response.success(response));
                }
            }

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Response.error("Không tìm thấy thông tin"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.error("Có lỗi xảy ra khi lấy trạng thái upload"));
        }
    }

    @PutMapping("/courses/{courseId}/chapter/{chapterId}/lesson/{lessonId}")
    public ResponseEntity<JSONObject> changeIsPublish(Authentication authentication, @PathVariable("courseId") Long courseId, @PathVariable("chapterId") Long chapterId, @PathVariable("lessonId") Long lessonId){
        try {
            String username = authentication.getName();
            StudentEntity student = studentService.findByUsername(username);
            if (student == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Response.error("Không tìm thấy thông tin người dùng"));
            }

            InstructorEntity instructor = instructorService.getInstructorByStudentId(student.getId());
            if (instructor == null || !instructor.getIsActive()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Response.error("Không tìm thấy thông tin giáo viên"));
            }

            CourseEntity courseEntity = courseService.getCourse(courseId);
            if(courseEntity == null || !courseEntity.getInstructorId().equals(instructor.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Response.error("Không tìm thấy thông tin khóa học"));
            }

            ChapterEntity chapterEntity = courseService.getChapterById(chapterId);

            if(chapterEntity == null || !chapterEntity.getCourseId().equals(courseId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Response.error("Không tìm thấy thông tin chương"));
            }


            List<LessonEntity> lessons = courseService.getAllLessonByChapterId(chapterId);
            for (LessonEntity lesson : lessons) {
                if (lesson.getId().equals(lessonId)) {
                    JSONObject response = new JSONObject();
                    lesson.setIsPublish(!lesson.getIsPublish());
                    courseService.saveLesson(lesson);
                    response.put("lesson", lesson);
                    return ResponseEntity.ok(Response.success(response));
                }
            }

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Response.error("Không tìm thấy thông tin"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.error("Có lỗi xảy ra khi lấy trạng thái upload"));
        }
    }

    @GetMapping("/courses/{courseId}/chapter/{chapterId}/lesson/{lessonId}/video")
    public ResponseEntity<JSONObject> getVideoLessonInfo(Authentication authentication, @PathVariable("courseId") Long courseId, @PathVariable("chapterId") Long chapterId, @PathVariable("lessonId") Long lessonId){
        try {
            String username = authentication.getName();
            StudentEntity student = studentService.findByUsername(username);
            if (student == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Response.error("Không tìm thấy thông tin người dùng"));
            }

            InstructorEntity instructor = instructorService.getInstructorByStudentId(student.getId());
            if (instructor == null || !instructor.getIsActive()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Response.error("Không tìm thấy thông tin giáo viên"));
            }

            CourseEntity courseEntity = courseService.getCourse(courseId);
            if(courseEntity == null || !courseEntity.getInstructorId().equals(instructor.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Response.error("Không tìm thấy thông tin khóa học"));
            }

            ChapterEntity chapterEntity = courseService.getChapterById(chapterId);

            if(chapterEntity == null || !chapterEntity.getCourseId().equals(courseId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Response.error("Không tìm thấy thông tin chương"));
            }


            List<LessonEntity> lessons = courseService.getAllLessonByChapterId(chapterId);
            for (LessonEntity lesson : lessons) {
                if (lesson.getId().equals(lessonId)) {
                    JSONObject response = new JSONObject();
                    VideoLessonEntity videoLessonEntity = courseService.getVideoLesson(courseId, chapterId, lessonId);
                    response.put("video", videoLessonEntity);
                    return ResponseEntity.ok(Response.success(response));
                }
            }

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Response.error("Không tìm thấy thông tin"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.error("Có lỗi xảy ra khi lấy trạng thái upload"));
        }
    }

    @PostMapping("/courses/{courseId}/chapter/{chapterId}/lessons")
    public ResponseEntity<JSONObject> addNewLesson(Authentication authentication, @PathVariable("courseId") Long courseId, @PathVariable("chapterId") Long chapterId, @RequestBody AddNewLessonRequest addLessonRequest) {
        try {
            String username = authentication.getName();
            StudentEntity student = studentService.findByUsername(username);
            if (student == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Response.error("Không tìm thấy thông tin người dùng"));
            }

            InstructorEntity instructor = instructorService.getInstructorByStudentId(student.getId());
            if (instructor == null || !instructor.getIsActive()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Response.error("Không tìm thấy thông tin giáo viên"));
            }

            CourseEntity courseEntity = courseService.getCourse(courseId);
            if(courseEntity == null || !courseEntity.getInstructorId().equals(instructor.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Response.error("Không tìm thấy thông tin khóa học"));
            }

            ChapterEntity chapterEntity = courseService.getChapterById(chapterId);

            if(chapterEntity == null || !chapterEntity.getCourseId().equals(courseId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Response.error("Không tìm thấy thông tin chương"));
            }


            List<LessonEntity> lessons = courseService.getLessonsByChapterId(chapterId);

            LessonEntity lessonEntity = new LessonEntity();
            lessonEntity.setTitle(addLessonRequest.getLesson().getTitle());
            lessonEntity.setType(addLessonRequest.getLesson().getType());
            lessonEntity.setChapterId(chapterId);
            lessonEntity.setOrderIndex(lessons.size() + 1);
            lessonEntity.setIsPublish(addLessonRequest.getLesson().isPublish());
            courseService.saveLesson(lessonEntity);
            // Trả về response thành công
            return ResponseEntity.ok(Response.success());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.error("Có lỗi xảy ra khi"));
        }
    }

    @PostMapping("/courses/{courseId}/chapter/{chapterId}/lesson/{lessonId}/video/upload")
    public ResponseEntity<JSONObject> uploadLessonVideo(
            @RequestParam("video") MultipartFile file,
            @PathVariable Long courseId,
            @PathVariable Long chapterId,
            @PathVariable Long lessonId,
            Authentication authentication) {
        try {
            LogService.getgI().info("[InstructorAPI] uploadLessonVideo...");
            String username = authentication.getName();
            StudentEntity student = studentService.findByUsername(username);
            if (student == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Response.error("Không tìm thấy thông tin người dùng"));
            }

            InstructorEntity instructor = instructorService.getInstructorByStudentId(student.getId());
            if (instructor == null || !instructor.getIsActive()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Response.error("Không tìm thấy thông tin giáo viên"));
            }

            // Validate course ownership
            CourseEntity course = courseService.getCourse(ConvertUtils.toLong(courseId));
            if (course == null || !course.getInstructorId().equals(instructor.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Response.error("Không có quyền truy cập khóa học này"));
            }

            // Validate chapter
            ChapterEntity chapterEntity = courseService.getChapterById(chapterId);
            if(chapterEntity == null || !chapterEntity.getCourseId().equals(courseId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Response.error("Không tìm thấy thông tin chương"));
            }

            List<LessonEntity> lessons = courseService.getAllLessonByChapterId(chapterId);
            for (LessonEntity lesson : lessons) {
                if (lesson.getId().equals(lessonId)) {
                    JSONObject response = new JSONObject();

                    // lay ra duoc video lesson roi => bat dau luu tru
                    VideoLessonEntity videoLessonEntity = courseService.getVideoLesson(courseId, chapterId, lessonId);

                    if(videoLessonEntity == null) {
                        videoLessonEntity = new VideoLessonEntity();
                    }

                    // Check if video is already uploaded
                    String threadId = UUID.randomUUID().toString();
                    UploadingVideoDetail uploadStatus = new UploadingVideoDetail(threadId, "PENDING");
                    uploadStatus.setInstructorId(instructor.getId());
                    uploadStatus.setTitle("Video cho bài học: " + lesson.getTitle());


                    // save to cache
                    UploadingVideoCache.getInstance().getUploadingVideo().put(threadId, uploadStatus);

                    // Folder structure: course/{courseId}/chapter/{chapterId}/lesson/{lessonId}
                    String folderName = String.format("%s/%s/chapter/%s/lesson/%s",
                            ImageFolderName.COURSE,
                            course.getId(),
                            chapterId,
                            lessonId);

                    LogService.getgI().info("Uploading video... : " + folderName);

                    VideoLessonEntity finalVideoLessonEntity = videoLessonEntity;
                    CompletableFuture.supplyAsync(() -> {
                        try {
                            // Thực hiện upload video
                            Map uploadResult = cloudinaryService.uploadVideo(file, folderName);
                            uploadStatus.setStatus("COMPLETED");

                            String videoUrl = (String) uploadResult.get("secure_url");
                            uploadStatus.setUploadResult(uploadResult);

                            // Cập nhật thông tin khóa học ở đây
                            finalVideoLessonEntity.setLessonId(lessonId);
//
                            finalVideoLessonEntity.setDuration(ConvertUtils.toDouble(uploadResult.get("duration")));

                            finalVideoLessonEntity.setVideoUrl(videoUrl);
                            courseService.saveVideoLesson(finalVideoLessonEntity);

                            // Gửi thông báo cho giáo viên
                            WebSocketMessage messageSuccess = WebSocketMessage.uploadComplete(
                                    "Khóa học: " + course.getTitle(),
                                    "Video "+lesson.getTitle()+" đã được upload thành công"

                            );
                            webSocketService.sendToInstructor(instructor.getId(), "/uploads", messageSuccess);

                            return uploadResult;
                        } catch (Exception e) {
                            uploadStatus.setStatus("FAILED");
                            uploadStatus.setErrorMessage(e.getMessage());
                            return null;
                        } finally {
                            UploadingVideoCache.getInstance().getUploadingVideo().remove(threadId);
                        }
                    });
                    return ResponseEntity.ok(Response.success(response));
                }
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Response.error("Không tìm thấy thông tin"));
        } catch (Exception e) {
            LogService.getgI().error(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.error("Có lỗi xảy ra khi tải lên video"));
        }
    }

    @PostMapping("/courses/{courseId}/curriculum/chapters")
    public ResponseEntity<JSONObject> addChapter(
            @PathVariable Long courseId,
            @RequestBody CreateChapterRequest request,
            Authentication authentication) {
        try {
            // Validate user
            String username = authentication.getName();
            StudentEntity student = studentService.findByUsername(username);
            if (student == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Response.error("Không tìm thấy thông tin người dùng"));
            }

            // Validate instructor
            InstructorEntity instructor = instructorService.getInstructorByStudentId(student.getId());
            if (instructor == null || !instructor.getIsActive()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Response.error("Không tìm thấy thông tin giáo viên"));
            }

            // Validate course ownership
            CourseEntity course = courseService.getCourse(courseId);
            if (course == null || !course.getInstructorId().equals(instructor.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Response.error("Không có quyền truy cập khóa học này"));
            }

            // Create new chapter
            ChapterEntity chapter = new ChapterEntity();
            chapter.setCourseId(courseId);
            chapter.setTitle(request.getTitle());
            chapter.setOrderIndex(request.getOrderIndex());

            // Save to database
            ChapterEntity savedChapter = courseService.saveChapter(chapter);

            // Map to response
            JSONObject response = new JSONObject();
            ChapterInstructorEditDTO chapterInstructorEditDTO = new ChapterInstructorEditDTO();
            chapterInstructorEditDTO.setId(savedChapter.getId());
            chapterInstructorEditDTO.setTitle(savedChapter.getTitle());
            chapterInstructorEditDTO.setOrderIndex(savedChapter.getOrderIndex());
            chapterInstructorEditDTO.setLessons(Collections.emptyList());  // Chapter mới không có lessons


            response.put("chapter", chapterInstructorEditDTO);

            return ResponseEntity.ok(Response.success(response));

        } catch (Exception e) {
            LogService.getgI().error(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.error("Có lỗi xảy ra khi thêm chương mới"));
        }
    }

    @PutMapping("/courses/{courseId}/chapter/{chapterId}")
    public ResponseEntity<JSONObject> updateChapter(
            @PathVariable Long courseId,
            @PathVariable Long chapterId,
            @RequestBody UpdateChapterRequest request,
            Authentication authentication) {
        try {
            // Validate user
            String username = authentication.getName();
            StudentEntity student = studentService.findByUsername(username);
            if (student == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Response.error("Không tìm thấy thông tin người dùng"));
            }

            // Validate instructor
            InstructorEntity instructor = instructorService.getInstructorByStudentId(student.getId());
            if (instructor == null || !instructor.getIsActive()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Response.error("Không tìm thấy thông tin giáo viên"));
            }

            // Validate course ownership
            CourseEntity course = courseService.getCourse(courseId);
            if (course == null || !course.getInstructorId().equals(instructor.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Response.error("Không có quyền truy cập khóa học này"));
            }

            // Get and validate chapter
            ChapterEntity chapter = courseService.getChapterById(chapterId);
            if (chapter == null || !chapter.getCourseId().equals(courseId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Response.error("Không tìm thấy thông tin chương"));
            }

            // Update chapter
            chapter.setTitle(request.getChapter().getTitle());
            chapter.setOrderIndex(request.getChapter().getOrderIndex());
            courseService.saveChapter(chapter);
            return ResponseEntity.ok(Response.success());

        } catch (Exception e) {
            LogService.getgI().error(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.error("Có lỗi xảy ra khi cập nhật chương"));
        }
    }

}
